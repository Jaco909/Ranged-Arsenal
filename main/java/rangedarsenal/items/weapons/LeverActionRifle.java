package rangedarsenal.items.weapons;

import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class LeverActionRifle extends GunProjectileToolItem implements ItemInteractAction {
    public LeverActionRifle() {
        super(NORMAL_AMMO_TYPES, 500);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(1200).setUpgradedValue(1.0F, 1100).setUpgradedValue(5.0F, 700);
        this.attackDamage.setBaseValue(34.0F).setUpgradedValue(1.0F, 120.0F);
        this.attackXOffset = 20;
        this.attackYOffset = 10;
        this.velocity.setBaseValue(700).setUpgradedValue(1.0F, 750);
        this.attackRange.setBaseValue(1000).setUpgradedValue(1.0F, 1100).setUpgradedValue(5.0F, 1500);
        this.knockback.setBaseValue(50);
        this.resilienceGain.setBaseValue(0.4f).setUpgradedValue(1.0F, 0.401f).setUpgradedValue(4.0F, 0.405f);
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    //force crit chance
    public GameDamage getFlatAttackDamage(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        if (gndData.hasKey("damage")) {
            GNDItem gndItem = gndData.getItem("damage");
            if (gndItem instanceof GNDItemGameDamage) {
                return ((GNDItemGameDamage)gndItem).damage;
            }

            if (gndItem instanceof GNDItem.GNDPrimitive) {
                float damage = ((GNDItem.GNDPrimitive)gndItem).getFloat();
                return new GameDamage(this.getDamageType(item), damage);
            }
        }
        if (this.getUpgradeTier(item) > 0) {
            return new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)),0,(0.14f + (this.getUpgradeTier(item)*0.04f)));
        } else {
            return new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)),0,0.15f);
        }
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(mob));
    }
    protected void addExtraGunTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraGunTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "Lever_Action_RifleSecondaryTip"), 400);
    }

    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.buffManager.hasBuff("LeverActionRifleCooldownDebuff");
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
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
        projectile.piercing = projectile.piercing+1;
        projectile.dropItem = dropItem;
        projectile.getUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (attackerMob.isPlayer) {
            Item bulletcheck = attackerMob.getFirstPlayerOwner().getInv().main.getFirstItem(level, attackerMob.getFirstPlayerOwner(), this.ammoItems(), "bulletammo");
            if (bulletcheck != null) {
                ActiveBuff ab = new ActiveBuff("LeverActionRifleCooldownDebuff", attackerMob, 2F, attackerMob);
                attackerMob.buffManager.addBuff(ab, true);

                Item bullet = attackerMob.getFirstPlayerOwner().getInv().main.getFirstItem(level, attackerMob.getFirstPlayerOwner(), this.ammoItems(), "bulletammo");
                int range;
                range = this.getAttackRange(item);
                Projectile projectile = this.getProjectile(item, (BulletItem) bullet, attackerMob.x, attackerMob.y, (float) x, (float) y, range, attackerMob);
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                projectile.dropItem = true;
                projectile.getUniqueID(new GameRandom((long) seed));
                attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
                projectile.piercing = projectile.piercing+2;
                projectile.setDamage(new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)), 0, 0.30f + (this.getUpgradeTier(item)*0.04f)));
                attackerMob.getFirstPlayerOwner().getInv().removeItems(attackerMob.getFirstPlayerOwner().getInv().main.getFirstItem(level, attackerMob.getFirstPlayerOwner(), this.ammoItems(), "bulletammo"), 1, true, true, true, true, "bulletammo");

                SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(attackerMob).volume(1.1f).pitch(GameRandom.globalRandom.getFloatBetween(0.85f, 0.9f)));
                SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(attackerMob).volume(0.5f).pitch(GameRandom.globalRandom.getFloatBetween(0.4f, 0.45f)));
                SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(attackerMob).volume(0.25F).pitch(1F));
            }
        } else {
            ActiveBuff ab = new ActiveBuff("LeverActionRifleCooldownDebuff", attackerMob, 2F, attackerMob);
            attackerMob.buffManager.addBuff(ab, true);

            int range;
            range = this.getAttackRange(item);
            Projectile projectile = this.getProjectile(item, (BulletItem) ItemRegistry.getItem("Standard_Bullet"), attackerMob.x, attackerMob.y, (float) x, (float) y, range, attackerMob);
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.dropItem = true;
            projectile.getUniqueID(new GameRandom((long) seed));
            attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
            projectile.piercing = 5;
            projectile.setDamage(new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)), 0, 1f));

            SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(attackerMob).volume(1.1f).pitch(GameRandom.globalRandom.getFloatBetween(0.85f, 0.9f)));
            SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(attackerMob).volume(0.5f).pitch(GameRandom.globalRandom.getFloatBetween(0.4f, 0.45f)));
            SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(attackerMob).volume(0.25F).pitch(1F));
        }
        return item;
    }

    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        return perspective.buffManager.getBuffDurationLeftSeconds("LeverActionRifleCooldownDebuff") / 5.0F;
    }

    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y) {
            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }
}
