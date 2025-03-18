package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;

public class LightMachinegun extends GunProjectileToolItem {
    public LightMachinegun() {
        super(NORMAL_AMMO_TYPES, 1600);
        this.rarity = Rarity.COMMON;
        this.attackAnimTime.setBaseValue(100);
        this.attackDamage.setBaseValue(9.0F).setUpgradedValue(1.0F, 13F).setUpgradedValue(2.0F, 22F).setUpgradedValue(3.0F, 26F).setUpgradedValue(4.0F, 31F);
        this.attackXOffset = 10;
        this.attackYOffset = 10;
        this.moveDist = 50;
        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(400);
        this.knockback.setBaseValue(0).setUpgradedValue(1.0F, 10);
        this.resilienceGain.setBaseValue(0.2F);
        this.ammoConsumeChance = 0.75F;
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        super.addAmmoTooltips(tooltips, item);
        tooltips.add(Localization.translate("itemtooltip", "LightMachinegunTip"));
        tooltips.add(Localization.translate("itemtooltip", "LightMachinegunTip2"));
        tooltips.add(Localization.translate("itemtooltip", "LightMachinegunTip3"));
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.handgun, SoundEffect.effect(mob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.5f, 1.7f)));;
    }
    public float getAttackMovementMod(InventoryItem item) {
        return 0.7F;
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(new GameRandom((long)(seed + 10)), attackerMob, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)attackerMob.getDistance((float)x, (float)y);
        } else {
            range = this.getAttackRange(item);
        }

        Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, range, attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = dropItem;
        projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * (12F - this.getUpgradeTier(item)));
        projectile.getUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
    }
}
