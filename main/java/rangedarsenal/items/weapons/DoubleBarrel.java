package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;
import java.awt.geom.Point2D;
import java.awt.*;
import java.util.LinkedList;

public class DoubleBarrel extends GunProjectileToolItem implements ItemInteractAction {
    public DoubleBarrel() {
        super(NORMAL_AMMO_TYPES, 1200);
        this.rarity = Rarity.COMMON;
        this.attackAnimTime.setBaseValue(910);
        this.attackDamage.setBaseValue(22.0F).setUpgradedValue(1.0F, 30F);
        this.attackXOffset = 12;
        this.attackYOffset = 10;
        this.attackRange.setBaseValue(655);
        this.velocity.setBaseValue(350);
        this.knockback.setBaseValue(50).setUpgradedValue(1.0F, 65).setUpgradedValue(5.0F, 115);
        this.resilienceGain.setBaseValue(0.1F);
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.shotgun, SoundEffect.effect(mob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(0.65f, 0.7f)));
    }

    protected void addExtraGunTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraGunTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "DoubleBarrelPrimaryTip"), 400);
        tooltips.add(Localization.translate("itemtooltip", "DoubleBarrelSecondaryTip"), 400);
        tooltips.add(Localization.translate("itemtooltip", "Shotguntier","pellets",Math.round(this.getUpgradeTier(item)/2 + 4)));
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        super.addAmmoTooltips(tooltips, item);
    }

    public float getAttackMovementMod(InventoryItem item) {
        return 0.6F;
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

        for(int i = 0; i <= 3 + this.getUpgradeTier(item)/2; ++i) {
            Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, range, attackerMob);
            projectile.setDamage(this.getDamage(item).modFinalMultiplier(0.8F));
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.dropItem = dropItem;
            projectile.getUniqueID(new GameRandom((long)seed));
            attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
            projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 22.0F);
        }
    }
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.buffManager.hasBuff("DoubleBarrelCooldownDebuff");
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (attackerMob.isPlayer) {
            Item bulletcheck = attackerMob.getFirstPlayerOwner().getInv().main.getFirstItem(level, attackerMob.getFirstPlayerOwner(), this.ammoItems(), "bulletammo");
            if (bulletcheck != null) {
                ActiveBuff ab = new ActiveBuff("DoubleBarrelCooldownDebuff", attackerMob, 2F, attackerMob);
                attackerMob.buffManager.addBuff(ab, true);

                Item bullet = attackerMob.getFirstPlayerOwner().getInv().main.getFirstItem(level, attackerMob.getFirstPlayerOwner(), this.ammoItems(), "bulletammo");
                int range;
                range = this.getAttackRange(item);
                GameRandom random = new GameRandom((long) seed);
                GameRandom spreadRandom = new GameRandom((long) (seed + 10));
                for (int i = 0; i <= 7 + this.getUpgradeTier(item) / 2; ++i) {
                    Projectile projectile = this.getProjectile(item, (BulletItem) bullet, attackerMob.x, attackerMob.y, (float) x, (float) y, range, attackerMob);
                    projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 28.0F);
                    projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                    projectile.dropItem = true;
                    projectile.getUniqueID(new GameRandom((long) seed));
                    attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
                }
                attackerMob.getFirstPlayerOwner().getInv().removeItems(attackerMob.getFirstPlayerOwner().getInv().main.getFirstItem(level, attackerMob.getFirstPlayerOwner(), this.ammoItems(), "bulletammo"), 1, true, true, true, true, "bulletammo");
                SoundManager.playSound(GameResources.shotgun, SoundEffect.effect(attackerMob).volume(1.1f).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 0.85f)));
                SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(attackerMob).volume(0.75F).pitch(1F));
            }
        } else {
            ActiveBuff ab = new ActiveBuff("DoubleBarrelCooldownDebuff", attackerMob, 2F, attackerMob);
            attackerMob.buffManager.addBuff(ab, true);
            int range;
            range = this.getAttackRange(item);
            GameRandom random = new GameRandom((long) seed);
            GameRandom spreadRandom = new GameRandom((long) (seed + 10));
            for (int i = 0; i <= 7 + this.getUpgradeTier(item) / 2; ++i) {
                Projectile projectile = this.getProjectile(item, (BulletItem) ItemRegistry.getItem("Flame_Bullet"), attackerMob.x, attackerMob.y, (float) x, (float) y, range, attackerMob);
                projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 28.0F);
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                projectile.dropItem = true;
                projectile.getUniqueID(new GameRandom((long) seed));
                attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
            }
            SoundManager.playSound(GameResources.shotgun, SoundEffect.effect(attackerMob).volume(1.1f).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 0.85f)));
            SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(attackerMob).volume(0.75F).pitch(1F));
        }
        return item;
    }

    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        return perspective.buffManager.getBuffDurationLeftSeconds("DoubleBarrelCooldownDebuff") / 2.0F;
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
