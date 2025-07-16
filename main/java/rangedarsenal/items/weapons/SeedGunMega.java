package rangedarsenal.items.weapons;

import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
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
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.SeedBulletProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
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

public class SeedGunMega extends GunProjectileToolItem {
    protected float fasterAttackAnimModifier = 0.75F;
    public SeedGunMega() {
        super(SEED_AMMO_TYPES, 1400);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(124).setUpgradedValue(1.0F, 111).setUpgradedValue(2.0F, 98).setUpgradedValue(3.0F, 90).setUpgradedValue(4.0F, 82).setUpgradedValue(5.0F, 79);
        this.attackDamage.setBaseValue(31.0F).setUpgradedValue(1.0F, 32).setUpgradedValue(2.0F, 33).setUpgradedValue(3.0F, 34).setUpgradedValue(4.0F, 35).setUpgradedValue(5.0F, 36);
        this.attackXOffset = 10;
        this.attackYOffset = 10;
        this.moveDist = 50;
        this.attackRange.setBaseValue(400);
        this.velocity.setBaseValue(321);
        this.resilienceGain.setBaseValue(0.1F);
        this.knockback.setBaseValue(10);
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
        SoundManager.playSound(GameResources.grass, SoundEffect.effect(mob).volume(0.94f).pitch(GameRandom.globalRandom.getFloatBetween(1.6f, 1.7f)));;
    }

    public Projectile getNormalProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage toolItemDamage, int knockback, ItemAttackerMob attackerMob) {
        return new SeedBulletProjectile(x, y, targetX, targetY, velocity, range, toolItemDamage, knockback, attackerMob);
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int bulletID = mapContent.getShortUnsigned("bulletID", 65535);
        if (bulletID != 65535) {
            Item seedObjectItem = ItemRegistry.getItem(bulletID);
            if (seedObjectItem != null && seedObjectItem.type == Type.SEED) {
                GameRandom random = new GameRandom((long)(seed + 5));
                float ammoConsumeChance = this.getAmmoConsumeChance(attackerMob, item);
                boolean consumeAmmo = ammoConsumeChance >= 1.0F || ammoConsumeChance > 0.0F && random.getChance(ammoConsumeChance);
                boolean dropItem;
                boolean shouldFire;
                if (!consumeAmmo) {
                    shouldFire = true;
                    dropItem = false;
                } else if (attackerMob instanceof AmmoUserMob) {
                    AmmoConsumed consumed = ((AmmoUserMob)attackerMob).removeAmmo(seedObjectItem, 1, "bulletammo");
                    shouldFire = consumed.amount >= 1;
                    dropItem = random.getChance(consumed.dropChance);
                } else {
                    shouldFire = true;
                    dropItem = false;
                }

                if (shouldFire) {
                    this.fireSeedProjectiles(level, x, y, attackerMob, item, seed, (SeedObjectItem)seedObjectItem, dropItem, mapContent);
                    boolean isAttackSpeedBullet = seedObjectItem.getStringID().equals("riceseed") || seedObjectItem.getStringID().equals("strawberryseed");
                    item.getGndData().setBoolean("attackSpeedBullet", isAttackSpeedBullet);
                }
            } else {
                GameLog.warn.println(attackerMob.getDisplayName() + " tried to use item " + (seedObjectItem == null ? bulletID : seedObjectItem.getStringID()) + " as seed seedObjectItem.");
            }
        }

        return item;
    }
    protected void fireSeedProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, SeedObjectItem seedObjectItem, boolean dropItem, GNDItemMap mapContent) {
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(new GameRandom((long)(seed + 10)), attackerMob, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)attackerMob.getDistance((float)x, (float)y);
        } else {
            range = this.getAttackRange(item);
        }

        Item seedBullet = ItemRegistry.getItem("seedbullet");
        Projectile projectile = this.getProjectile(item, (SeedBulletItem)seedBullet, attackerMob.x, attackerMob.y, (float)x, (float)y, range, attackerMob);
        ((SeedBulletProjectile)projectile).setSeedBulletVariant(seedObjectItem);
        String seedName = seedObjectItem.getStringID();
        float resGain = !seedName.equals("cornseed") && !seedName.equals("wheatseed") ? this.getResilienceGain(item) : this.getResilienceGain(item) + 1.0F;
        projectile.setModifier(new ResilienceOnHitProjectileModifier(resGain));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
    }
}
