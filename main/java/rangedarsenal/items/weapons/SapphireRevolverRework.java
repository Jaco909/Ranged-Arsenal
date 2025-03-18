package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.SapphireRevolverAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.SapphireRevolverProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
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


    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new SapphireRevolverAttackHandlerRework(attackerMob, slot, item, this, seed, x, y, this.getUpgradeTier(item)));
        return item;
    }

    public InventoryItem superOnAttack(Level level, int attackX, int attackY, ItemAttackerMob attackerMob, int currentAttackHeight, InventoryItem attackItem, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, attackX, attackY, attackerMob, currentAttackHeight, attackItem, slot, animAttack, seed, mapContent);
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        float charge = item.getGndData().getFloat("charge", 1F);
        float distance = GameMath.diamondDistance(attackerMob.x, attackerMob.y, (float) x, (float) y);
        float t = 30.0F / distance;
        float projectileX = (1.0F - t) * attackerMob.x + t * (float) x;
        float projectileY = (1.0F - t) * attackerMob.y + t * (float) y;
        GameRandom random = new GameRandom((long) seed);
        Projectile projectile;
        if (!attackerMob.isPlayer) {
            projectile = this.getProjectile(item, (BulletItem) ItemRegistry.getItem("Ruby_Bullet"), attackerMob.x, attackerMob.y, (float) x, (float) y, 2000, attackerMob);
        } else {
            projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float) x, (float) y, 2000, attackerMob);
        }
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(random);
        projectile.piercing = projectile.piercing + (int) Math.floor(charge) - 1;
        projectile.setDamage(new GameDamage(projectile.getDamage().damage, projectile.getDamage().armorPen + (int) Math.floor(charge * 3F) - 3, projectile.getDamage().baseCritChance + ((float) (Math.floor(charge) / 11) - .09f)));
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
    }

    public void playFireSound(AttackAnimMob mob) {
    }
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (attackerMob.isClient() && item.getGndData().getBoolean("charged3")) {
            SoundManager.playSound(GameResources.shatter1, SoundEffect.effect(attackerMob).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.3F, 1.7F)));
            SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(attackerMob).volume(1.1F).pitch(GameRandom.globalRandom.getFloatBetween(0.4F, 0.6F)));
            SoundManager.playSound(GameResources.laserBlast1, SoundEffect.effect(attackerMob).volume(2.2F).pitch(GameRandom.globalRandom.getFloatBetween(0.95F, 1.25F)));
        } else if (attackerMob.isClient() && item.getGndData().getBoolean("charged2")) {
            SoundManager.playSound(GameResources.shatter1, SoundEffect.effect(attackerMob).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.3F, 1.7F)));
            SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(attackerMob).volume(0.7F).pitch(GameRandom.globalRandom.getFloatBetween(0.4F, 0.6F)));
            SoundManager.playSound(GameResources.laserBlast1, SoundEffect.effect(attackerMob).volume(1.5F).pitch(GameRandom.globalRandom.getFloatBetween(0.75F, 0.9F)));
        } else if (attackerMob.isClient() && item.getGndData().getBoolean("charged")) {
            SoundManager.playSound(GameResources.shatter1, SoundEffect.effect(attackerMob).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.3F, 1.7F)));
            SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(attackerMob).volume(0.5F).pitch(GameRandom.globalRandom.getFloatBetween(0.4F, 0.6F)));
            SoundManager.playSound(GameResources.laserBlast1, SoundEffect.effect(attackerMob).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.45F, 0.65F)));
        }

    }
}