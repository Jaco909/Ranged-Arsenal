package rangedarsenal.items.bullets.food;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import rangedarsenal.projectiles.food.BeetBulletProjectile;

public class BeetFoodBullet extends BulletItem {
    public BeetFoodBullet() {
        super(1000);
        this.damage = 9;
        this.armorPen = 2;
        this.rarity = Rarity.NORMAL;
    }
    public boolean overrideProjectile() {
        return true;
    }
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new BeetBulletProjectile(x, y, targetX, targetY, velocity*1.15f, Math.round(range*1.15f), damage, knockback, owner);
    }
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("bullettooltip", "seedbullettip"));
        tooltips.add(Localization.translate("bullettooltip", "seedbullettip2"));
        return tooltips;
    }
}
