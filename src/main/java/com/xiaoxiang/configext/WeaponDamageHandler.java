package com.xiaoxiang.configext;

import com.xiaoxiang.configext.config.ExtendedConfig;
import com.xiaoxiang.cultivation.item.weapon.TieredWeapon;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Applies WEAPON_DAMAGE_GLOBAL_MULTIPLIER to melee damage dealt with any of the
 * base mod's tiered weapons (spirit swords, bloodthirst blade, etc).
 *
 * VERIFIED (2026-09-01) via javap that the base mod's own weapon attack damage
 * is baked into a vanilla SwordItem AttributeModifier at item construction time
 * (the "attackDamage" int passed into each weapon's constructor never appears
 * anywhere else in the base mod's bytecode - grepped the whole jar, only the
 * weapon item classes and their gametest classes reference it). That means
 * there is no live per-hit accessor to intercept the way PillEffectSpecs
 * exposes one for pills. Rather than mixin into the weapon constructors
 * (registration-time config-load-order risk, and the SwordItem constructor
 * arg is baked once for the item's whole lifetime) or guess at vanilla
 * SwordItem's obfuscated method name for rebuilding its attribute modifiers
 * (can't verify an SRG name without a decompiler/mapping data in this sandbox
 * - we've stuck to mixing into the base mod's OWN classes, which keep real
 * method names, for exactly this reason), this instead multiplies the FINAL
 * damage on hit via the standard Forge LivingHurtEvent - the same technique
 * the base mod's own com.xiaoxiang.cultivation.event.AttackBonusHandler uses
 * for its melee damage bonuses (confirmed by disassembling that class this
 * session: it calls LivingHurtEvent.getSource()/getAmount()/setAmount(F) in
 * exactly this shape). This also means the multiplier applies live on every
 * hit rather than needing a restart - strictly better than what a
 * constructor-time fix could offer.
 *
 * Deliberately not player-restricted (unlike AttackBonusHandler, which only
 * fires for ServerPlayer attackers): this checks that the event's direct and
 * indirect damage source are the same entity (i.e. a melee hit, not a
 * projectile/spell) so it also applies when an NPC wields one of these
 * weapons, matching what "global" in the field's own name promises.
 *
 * NOT bytecode-verified against Forge's own classes (no Forge jar available
 * in this sandbox to javap directly) - the LivingHurtEvent/DamageSource
 * method shapes used below match what was just confirmed via the base mod's
 * own AttackBonusHandler disassembly, which is real corroboration, but
 * flagging this explicitly since it's the one piece of this fix that
 * ultimately rests on that cross-check rather than a full independent
 * disassembly of the event class itself.
 */
@Mod.EventBusSubscriber(modid = XiaoxiangConfigExt.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WeaponDamageHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        try {
            if (!ExtendedConfig.ENABLE_WEAPON_OVERRIDES.get()) return;
            double mult = ExtendedConfig.WEAPON_DAMAGE_GLOBAL_MULTIPLIER.get();
            if (mult == 1.0) return;

            var source = event.getSource();
            Entity direct = source.getDirectEntity();
            Entity causer = source.getEntity();
            if (direct == null || causer == null || direct != causer) return;
            if (!(causer instanceof LivingEntity attacker)) return;

            ItemStack weapon = attacker.getMainHandItem();
            if (weapon.isEmpty()) return;
            if (!(weapon.getItem() instanceof TieredWeapon tw) || !tw.isSwordWeapon()) return;

            event.setAmount((float) (event.getAmount() * mult));
        } catch (Exception ignored) {
            // Config not loaded yet, or an API shape mismatch - do nothing rather than risk a bad multiply.
        }
    }
}
