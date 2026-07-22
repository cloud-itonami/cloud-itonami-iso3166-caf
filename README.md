# cloud-itonami-iso3166-caf

**CAF**: Central African Republic.

- A.R.C.O.P / D.G.C.O.P / U.G.C.O.P (Code de la Commande Publique, Loi
  N° 25.016 du 23 décembre 2025) public-procurement compliance
- RCCM (OHADA AUDCG) business registration + DGID tax registration;
  Code de la Commande Publique "Marché réservé" reserved-contract
  eligibility gate

AGPL-3.0-or-later.

## Market-entry / statute catalogs

Governed public-sector market-entry compliance actor, same architecture
as `cloud-itonami-iso3166-ben`/`-btn` (the closest architectural match:
Benin is also an OHADA member state sharing the same supranational
company-law instrument):

- `src/marketentry/{facts,governor,phase,sim,operation,registry,store,
  marketentryllm}.cljc` -- the actor. `facts.cljc` cites the new (23
  December 2025) Code de la Commande Publique's own A.R.C.O.P
  (Autorité de Régulation de la Commande Publique, attached to the
  Primature) / D.G.C.O.P (Direction Générale de la Commande Publique,
  under the Minister of Finance) / U.G.C.O.P (Unité de Gestion de la
  Commande Publique, per contracting authority) three-body split,
  which replaces the 2008-era ARMP/DGMP two-body split (Loi n°08.017
  du 06 juin 2008); RCCM (OHADA's Acte Uniforme relatif au Droit
  Commercial Général, per the Ministry of Justice's own
  justice.gouv.cf) and DGID (Direction Générale des Impôts et des
  Domaines) tax registration. `governor.cljc`'s flagship check
  independently recomputes the Code's own "Marché réservé"
  (reserved-contract) eligibility criteria -- a multi-criterion
  inclusion-eligibility test (social-economy legal form, OR workforce
  composition >=30% persons with disabilities / >=50% youth / >=50%
  women), a check shape genuinely different from every other iso3166
  sibling's (see the namespace docstrings for the full research trail
  and honestly-narrowed scope, including facts this iteration could
  NOT verify, such as a Labour Code citation and a national
  single-window business-formalities agency name).
- `src/statute/facts.cljc` -- general-law catalog: the OHADA Uniform
  Act on Commercial Companies (AUSCGIE, directly applicable, no
  domestic transposition act). Smaller than some siblings' catalogs --
  a Labour Code (Code du Travail) citation could not be independently
  verified this iteration (an honest gap, not an omission by design;
  see the namespace docstring).

Every citation is curl/WebFetch-verified against an official source
(finances.gouv.cf, ohada.org, justice.gouv.cf); the new Code de la
Commande Publique PDF is a scanned image with no embedded text layer,
so this iteration read it via its own OCR pass (no French-language OCR
pack was available, so an English-model OCR pass plus direct visual
zoom on the cover/signature pages was used instead) -- see
`marketentry.facts`'s docstring for exactly which facts are
HIGH-confidence (read directly off a zoomed image) vs. MODERATE (OCR
of a hand-filled serial number) vs. an honestly-flagged gap.

## Culture catalog

This repo carries a **country-level regional-culture catalog**
(ADR-2607171400 addendum 2, `cloud-itonami-municipality-culture-catalog`
Wave 1, in `com-junkawasaki/root`) — national dishes, protected products,
beverages, crafts, festivals and heritage sites for the Central African
Republic:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
