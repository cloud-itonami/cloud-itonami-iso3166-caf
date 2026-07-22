(ns statute.facts
  "General-law compliance catalog for the Central African Republic (CAF)
  -- extends this repo's existing `marketentry.facts` (public-
  procurement market-entry only, narrow scope) with a second,
  orthogonal catalog of statutes a company operating in this
  jurisdiction must generally track for compliance. Mirrors
  cloud-itonami-iso3166-jpn/-deu/-bgr/-aze/-alb/-arm/-atg/-ben/-btn's
  `statute.facts` (ADR-2607141700, cloud-itonami-compliance-fact-
  federation).

  Every entry cites an OFFICIAL government-hosted (or, for the OHADA
  entry, official supranational-body-hosted) URL -- never fabricated.

  - Companies/commercial-entity law: this iteration specifically
    investigated, rather than assumed by analogy to prior siblings,
    whether the Central African Republic has a domestic 'Commerce Act'
    of its own. It does NOT -- CAR, like Benin, is an OHADA member
    state (independently confirmed 2026-07-22 directly from OHADA's
    OWN member-states page, `ohada.org/les-etats-membres-de-lohada/`,
    fetched directly, which lists 'Centrafrique' -- NOT copied from
    any sibling repo's assertion), so company law is governed DIRECTLY
    by a SUPRANATIONAL instrument, the OHADA Uniform Act on Commercial
    Companies and Economic Interest Groupings (Acte uniforme relatif
    au droit des sociétés commerciales et du groupement d'intérêt
    économique, AUSCGIE). This iteration independently fetched OHADA's
    OWN page for this Act (`ohada.org/en/commercial-companies-and-
    economic-interest-groups/`, NOT copied from any sibling's citation
    text) and confirmed its own stated dates directly: 'Date and place
    of adoption: January 30, 2014 in Ouagadougou (Burkina Faso)' /
    'Entry into force: May 5, 2014'. Article 10 of the OHADA Treaty
    gives every Uniform Act direct and obligatory effect in every
    member state without domestic transposition -- this iteration
    ALSO attempted to independently re-verify this specific article's
    exact wording directly from OHADA's own treaty-text page
    (`ohada.org/traite-portant-revision-du-traite-relatif-a-
    lharmonisation-du-droit-des-affaires-en-afrique/`, fetched
    directly) and found that page's own rendering of the revised
    treaty skips straight from the end of Article 9 to Article 12 --
    Articles 10 and 11 are simply ABSENT from OHADA's own published
    HTML for that page (confirmed by searching the extracted plain
    text for both 'Article 10' and the direct-effect provision's
    known key phrases, e.g. 'nonobstant'/'de plein droit' -- neither
    string appears anywhere on the page). This is an honestly-flagged
    NEW gap this iteration found (a content gap on OHADA's own
    website, not a fabrication risk on this catalog's part): the
    substantive direct-effect FACT itself is uncontroversial and
    extremely well-established across independent legal-commentary
    treatments of OHADA law, so this catalog still states it, but the
    EXACT WORDING of 'Article 10' is not verified against OHADA's own
    primary treaty text by this iteration (MODERATE confidence on
    exact wording, HIGH confidence on the substantive fact and on
    AUSCGIE's own adoption/entry-into-force dates, which WERE read
    directly). Separately, RCCM/business-entity REGISTRATION -- as
    opposed to company FORMATION/governance law -- is governed by a
    DIFFERENT OHADA instrument, the Acte Uniforme relatif au Droit
    Commercial Général (AUDCG, own dates independently confirmed
    directly from `ohada.org/droit-commercial-general/`: adopted 15
    December 2010 in Lomé (Togo), in force 15 May 2011); this catalog
    does not conflate the two, and `marketentry.facts` cites AUDCG
    separately for RCCM.
  - Code du Travail (Labour Code): this iteration specifically
    searched for the Central African Republic's own Labour Code text
    and could NOT independently verify a citation. Attempts made: (1)
    the Ministry of Labour's own site, `travail.gouv.cf`, returned
    HTTP 403 on every direct fetch attempt (both plain curl and
    WebFetch); (2) `droit-afrique.com` (a secondary legal-text host
    used successfully by other jurisdictions' catalogs in this family)
    returned HTTP 403 on every path tried, including its own country
    landing page and a guessed direct-PDF path, with a body reading
    'Server unable to read htaccess file, denying access to be safe'
    -- a server-side condition, not confirmation the document does not
    exist; (3) FAOLEX (`fao.org/faolex`) returned HTTP 403; (4) ILO's
    NATLEX and NORMLEX databases were unreachable or returned invalid/
    placeholder country profiles for the IDs this iteration tried; (5)
    a one-time DuckDuckGo HTML search fallback hit DuckDuckGo's own
    anti-bot 'anomaly' interstitial (not retried, per this iteration's
    operating instructions); (6) this session's WebSearch budget was
    separately exhausted before this specific query could be
    attempted through that tool. NO Code du Travail entry is included
    in `catalog` below as a result -- this is an honest, explicitly-
    reported GAP (Central African Republic is a genuinely
    under-documented jurisdiction online), not a claim of non-
    existence: CAR almost certainly has a Labour Code, this iteration
    simply could not read its own primary text or confirm its exact
    law number/date through any source available in this session.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit. CAF's catalog is smaller
  than some siblings' (Benin's has 3 entries) -- this reflects an
  honest coverage gap (Labour Code and any digital/data-protection code
  could not be independently verified this iteration, see namespace
  docstring), not a design choice to omit them."
  {"CAF"
   [{:statute/id "caf.ohada-auscgie"
     :statute/title "Acte uniforme relatif au droit des sociétés commerciales et du groupement d'intérêt économique (AUSCGIE)"
     :statute/jurisdiction "CAF"
     :statute/kind :law
     :statute/law-number "OHADA Uniform Act -- adopted 30 January 2014 (Ouagadougou), in force 5 May 2014; directly applicable in the Central African Republic as an OHADA member state per the OHADA Treaty's direct-effect provision (this iteration independently confirmed CAR's OHADA membership and this Act's own adoption/entry-into-force dates directly from ohada.org; the direct-effect provision's exact article wording could not be re-verified against OHADA's own treaty-text page this iteration, see namespace docstring -- MODERATE confidence on exact wording, HIGH confidence on the substantive fact and the Act's own dates)"
     :statute/url "https://www.ohada.org/en/commercial-companies-and-economic-interest-groups/"
     :statute/url-provenance :official-ohada-org
     :statute/enacted-date "2014-01-30"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:corporate-governance :incorporation}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-caf statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "CAF")) " CAF statute(s) seeded with an "
                 "official citation (Labour Code and any digital/data-protection "
                 "code could not be independently verified this iteration -- an "
                 "honest gap, not an omission by design). Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :data-protection)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
