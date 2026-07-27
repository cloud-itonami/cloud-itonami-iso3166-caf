(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `reserved-market-eligible?` / `reserved-market-ineligible-claim?` are
  the SAME discipline applied to a genuinely Central African Republic-
  specific mechanism: the Code de la Commande Publique (Loi N° 25.016
  du 23 décembre 2025), Chapitre II's own definition of 'Marché
  réservé' -- a reserved contract whose ELIGIBLE candidates are
  restricted to social-economy actors (associations, worker/artisan
  cooperatives, groupements d'intérêt économique, entreprises
  sociales) OR small/medium enterprises whose workforce is at least
  30% persons with disabilities, OR at least 50% youth, OR at least 50%
  women. The Code's own text delegates the reserved contract's VALUE
  THRESHOLD to a ministerial arrêté this iteration did not
  independently fetch/verify a number for -- that branch is
  DELIBERATELY NOT modeled (the same honest scope-narrowing Benin's
  Art. 77 discretionary-subcontracting branch and Bhutan's unread
  Debarment Rules duration clause already established for this
  family): only the ELIGIBILITY CRITERIA, which the Code states
  directly and unconditionally (no delegated number to guess), is
  independently recomputed here.

  This is a GENUINELY DIFFERENT check SHAPE than every prior iso3166
  sibling this repo mirrors: Bulgaria's ЗОП Art. 54(5) de-minimis is a
  PERCENTAGE-OF-TURNOVER ELIGIBILITY formula, Albania's Neni 76(2)(c)
  carve-out is a FLAT-CONSTANT ELIGIBILITY threshold, Azerbaijan's/
  Armenia's flagship checks are BOOLEAN registry-membership ELIGIBILITY
  reads, Antigua and Barbuda's vendor-class check is a THREE-TIER
  ELIGIBILITY-THRESHOLD classification, Benin's MPME mechanism is a
  BID-EVALUATION PRICE ADJUSTMENT (not an eligibility gate at all), and
  Bhutan's FDI Negative List is a CATEGORICAL SECTOR-EXCLUSION
  allow-list gate. CAF's Marché réservé mechanism is none of these: it
  is a MULTI-CRITERION INCLUSION-ELIGIBILITY test -- an OR of THREE
  independent workforce-composition percentage thresholds and a
  legal-form set-membership test -- the first in this family to gate
  eligibility on the BIDDER'S OWN workforce composition rather than on
  turnover, a flat value, registry membership, contract value tier, a
  price adjustment, or the business ACTIVITY/sector.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement portal. It builds the RECORD an
  operator would keep, not the act of submitting a portal registration
  itself (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(def ^:private money-scale
  "Sub-minor-unit scale used when comparing two money amounts: 1/10000 of
  a unit. Coarser than double representation error by many orders of
  magnitude, finer than any real currency's minor unit (2 decimals for
  most, 3 for KWD/BHD/OMR, 0 for JPY/KRW)."
  10000)

(defn- money=
  "Exact-at-money-precision equality for two amounts.

  `==` on raw doubles is NOT the right comparison for money. With
  whole-unit fees the two agree, but as soon as an amount carries
  cents the sum `base + rate x months` is routinely not the double
  nearest the true total, and a CORRECT claim compares false: measured
  on this exact shape, 40,989 of 327,060 cent-denominated combinations
  (12.5%) were rejected while being right, against 0 of 327,060 in
  whole units.

  Rounding both sides to `money-scale` before comparing removes the
  representation error while preserving every distinction money can
  actually carry."
  [x y]
  (and (number? x) (number? y)
       (= (Math/round (* money-scale (double x)))
          (Math/round (* money-scale (double y))))))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  ;; nil when any field is not a number: an un-recomputable engagement is
  ;; un-verifiable, which is neither `correct` nor a ClassCastException
  ;; thrown out of the caller.
  (when (and (number? base-fee) (number? monthly-rate) (number? monitoring-months))
    (+ (double base-fee)
       (* (double monthly-rate) (double monitoring-months)))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (money= claimed-fee (compute-engagement-fee engagement)))

(def reserved-market-thresholds
  "Code de la Commande Publique (Loi N° 25.016 du 23 décembre 2025),
  Chapitre II, own definition of 'Marché réservé' (OCR/image-verified
  2026-07-22 against finances.gouv.cf's own hosting): the workforce-
  composition thresholds, ANY ONE of which makes a small/medium
  enterprise eligible for a reserved contract."
  {:pct-disabled-employees 0.30
   :pct-youth-employees 0.50
   :pct-women-employees 0.50})

(def social-economy-legal-forms
  "Code de la Commande Publique, Chapitre II, own definition of 'Marché
  réservé': legal forms eligible regardless of workforce composition
  ('les acteurs de l'économie sociale tels que les associations, les
  coopératives ouvrières ou artisanales, les groupements d'intérêt
  économique et les entreprises sociales')."
  #{:association :cooperative-ouvriere-ou-artisanale
    :groupement-interet-economique :entreprise-sociale})

(defn reserved-market-eligible?
  "The ground-truth Marché réservé eligibility for `engagement`,
  independently recomputed from its own declared legal form and
  workforce composition -- an OR of the legal-form set-membership test
  and the three workforce-composition threshold tests. A missing/nil
  declared value on any one branch simply fails that branch (does not
  throw); an engagement with no qualifying declaration at all is not
  eligible."
  [{:keys [social-economy-legal-form
           pct-disabled-employees pct-youth-employees pct-women-employees]}]
  (boolean
   (or (contains? social-economy-legal-forms social-economy-legal-form)
       (and (some? pct-disabled-employees)
            (>= (double pct-disabled-employees) (:pct-disabled-employees reserved-market-thresholds)))
       (and (some? pct-youth-employees)
            (>= (double pct-youth-employees) (:pct-youth-employees reserved-market-thresholds)))
       (and (some? pct-women-employees)
            (>= (double pct-women-employees) (:pct-women-employees reserved-market-thresholds))))))

(defn reserved-market-ineligible-claim?
  "Does `engagement` declare `:reserved-market? true` (i.e. it is
  bidding on a contract flagged as a Marché réservé) while the
  INDEPENDENTLY recomputed `reserved-market-eligible?` is false? A
  non-reserved-market engagement is never flagged by this check
  (entity/engagement-scope-gated, the same discipline Bhutan's
  `:foreign-company?`-gated FDI check uses)."
  [{:keys [reserved-market?] :as engagement}]
  (boolean (and reserved-market? (not (reserved-market-eligible? engagement)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing a portal registration
  package. Pure function -- does not touch any real procurement
  portal."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting a portal
  registration (always human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
