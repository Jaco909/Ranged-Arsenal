package rangedarsenal.items.bullets.shells;

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
import rangedarsenal.projectiles.shells.GrenadeLauncherProjectile;

public class GrenadeLauncherShell extends BulletItem {
    public GrenadeLauncherShell() {
        super(100);
        this.damage = 50;
        this.stackSize = 5000;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("bullettooltip", "GrenadeLauncherBullettip"));
        return tooltips;
    }

    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new GrenadeLauncherProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }

    public boolean overrideProjectile() {
        return true;
    }
}