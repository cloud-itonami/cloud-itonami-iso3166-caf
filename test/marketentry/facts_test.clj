(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest caf-has-spec-basis
  (let [sb (facts/spec-basis "CAF")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "CAF")))
    (is (some? (facts/reserved-market-spec-basis "CAF")))))

(deftest caf-rep-spec-basis-is-honestly-absent
  (testing "the now-superseded 2008 Code's Art. 17-19 exclusion-extension provision could not be confirmed in the new Dec-2025 Code's own text -- deliberately not claimed"
    (is (nil? (facts/rep-spec-basis "CAF")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "CAF")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "CAF" all)))
    (is (not (facts/required-evidence-satisfied? "CAF" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["CAF" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest reserved-market-spec-basis-criteria
  (let [rm (facts/reserved-market-spec-basis "CAF")]
    (is (= 0.30 (get-in rm [:reserved-market-criteria :pct-disabled-employees-threshold])))
    (is (= 0.50 (get-in rm [:reserved-market-criteria :pct-youth-employees-threshold])))
    (is (= 0.50 (get-in rm [:reserved-market-criteria :pct-women-employees-threshold])))
    (is (contains? (get-in rm [:reserved-market-criteria :social-economy-legal-forms]) :association))))
