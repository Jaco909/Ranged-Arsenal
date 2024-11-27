package rangedarsenal.items.weapons;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
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
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameColor;
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

public class ProduceCannon extends GunProjectileToolItem {
    public ProduceCannon() {
        super(FOOD_AMMO_TYPES, 800);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(641).setUpgradedValue(5.0F, 587);
        this.attackDamage.setBaseValue(48.0F).setUpgradedValue(1.0F, 153.0F);
        this.attackXOffset = 10;
        this.attackYOffset = 10;
        this.attackRange.setBaseValue(641).setUpgradedValue(1.0F, 941);
        this.velocity.setBaseValue(277).setUpgradedValue(1.0F, 487);
        this.knockback.setBaseValue(0);
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        tooltips.add(Localization.translate("itemtooltip", "FoodgunTip"));
        tooltips.add(Localization.translate("itemtooltip", "FoodgunTip2"));
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(mob).volume(0.79f).pitch(1.71F));
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        int bulletID = contentReader.getNextShortUnsigned();
        if (bulletID != 65535) {
            Item bullet = ItemRegistry.getItem(bulletID);
            if (bullet != null) {
                GameRandom random = new GameRandom((long)(seed + 5));
                boolean consumeAmmo = true;
                if (!consumeAmmo || player.getInv().main.removeItems(level, player, bullet, 1, "bulletammo") >= 1) {
                    if (ItemRegistry.itemExists(bullet.idData.getStringID().replaceFirst(Character.toString(bullet.idData.getStringID().charAt(0)),Character.toString(bullet.idData.getStringID().charAt(0)).toUpperCase()) + "_Food_Bullet")) {
                        bullet = ItemRegistry.getItem(bullet.idData.getStringID().replaceFirst(Character.toString(bullet.idData.getStringID().charAt(0)),Character.toString(bullet.idData.getStringID().charAt(0)).toUpperCase()) + "_Food_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else {
                        bullet = ItemRegistry.getItem("Apple_Food_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    }
                }
            } else {
                GameLog.warn.println(player.getDisplayName() + " tried to use item " + (bullet == null ? bulletID : bullet.getStringID()) + " as bullet.");
            }
        }
        return item;
    }
    protected void fireProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, BulletItem bullet, boolean consumeAmmo, PacketReader contentReader) {
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(spreadRandom, player, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)player.getDistance((float)x, (float)y);
        } else {
            range = this.getAttackRange(item);
        }
        if (bullet == ItemRegistry.getItem("Coffee_Food_Bullet")) {
            for(int i = 0; i <= 2; ++i) {
                Projectile projectile = this.getProjectile(item, bullet, player.x, player.y, (float)x, (float)y, range, player);
                projectile.setDamage(this.getDamage(item).modFinalMultiplier(0.38F));
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                projectile.dropItem = consumeAmmo;
                projectile.getUniqueID(random);
                level.entityManager.projectiles.addHidden(projectile);
                if (this.moveDist != 0) {
                    projectile.moveDist((double)this.moveDist);
                }

                projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 11F);
                if (level.isServer()) {
                    level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
                }
            }
        } else if (bullet == ItemRegistry.getItem("Rice_Food_Bullet")) {
            for(int i = 0; i <= 7; ++i) {
                Projectile projectile = this.getProjectile(item, bullet, player.x, player.y, (float)x, (float)y, range, player);
                projectile.setDamage(this.getDamage(item).modFinalMultiplier(0.18F));
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                projectile.dropItem = consumeAmmo;
                projectile.getUniqueID(random);
                level.entityManager.projectiles.addHidden(projectile);
                if (this.moveDist != 0) {
                    projectile.moveDist((double)this.moveDist);
                }

                projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 21.0F);
                if (level.isServer()) {
                    level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
                }
            }
        } else if (bullet == ItemRegistry.getItem("Wheat_Food_Bullet")) {
            for(int i = 0; i <= 5; ++i) {
                Projectile projectile = this.getProjectile(item, bullet, player.x, player.y, (float)x, (float)y, range, player);
                projectile.setDamage(this.getDamage(item).modFinalMultiplier(0.24F));
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                projectile.dropItem = consumeAmmo;
                projectile.getUniqueID(random);
                level.entityManager.projectiles.addHidden(projectile);
                if (this.moveDist != 0) {
                    projectile.moveDist((double)this.moveDist);
                }

                projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 19.0F);
                if (level.isServer()) {
                    level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
                }
            }
        } else {
            Projectile projectile = this.getProjectile(item, bullet, player.x, player.y, (float) x, (float) y, range, player);
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.getUniqueID(random);
            level.entityManager.projectiles.addHidden(projectile);
            if (this.moveDist != 0) {
                projectile.moveDist((double) this.moveDist);
            }

            projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 0.1F);
            if (level.isServer()) {
                level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
            }
        }
    }
    public Projectile getProjectile(InventoryItem item, BulletItem bulletItem, float x, float y, float targetX, float targetY, int range, Mob owner) {
        float velocity = bulletItem.modVelocity((float)this.getVelocity(item, owner));
        range = bulletItem.modRange(range);
        GameDamage damage = bulletItem.modDamage(this.getDamage(item));
        int knockback = bulletItem.modKnockback(this.getKnockback(item, owner));
        return bulletItem.getProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }
}
