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
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;

import static rangedarsenal.rangedarsenal.SEED_AMMO_TYPES;

public class SeedGunMega extends GunProjectileToolItem {
    public SeedGunMega() {
        super(SEED_AMMO_TYPES, 1400);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(124).setUpgradedValue(1.0F, 111).setUpgradedValue(2.0F, 98).setUpgradedValue(3.0F, 90).setUpgradedValue(4.0F, 82);
        this.attackDamage.setBaseValue(41.0F);
        this.attackXOffset = 10;
        this.attackYOffset = 10;
        this.moveDist = 50;
        this.attackRange.setBaseValue(771);
        this.velocity.setBaseValue(389);
        this.resilienceGain.setBaseValue(0.5F);
        this.knockback.setBaseValue(10);
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        //super.addAmmoTooltips(tooltips, item);
        tooltips.add(Localization.translate("itemtooltip", "SeedgunTip"));
        tooltips.add(Localization.translate("itemtooltip", "SeedgunTip2"));
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.grass, SoundEffect.effect(mob).volume(0.94f).pitch(GameRandom.globalRandom.getFloatBetween(1.6f, 1.7f)));;
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        int bulletID = contentReader.getNextShortUnsigned();
        if (bulletID != 65535) {
            Item bullet = ItemRegistry.getItem(bulletID);
            if (bullet != null) {
                GameRandom random = new GameRandom((long)(seed + 5));
                boolean consumeAmmo = true;
                if (!consumeAmmo || player.getInv().main.removeItems(level, player, bullet, 1, "bulletammo") >= 1) {
                    //player.getInv().removeItems(bullet,1,true,true,true,"bulletammo");
                    if (bullet.idData.getStringID().equalsIgnoreCase("firemoneseed") || bullet.idData.getStringID().equalsIgnoreCase("chilipepperseed")) {
                        bullet = ItemRegistry.getItem("Fire_Seed_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else if (bullet.idData.getStringID().equalsIgnoreCase("iceblossomseed")) {
                        bullet = ItemRegistry.getItem("Cold_Seed_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else if (bullet.idData.getStringID().equalsIgnoreCase("sunflowerseed")) {
                        bullet = ItemRegistry.getItem("Light_Seed_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else if (bullet.idData.getStringID().equalsIgnoreCase("riceseed")) {
                        bullet = ItemRegistry.getItem("Pierce_Seed_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else if (bullet.idData.getStringID().equalsIgnoreCase("kew_copper_seed")) {
                        bullet = ItemRegistry.getItem("Metal_Seed_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else if (bullet.idData.getStringID().equalsIgnoreCase("kew_iron_seed")) {
                        bullet = ItemRegistry.getItem("Metal_Seed_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else if (bullet.idData.getStringID().equalsIgnoreCase("kew_gold_seed")) {
                        bullet = ItemRegistry.getItem("Metal_Seed_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else if (bullet.idData.getStringID().equalsIgnoreCase("kew_tier_1_seed")) {
                        bullet = ItemRegistry.getItem("Essence_Seed_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else if (bullet.idData.getStringID().equalsIgnoreCase("kew_tier_2_seed")) {
                        bullet = ItemRegistry.getItem("Essence_Seed_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else if (bullet.idData.getStringID().equalsIgnoreCase("grassseed") || bullet.idData.getStringID().equalsIgnoreCase("swampgrassseed")) {
                        bullet = ItemRegistry.getItem("Grass_Seed_Bullet");
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else {
                        bullet = ItemRegistry.getItem("Seed_Bullet");
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
        Projectile projectile = this.getProjectile(item, bullet, player.x, player.y, (float)x, (float)y, range, player);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.getUniqueID(random);
        level.entityManager.projectiles.addHidden(projectile);
        if (this.moveDist != 0) {
            projectile.moveDist((double)this.moveDist);
        }

        projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 2F);
        if (level.isServer()) {
            level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
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
