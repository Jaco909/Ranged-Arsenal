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
import rangedarsenal.projectiles.food.BlueberryBulletProjectile;

public class BlueberryFoodBullet extends BulletItem {
    public BlueberryFoodBullet() {
        super(1000);
        this.damage = 0;
        this.rarity = Rarity.NORMAL;
    }
    public boolean overrideProjectile() {
        return true;
    }
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new BlueberryBulletProjectile(x, y, targetX, targetY, velocity * 2, range, damage, knockback, owner);
    }
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("bullettooltip", "seedbullettip"));
        tooltips.add(Localization.translate("bullettooltip", "seedbullettip2"));
        return tooltips;
    }
}
