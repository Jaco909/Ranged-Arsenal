package rangedarsenal.items.bullets.gunbullets;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import rangedarsenal.projectiles.bullets.NewVoidBulletProjectile;

public class NewVoidBullet extends BulletItem {
    public NewVoidBullet() {
        super(1000);
        this.damage = 6;
        this.rarity = Rarity.UNCOMMON;
    }

    public boolean overrideProjectile() {
        return true;
    }

    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new NewVoidBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "voidammotip"));
        return tooltips;
    }
}