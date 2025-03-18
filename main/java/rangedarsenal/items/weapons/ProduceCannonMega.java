package rangedarsenal.items.weapons;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
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

import static rangedarsenal.rangedarsenal.FOOD_AMMO_TYPES;

public class ProduceCannonMega extends GunProjectileToolItem {
    public ProduceCannonMega() {
        super(FOOD_AMMO_TYPES, 1200);
        this.rarity = Rarity.LEGENDARY;
        this.attackAnimTime.setBaseValue(397).setUpgradedValue(1.0F, 331);
        this.attackDamage.setBaseValue(60.0F).setUpgradedValue(1.0F, 81.0F);
        this.attackXOffset = 10;
        this.attackYOffset = 10;
        this.attackRange.setBaseValue(641).setUpgradedValue(1.0F, 700);
        this.velocity.setBaseValue(300).setUpgradedValue(1.0F, 321);
        this.knockback.setBaseValue(0);
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        tooltips.add(Localization.translate("itemtooltip", "FoodgunTip"));
        tooltips.add(Localization.translate("itemtooltip", "FoodgunTip2"));
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(mob).volume(0.79f).pitch(1.71F));
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int bulletID = mapContent.getShortUnsigned("bulletID", 65535);
        if (bulletID != 65535) {
            Item bullet = ItemRegistry.getItem(bulletID);
            if (bullet != null) {
                GameRandom random = new GameRandom((long)(seed + 5));
                boolean consumeAmmo = true;
                if (attackerMob.isPlayer) {
                    if (!consumeAmmo || attackerMob.getFirstPlayerOwner().getInv().main.removeItems(level, attackerMob.getFirstPlayerOwner(), bullet, 1, "bulletammo") >= 1) {
                        if (ItemRegistry.itemExists(bullet.idData.getStringID().replaceFirst(Character.toString(bullet.idData.getStringID().charAt(0)), Character.toString(bullet.idData.getStringID().charAt(0)).toUpperCase()) + "_Food_Bullet")) {
                            bullet = ItemRegistry.getItem(bullet.idData.getStringID().replaceFirst(Character.toString(bullet.idData.getStringID().charAt(0)), Character.toString(bullet.idData.getStringID().charAt(0)).toUpperCase()) + "_Food_Bullet");
                            this.fireProjectiles(level, x, y, attackerMob, item, seed, (BulletItem) bullet, true, mapContent);
                        } else {
                            bullet = ItemRegistry.getItem("Apple_Food_Bullet");
                            this.fireProjectiles(level, x, y, attackerMob, item, seed, (BulletItem) bullet, false, mapContent);
                        }
                    }
                } else {
                    bullet = ItemRegistry.getItem("Pumpkin_Food_Bullet");
                    this.fireProjectiles(level, x, y, attackerMob, item, seed, (BulletItem)bullet, false, mapContent);
                }
            }
        }
        return item;
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
        if (bullet == ItemRegistry.getItem("Coffee_Food_Bullet")) {
            for(int i = 0; i <= 2; ++i) {
                Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, range, attackerMob);
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                projectile.dropItem = dropItem;
                projectile.getUniqueID(new GameRandom((long)seed));
                projectile.setDamage(this.getDamage(item).modFinalMultiplier(0.38F));
                projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 11F);
                attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
            }
        } else if (bullet == ItemRegistry.getItem("Rice_Food_Bullet")) {
            for(int i = 0; i <= 7; ++i) {
                Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, range, attackerMob);
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                projectile.dropItem = dropItem;
                projectile.getUniqueID(new GameRandom((long)seed));
                projectile.setDamage(this.getDamage(item).modFinalMultiplier(0.18F));
                projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 21F);
                attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
            }
        } else if (bullet == ItemRegistry.getItem("Wheat_Food_Bullet")) {
            for(int i = 0; i <= 5; ++i) {
                Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, range, attackerMob);
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                projectile.dropItem = dropItem;
                projectile.getUniqueID(new GameRandom((long)seed));
                projectile.setDamage(this.getDamage(item).modFinalMultiplier(0.24F));
                projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 19F);
                attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
            }
        } else {
            Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, range, attackerMob);
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.dropItem = dropItem;
            projectile.getUniqueID(new GameRandom((long)seed));
            projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 1F);
            attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
        }
    }
    public Projectile getProjectile(InventoryItem item, BulletItem bulletItem, float x, float y, float targetX, float targetY, int range, ItemAttackerMob attackerMob) {
        float velocity = bulletItem.modVelocity((float)this.getVelocity(item, attackerMob));
        range = bulletItem.modRange(range);
        GameDamage damage = bulletItem.modDamage(this.getDamage(item));
        int knockback = bulletItem.modKnockback(this.getKnockback(item, attackerMob));
        return bulletItem.getProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, attackerMob);
    }
}
