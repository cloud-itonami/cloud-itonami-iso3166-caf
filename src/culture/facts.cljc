(ns culture.facts
  "Country-level regional-culture catalog for the Central African Republic
  (CAF) -- national dishes, protected products, beverages, crafts,
  festivals and heritage sites, per ADR-2607171400 addendum 2
  (cloud-itonami-municipality-culture-catalog Wave 1, in
  com-junkawasaki/root). Sibling namespace to `marketentry.facts` /
  `statute.facts` (ADR-2607141700); city-level counterparts live in the
  cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"CAF"
   [{:culture/id "caf.dish.fufu"
     :culture/name "Fufu"
     :culture/name-local "Foufou"
     :culture/country "CAF"
     :culture/kind :dish
     :culture/summary "Pounded meal of boiled cassava, plantains or cocoyam eaten across West and Central Africa; in the Central African Republic it is known as foufou."
     :culture/url "https://en.wikipedia.org/wiki/Fufu"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "caf.dish.maboke"
     :culture/name "Maboké"
     :culture/country "CAF"
     :culture/kind :dish
     :culture/summary "Central African dish of fish wrapped and cooked in cassava or banana leaves, mainly eaten in the Central African Republic and the Democratic Republic of the Congo."
     :culture/url "https://en.wikipedia.org/wiki/Maboké"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "caf.dish.koko"
     :culture/name "Koko"
     :culture/name-local "Gnetum africanum"
     :culture/country "CAF"
     :culture/kind :dish
     :culture/summary "Leaves of the tropical African vine Gnetum africanum, used as a vegetable for soups and stews; the plant is called koko in the Central African Republic."
     :culture/url "https://en.wikipedia.org/wiki/Gnetum_africanum"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "caf.product.cassava"
     :culture/name "Cassava"
     :culture/country "CAF"
     :culture/kind :product
     :culture/summary "Abundant staple crop in the Central African Republic, whose residents have developed a number of unique ways of utilizing the plant."
     :culture/url "https://en.wikipedia.org/wiki/Cassava-based_dishes"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "caf.beverage.palm-wine"
     :culture/name "Palm wine"
     :culture/country "CAF"
     :culture/kind :beverage
     :culture/summary "Alcoholic beverage created from the fermented sap of various palm species, playing an important role in ceremonies in Central and Western Africa, including the Central African Republic."
     :culture/url "https://en.wikipedia.org/wiki/Palm_wine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "caf.heritage.manovo-gounda-st-floris"
     :culture/name "Manovo-Gounda St Floris National Park"
     :culture/country "CAF"
     :culture/kind :heritage
     :culture/summary "National park in the Central African Republic's Bamingui-Bangoran prefecture, inscribed as a UNESCO World Heritage Site in 1988 and on the List of World Heritage in Danger since 1997."
     :culture/url "https://en.wikipedia.org/wiki/Manovo-Gounda_St._Floris_National_Park"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "caf.heritage.sangha-trinational"
     :culture/name "Sangha Trinational"
     :culture/country "CAF"
     :culture/kind :heritage
     :culture/summary "Transnational Congo Basin rainforest protected area shared by the Central African Republic, Cameroon and the Republic of the Congo, added as a UNESCO World Heritage Site in 2012."
     :culture/url "https://en.wikipedia.org/wiki/Sangha_Trinational"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-caf culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "CAF"))
                 " CAF entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
