package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
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

import static rangedarsenal.rangedarsenal.FLAME_AMMO_TYPES;

public class Flamethrower extends GunProjectileToolItem {
    public Flamethrower() {
        super(FLAME_AMMO_TYPES, 1000);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(40).setUpgradedValue(1.0F, 35);
        this.attackDamage.setBaseValue(7.0F).setUpgradedValue(1.0F, 9).setUpgradedValue(5.0F, 14);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(230).setUpgradedValue(1.0F, 320);
        this.velocity.setBaseValue(68).setUpgradedValue(1.0F, 97);
        this.knockback.setBaseValue(0);
        this.keyWords.add("flamethrower");
        this.resilienceGain.setBaseValue(0.15F);
        this.ammoConsumeChance = 0.75F;
        this.controlledRange = false;
        this.controlledMinRange = 5;
    }
    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.firespell1, SoundEffect.effect(mob).volume(0.32f).pitch(GameRandom.globalRandom.getFloatBetween(0.3f, 0.5f)));
    }
    public float getAttackMovementMod(InventoryItem item) {
        return 0.25F;
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        tooltips.add(Localization.translate("itemtooltip", "FlamethrowerTip"));
    }
    protected float getAmmoConsumeChance(ItemAttackerMob attackerMob, InventoryItem item) {
        float playerMod = attackerMob == null ? 1.0F : (Float)attackerMob.buffManager.getModifier(BuffModifiers.BULLET_USAGE);
        return GameMath.limit((this.ammoConsumeChance-(this.getUpgradeTier(item)*5/100)) * playerMod,0.50f,0.75f);
    }
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        //tooltips.add(Localization.translate("itemtooltip", "shardcannontipalt"));
        //doing this dynamicly looks gross, I have to make another if pyramid
        //I'm also lazy
        if (this.getUpgradeTier(item) == 1.0f) {
            tooltips.add(Localization.translate("itemtooltip", "FlamethrowerTipammo","ammouse",30));
        } else if (this.getUpgradeTier(item) == 2.0f) {
            tooltips.add(Localization.translate("itemtooltip", "FlamethrowerTipammo","ammouse",35));
        } else if (this.getUpgradeTier(item) == 3.0f) {
            tooltips.add(Localization.translate("itemtooltip", "FlamethrowerTipammo","ammouse",40));
        } else if (this.getUpgradeTier(item) == 4.0f) {
            tooltips.add(Localization.translate("itemtooltip", "FlamethrowerTipammo","ammouse",45));
        } else if (this.getUpgradeTier(item) >= 5.0f) {
            tooltips.add(Localization.translate("itemtooltip", "FlamethrowerTipammo","ammouse",50));
        } else {
            tooltips.add(Localization.translate("itemtooltip", "FlamethrowerTipammo","ammouse",25));
        }

        return tooltips;
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        if (bullet.idData.getStringID().equalsIgnoreCase("Napalm")) {
            this.controlledRange = true;
        } else {
            this.controlledRange = false;
        }
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
        projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 6.5F);
        projectile.dropItem = dropItem;
        projectile.getUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
    }
}
