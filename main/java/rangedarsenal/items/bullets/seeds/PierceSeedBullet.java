package rangedarsenal.items.bullets.seeds;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import rangedarsenal.projectiles.seed.PierceSeedBulletProjectile;

public class PierceSeedBullet extends BulletItem {
    public PierceSeedBullet() {
        super(1000);
        this.armorPen = 1;
        this.rarity = Rarity.NORMAL;
    }
    public boolean overrideProjectile() {
        return true;
    }
    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new PierceSeedBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("bullettooltip", "seedbullettip"));
        tooltips.add(Localization.translate("bullettooltip", "seedbullettip2"));
        return tooltips;
    }
}