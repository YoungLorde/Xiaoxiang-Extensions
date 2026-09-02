// RETIRED (2026-09-02): base mod 0.1.1479 deleted the BeastRealm enum this
// mixin targeted entirely (confirmed: the class is genuinely absent from the
// new jar's com/xiaoxiang/cultivation/cultivation/realm/ package, not just
// renamed - com.xiaoxiang.cultivation.cultivation.realm.BeastRealm no longer
// exists anywhere in 0.1.1479). Beast realm progression was redesigned to
// route through the same Realm enum players use, via a new static utility,
// BeastProgressionRules.advanceCost(Realm).
//
// This file's logic has been fully ported to the new target - see
// BeastProgressionRulesMixin.java in this same package, which covers the
// same 6 pre-existing "spirit*" config fields (unchanged names/values - the
// new system's first 6 tiers use identical defaults) plus 4 new fields for
// the 4 additional Realm tiers the redesign added.
//
// This session cannot delete files on your computer directly - please
// delete this file by hand:
//   src/main/java/com/xiaoxiang/configext/mixin/BeastRealmMixin.java
// It has been removed from xiaoxiang_config_ext.mixins.json (replaced by
// BeastProgressionRulesMixin) and contains no class, so leaving it in place
// changes nothing about how the mod behaves - it just won't compile-reference
// BeastRealm anymore, since that class doesn't exist in 0.1.1479.
