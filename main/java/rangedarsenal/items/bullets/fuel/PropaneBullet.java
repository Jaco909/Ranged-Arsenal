package rangedarsenal.items.bullets.fuel;

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
import rangedarsenal.projectiles.fuel.GasolineBulletProjectile;
import rangedarsenal.projectiles.fuel.NapalmBulletProjectile;

public class PropaneBullet extends BulletItem {
    public PropaneBullet() {
        super(1000);
        this.damage = 7;
        this.armorPen = 15;
        this.rarity = Rarity.COMMON;
        this.stackSize = 5000;
    }

    public boolean overrideProjectile() {
        return true;
    }

    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        return new GasolineBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "FlamethrowerAmmoTip"));
        return tooltips;
    }
}