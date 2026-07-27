(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "CAF" 0)
        s (registry/register-submit "eng-1" "CAF" 0)]
    (is (= "CAF-DFT-000000" (get d "draft_number")))
    (is (= "CAF-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "CAF" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest reserved-market-eligible-by-legal-form
  (testing "a qualifying social-economy legal form is eligible regardless of workforce composition"
    (is (true? (registry/reserved-market-eligible? {:social-economy-legal-form :association})))
    (is (true? (registry/reserved-market-eligible? {:social-economy-legal-form :cooperative-ouvriere-ou-artisanale})))
    (is (true? (registry/reserved-market-eligible? {:social-economy-legal-form :groupement-interet-economique})))
    (is (true? (registry/reserved-market-eligible? {:social-economy-legal-form :entreprise-sociale})))
    (is (false? (registry/reserved-market-eligible? {:social-economy-legal-form :sarl})))))

(deftest reserved-market-eligible-by-workforce-composition
  (testing "any ONE of the three thresholds is sufficient (OR, not AND)"
    (is (true? (registry/reserved-market-eligible? {:pct-disabled-employees 0.30})))
    (is (true? (registry/reserved-market-eligible? {:pct-disabled-employees 0.35})))
    (is (false? (registry/reserved-market-eligible? {:pct-disabled-employees 0.29})))
    (is (true? (registry/reserved-market-eligible? {:pct-youth-employees 0.50})))
    (is (false? (registry/reserved-market-eligible? {:pct-youth-employees 0.49})))
    (is (true? (registry/reserved-market-eligible? {:pct-women-employees 0.50})))
    (is (false? (registry/reserved-market-eligible? {:pct-women-employees 0.49}))))
  (testing "no qualifying legal form and no threshold met -> not eligible"
    (is (false? (registry/reserved-market-eligible? {}))))
  (testing "a legal form AND high workforce percentages together are still eligible"
    (is (true? (registry/reserved-market-eligible? {:social-economy-legal-form :association
                                                     :pct-women-employees 0.60})))))

(deftest reserved-market-ineligible-claim-is-entity-scope-gated
  (testing "an engagement NOT declared :reserved-market? is never flagged, even if it would fail eligibility"
    (is (false? (registry/reserved-market-ineligible-claim? {:reserved-market? false}))))
  (testing "a reserved-market engagement that fails every eligibility branch -> ineligible claim"
    (is (true? (registry/reserved-market-ineligible-claim? {:reserved-market? true
                                                             :pct-disabled-employees 0.05
                                                             :pct-youth-employees 0.10
                                                             :pct-women-employees 0.20}))))
  (testing "a reserved-market engagement that DOES satisfy eligibility -> not flagged"
    (is (false? (registry/reserved-market-ineligible-claim? {:reserved-market? true
                                                              :social-economy-legal-form :association})))
    (is (false? (registry/reserved-market-ineligible-claim? {:reserved-market? true
                                                              :pct-women-employees 0.55})))))

;; ---------------------------------------------------------------------------
;; Money is compared at money precision, not at double precision
;; ---------------------------------------------------------------------------

(deftest whole-unit-fees-were-already-correct-and-stay-correct
  (testing "the seeded shape: base + rate x months in whole currency units"
    (is (registry/engagement-fee-matches-claim?
         {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
           :claimed-fee 860000.0}))))

(deftest cent-denominated-fees-are-no-longer-rejected-while-correct
  (testing "`(== (double claimed) (+ (double base) (* (double rate) (double months))))`
            rejected CORRECT totals once an amount carried cents -- 40,989 of
            327,060 combinations (12.5%), against 0 of 327,060 in whole units"
    (let [bad (for [m (range 1 37)
                    bc (range 10000 90000 2100)
                    rc (range 500 6000 210)
                    :let [truth (/ (+ bc (* rc m)) 100.0)]
                    :when (not (registry/engagement-fee-matches-claim?
                                {:base-fee (/ bc 100.0) :monthly-rate (/ rc 100.0)
                                  :monitoring-months m :claimed-fee truth}))]
                [m (/ bc 100.0) (/ rc 100.0) truth])]
      (is (empty? bad) (str "false rejections: " (count bad) " e.g. " (first bad))))))

(deftest a-genuinely-wrong-fee-is-still-caught
  (testing "rounding to money precision must not blunt the check"
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 860000.01})))
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 859999.99})))))

(deftest an-unverifiable-fee-never-matches
  (testing "un-verifiable is not the same as correct, and not a crash"
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12})))
    (is (not (registry/engagement-fee-matches-claim?
              {:base-fee "500000" :monthly-rate 30000 :monitoring-months 12
                :claimed-fee 860000.0})))
    (is (nil? (registry/compute-engagement-fee {:base-fee 500000 :monthly-rate 30000})))))
