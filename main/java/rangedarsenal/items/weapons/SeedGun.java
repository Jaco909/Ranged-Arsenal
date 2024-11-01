package rangedarsenal.items.weapons;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.SeedBulletProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.bulletItem.SeedBulletItem;
import necesse.inventory.item.placeableItem.objectItem.SeedObjectItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;

import static necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SeedGunProjectileToolItem.SEED_AMMO_TYPES;

public class SeedGun extends GunProjectileToolItem {
    protected float fasterAttackAnimModifier = 0.75F;
    public SeedGun() {
        super(SEED_AMMO_TYPES, 300);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(342);
        this.attackDamage.setBaseValue(24.0F).setUpgradedValue(1.0F, 112F);
        this.attackXOffset = 10;
        this.attackYOffset = 10;
        this.attackRange.setBaseValue(500).setUpgradedValue(1.0F, 1000);
        this.velocity.setBaseValue(152).setUpgradedValue(1.0F, 300);
        this.knockback.setBaseValue(0);
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "seedguntip"));
        return tooltips;
    }

    public int getFlatAttackAnimTime(InventoryItem item) {
        int attackTime = super.getFlatAttackAnimTime(item);
        if (item.getGndData().getBoolean("attackSpeedBullet")) {
            attackTime = (int)((float)attackTime * this.fasterAttackAnimModifier);
        }

        return attackTime;
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.handgun, SoundEffect.effect(mob).volume(0.37f).pitch(1f));
        SoundManager.playSound(GameResources.grass, SoundEffect.effect(mob).volume(2f).pitch(GameRandom.globalRandom.getFloatBetween(1.3f, 1.8f)));;
    }

    public Projectile getNormalProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage toolItemDamage, int knockback, Mob owner) {
        return new SeedBulletProjectile(x, y, targetX, targetY, velocity, range, toolItemDamage, knockback, owner);
    }

    public GameMessage getSettlerCanUseError(HumanMob mob, InventoryItem item) {
        return new LocalMessage("ui", "settlercantuseitem");
    }

    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        int bulletID = contentReader.getNextShortUnsigned();
        if (bulletID != 65535) {
            Item seedObjectItem = ItemRegistry.getItem(bulletID);
            if (seedObjectItem != null && seedObjectItem.type == Type.SEED) {
                GameRandom random = new GameRandom((long)(seed + 5));
                float ammoConsumeChance = this.getAmmoConsumeChance(player, item);
                boolean consumeAmmo = ammoConsumeChance >= 1.0F || ammoConsumeChance > 0.0F && random.getChance(ammoConsumeChance);
                if (!consumeAmmo || player.getInv().main.removeItems(level, player, seedObjectItem, 1, "bulletammo") >= 1) {
                    this.fireSeedProjectiles(level, x, y, player, item, seed, (SeedObjectItem)seedObjectItem, consumeAmmo, contentReader);
                    boolean isAttackSpeedBullet = seedObjectItem.getStringID().equals("riceseed") || seedObjectItem.getStringID().equals("strawberryseed");
                    item.getGndData().setBoolean("attackSpeedBullet", isAttackSpeedBullet);
                }
            } else {
                GameLog.warn.println(player.getDisplayName() + " tried to use item " + (seedObjectItem == null ? bulletID : seedObjectItem.getStringID()) + " as seed seedObjectItem.");
            }
        }

        return item;
    }

    protected void fireSeedProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, SeedObjectItem seedObjectItem, boolean consumeAmmo, PacketReader contentReader) {
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(new GameRandom((long)(seed + 10)), player, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)player.getDistance((float)x, (float)y);
        } else {
            range = this.getAttackRange(item);
        }

        Item seedBullet = ItemRegistry.getItem("seedbullet");
        Projectile projectile = this.getProjectile(item, (SeedBulletItem)seedBullet, player.x, player.y, (float)x, (float)y, range, player);
        ((SeedBulletProjectile)projectile).setSeedBulletVariant(seedObjectItem);
        String seedName = seedObjectItem.getStringID();
        float resGain = !seedName.equals("cornseed") && !seedName.equals("wheatseed") ? this.getResilienceGain(item) : this.getResilienceGain(item) + 1.0F;
        projectile.setModifier(new ResilienceOnHitProjectileModifier(resGain));
        projectile.dropItem = consumeAmmo;
        projectile.getUniqueID(new GameRandom((long)seed));
        level.entityManager.projectiles.addHidden(projectile);
        if (this.moveDist != 0) {
            projectile.moveDist((double)this.moveDist);
        }

        if (level.isServer()) {
            level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
        }
    }
}
