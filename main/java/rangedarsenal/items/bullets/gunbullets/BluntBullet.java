package rangedarsenal.items.bullets.gunbullets;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.bulletItem.BulletItem;
import rangedarsenal.projectiles.bullets.BluntBulletProjectile;

public class BluntBullet extends BulletItem {
    public BluntBullet() {
        super(1000);
        this.damage = 7;
        this.armorPen = 2;
        this.rarity = Rarity.COMMON;
        this.stackSize = 5000;
    }

    public boolean overrideProjectile() {
        return true;
    }

    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        knockback = Math.round(knockback*1.25f);
        if (knockback == 0) {
            knockback = 15;
        }
        return new BluntBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("bullettooltip", "bluntbullettip"));
        tooltips.add(Localization.translate("bullettooltip", "bluntbullettip2"));
        tooltips.add(Localization.translate("bullettooltip", "bluntbullettip3"));
        return tooltips;
    }
}