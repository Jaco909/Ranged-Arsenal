package rangedarsenal.items.weapons;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;

import static rangedarsenal.rangedarsenal.SHELL_AMMO_TYPES;

public class GrenadeLauncher extends GunProjectileToolItem {
    public GrenadeLauncher() {
        super(SHELL_AMMO_TYPES, 2000);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(1500).setUpgradedValue(1.0F, 1400).setUpgradedValue(2.0F, 1300).setUpgradedValue(3.0F, 1200).setUpgradedValue(4.0F, 1000).setUpgradedValue(5.0F, 975);
        this.attackDamage.setBaseValue(100.0F).setUpgradedValue(1.0F, 150.0F).setUpgradedValue(5.0F, 185.0F);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(350);
        this.velocity.setBaseValue(110).setUpgradedValue(1.0F, 120).setUpgradedValue(2.0F, 130).setUpgradedValue(3.0F, 140).setUpgradedValue(5.0F, 150);
        this.knockback.setBaseValue(0);
        this.controlledRange = false;
    }
    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(mob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.15f, 1.15f)));
    }
    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        tooltips.add(Localization.translate("itemtooltip", "GrenadeLauncherTip"));
        tooltips.add(Localization.translate("itemtooltip", "GrenadeLauncherTip2"));
    }

    public float getAttackMovementMod(InventoryItem item) {
        //makes proxy easier to use
        return 0.60F;
    }
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int bulletID = mapContent.getShortUnsigned("bulletID", 65535);
        if (bulletID != 65535) {
            Item bullet = ItemRegistry.getItem(bulletID);
            if (bullet != null && bullet.type == Type.BULLET) {
                GameRandom random = new GameRandom((long) (seed + 5));
                float ammoConsumeChance = ((BulletItem) bullet).getAmmoConsumeChance() * this.getAmmoConsumeChance(attackerMob, item);
                boolean consumeAmmo = ammoConsumeChance >= 1.0F || ammoConsumeChance > 0.0F && random.getChance(ammoConsumeChance);
                boolean dropItem;
                boolean shouldFire;
                if (!consumeAmmo) {
                    shouldFire = true;
                    dropItem = false;
                } else if (attackerMob instanceof AmmoUserMob) {
                    AmmoConsumed consumed = ((AmmoUserMob) attackerMob).removeAmmo(bullet, 1, "bulletammo");
                    shouldFire = consumed.amount >= 1;
                    dropItem = random.getChance(consumed.dropChance);
                } else {
                    shouldFire = true;
                    dropItem = false;
                }

                if (shouldFire) {
                    if (bullet.idData.getStringID().equalsIgnoreCase("Grenade_Launcher_Proxy_Shell")) {
                        this.controlledRange = true;
                        this.fireProjectiles(level, x, y, attackerMob, item, seed, (BulletItem) bullet, dropItem, mapContent);
                    } else {
                        this.controlledRange = false;
                        this.fireProjectiles(level, x, y, attackerMob, item, seed, (BulletItem) bullet, dropItem, mapContent);
                    }
                }
            }
        }
        return item;
    }
    public Projectile getProjectile(InventoryItem item, BulletItem bulletItem, float x, float y, float targetX, float targetY, int range, ItemAttackerMob attackerMob) {
        float velocity = bulletItem.modVelocity((float)this.getProjectileVelocity(item, attackerMob));
        range = bulletItem.modRange(range);
        GameDamage damage = bulletItem.modDamage(this.getAttackDamage(item));
        if (bulletItem.idData.getStringID().equalsIgnoreCase("GrenadeLauncherProxyShell")) {
            damage = (new GameDamage(this.attackDamage.getValue(this.getUpgradeTier(item))/4,bulletItem.armorPen,bulletItem.critChance));
        } else {
            damage = (new GameDamage(bulletItem.damage+this.attackDamage.getValue(this.getUpgradeTier(item)),bulletItem.armorPen,bulletItem.critChance+this.getCritChance(item,attackerMob)));
        }
        int knockback = bulletItem.modKnockback(this.getKnockback(item, attackerMob));
        return bulletItem.overrideProjectile() ? bulletItem.getProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, attackerMob) : this.getNormalProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, attackerMob);
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
        Projectile projectile;
        if (attackerMob.isPlayer) {
            projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float) x, (float) y, range, attackerMob);
        } else {
            projectile = this.getProjectile(item, (BulletItem) ItemRegistry.getItem("Grenade_Launcher_Shell"), attackerMob.x, attackerMob.y, (float) x, (float) y, range, attackerMob);
        }
        projectile.setDamage(new GameDamage(projectile.getDamage().damage+this.attackDamage.getValue(this.getUpgradeTier(item)),projectile.getDamage().armorPen,projectile.getDamage().baseCritChance));
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
    }
}
