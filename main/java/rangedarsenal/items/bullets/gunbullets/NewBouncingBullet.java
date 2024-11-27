package rangedarsenal.items.bullets.gunbullets;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.BouncingBulletProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import rangedarsenal.projectiles.bullets.FrozenBulletProjectile;
import rangedarsenal.projectiles.bullets.NewBouncingBulletProjectile;

public class NewBouncingBullet extends BulletItem {
    public NewBouncingBullet() {
        super(1000);
        this.damage = 7;
        this.rarity = Rarity.COMMON;
        this.stackSize = 5000;
    }

    public boolean overrideProjectile() {
        return true;
    }

    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new NewBouncingBulletProjectile(x, y, targetX, targetY, velocity, range * 4, damage, knockback, owner);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "bouncingammotip"));
        tooltips.add(Localization.translate("itemtooltip", "bouncingammotipnew"));
        return tooltips;
    }
}