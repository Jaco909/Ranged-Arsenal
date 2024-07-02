package rangedarsenal.items.weapons;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.SapphireRevolverProjectile;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;
import rangedarsenal.scripts.SapphireRevolverAttackHandlerRework;

public class SapphireRevolverRework extends GunProjectileToolItem {
    public SapphireRevolverRework() {
        super(NORMAL_AMMO_TYPES, 500);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(25.0F).setUpgradedValue(1.0F, 160.0F);
        this.attackXOffset = 8;
        this.attackYOffset = 12;
        this.attackRange.setBaseValue(2000);
        this.velocity.setBaseValue(2000);
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    public float getAttackMovementMod(InventoryItem item) {
        if (this.getUpgradeTier(item) < 1) {
            return 0.60F;
        } else {
            return 0.60F+(this.getUpgradeTier(item)/100)*2;
        }
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "sapphirerevolvertip"));
        tooltips.add(Localization.translate("itemtooltip", "sapphirerevolvertip2"));
        return tooltips;
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        player.startAttackHandler(new SapphireRevolverAttackHandlerRework(player, slot, item, this, seed, x, y, this.getUpgradeTier(item)));
        return item;
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader, float charge) {
        int bulletID = contentReader.getNextShortUnsigned();
        if (bulletID != 65535) {
            Item bullet = ItemRegistry.getItem(bulletID);
            if (bullet != null && bullet.type == Type.BULLET) {
                GameRandom random = new GameRandom((long)(seed + 5));
                float ammoConsumeChance = ((BulletItem)bullet).getAmmoConsumeChance() * this.getAmmoConsumeChance(player, item);
                boolean consumeAmmo = ammoConsumeChance >= 1.0F || ammoConsumeChance > 0.0F && random.getChance(ammoConsumeChance);
                if (!consumeAmmo || player.getInv().main.removeItems(level, player, bullet, 1, "bulletammo") >= 1) {
                    this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader, charge);
                }
            } else {
                GameLog.warn.println(player.getDisplayName() + " tried to use item " + (bullet == null ? bulletID : bullet.getStringID()) + " as bullet.");
            }
        }
        return item;
    }

    protected void fireProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, BulletItem bullet, boolean consumeAmmo, PacketReader contentReader, float charge) {
        float distance = GameMath.diamondDistance(player.x, player.y, (float)x, (float)y);
        float t = 30.0F / distance;
        float projectileX = (1.0F - t) * player.x + t * (float)x;
        float projectileY = (1.0F - t) * player.y + t * (float)y;
        GameRandom random = new GameRandom((long)seed);
        Projectile projectile = this.getProjectile(item, bullet, player.x, player.y, (float)x, (float)y, 2000, player);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = consumeAmmo;
        projectile.getUniqueID(random);
        projectile.piercing = projectile.piercing+(int)Math.floor(charge)-1;
        projectile.setDamage(new GameDamage(projectile.getDamage().damage, projectile.getDamage().armorPen+(int)Math.floor(charge*3F)-3, projectile.getDamage().baseCritChance+((float)(Math.floor(charge)/11)-.09f)));
        level.entityManager.projectiles.addHidden(projectile);
        if (this.moveDist != 0) {
            projectile.moveDist((double)this.moveDist);
        }

        projectile.setAngle(projectile.getAngle());
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
        }
    }

    public void playFireSound(AttackAnimMob mob) {
    }
}