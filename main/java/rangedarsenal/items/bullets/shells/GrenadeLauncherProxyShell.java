package rangedarsenal.items.bullets.shells;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import rangedarsenal.projectiles.shells.GrenadeLauncherProxyProjectile;

public class GrenadeLauncherProxyShell extends BulletItem {
    public GrenadeLauncherProxyShell() {
        super(100);
        this.damage = 300;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("bullettooltip", "GrenadeLauncherProxyBullettip"));
        tooltips.add(Localization.translate("bullettooltip", "GrenadeLauncherProxyBullettip3"));
        tooltips.add(Localization.translate("bullettooltip", "GrenadeLauncherProxyBullettip2"));
        return tooltips;
    }

    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new GrenadeLauncherProxyProjectile(x, y, targetX, targetY, velocity*1.5f, range, (new GameDamage(-90)), knockback, owner);
    }

    public boolean overrideProjectile() {
        return true;
    }
}