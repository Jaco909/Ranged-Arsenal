package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.HandGunBulletProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.ShardCannonProjectileToolItem;
import necesse.level.maps.Level;
import rangedarsenal.projectiles.modifiers.CrystalizeModifier;

public class ShardCannonRework extends ShardCannonProjectileToolItem implements ItemInteractAction {
    public ShardCannonRework() {
        this.attackDamage.setBaseValue(25.0F).setUpgradedValue(1.0F, 25.0F);
        this.velocity.setBaseValue(300);
        this.attackRange.setBaseValue(2000);
    }
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "shardcannontipnew"));
        return tooltips;
    }
    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        float distance = GameMath.diamondDistance(attackerMob.x, attackerMob.y, (float)x, (float)y);
        float t = 20.0F / distance;
        float projectileX = (1.0F - t) * attackerMob.x + t * (float)x;
        float projectileY = (1.0F - t) * attackerMob.y + t * (float)y;
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        Projectile projectile;
        if (!attackerMob.isPlayer) {
            projectile = this.getProjectile(item, (BulletItem) ItemRegistry.getItem("crystalbullet"), attackerMob.x, attackerMob.y, (float)x, (float)y, this.getAttackRange(item), attackerMob);
        } else {
            projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, this.getAttackRange(item), attackerMob);
        }
        projectile.setModifier(new CrystalizeModifier(this.getResilienceGain(item)));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(random);
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist, spreadRandom.getFloatOffset(0.0F, 3.0F));
    }

    public void playFireSound(AttackAnimMob mob) {
    }

    public Projectile getNormalProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage toolItemDamage, int knockback, ItemAttackerMob attackerMob) {
        return new HandGunBulletProjectile(x, y, targetX, targetY, velocity, range, toolItemDamage, knockback, attackerMob);
    }

    public Projectile getProjectile(InventoryItem item, BulletItem bulletItem, float x, float y, float targetX, float targetY, int range, ItemAttackerMob attackerMob) {
        float velocity = bulletItem.modVelocity((float)this.getProjectileVelocity(item, attackerMob));
        range = bulletItem.modRange(range);
        GameDamage damage = bulletItem.modDamage(this.getAttackDamage(item));
        int knockback = bulletItem.modKnockback(this.getKnockback(item, attackerMob));
        return bulletItem.overrideProjectile() ? bulletItem.getProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, attackerMob) : this.getNormalProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, attackerMob);
    }
}