package rangedarsenal.items.bullets.fuel;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import rangedarsenal.projectiles.fuel.NapalmBulletProjectile;

public class NapalmBullet extends BulletItem {
    public NapalmBullet() {
        super(1000);
        this.damage = 5;
        this.armorPen = 7;
        this.rarity = Item.Rarity.COMMON;
    }

    public boolean overrideProjectile() {
        return true;
    }

    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, Mob owner) {
        range = Math.round(range/1.3f);
        velocity = Math.round(velocity/1.35f);
        return new NapalmBulletProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("bullettooltip", "NapalmTip"));
        tooltips.add(Localization.translate("itemtooltip", "FlamethrowerAmmoTip"));
        tooltips.add(Localization.translate("bullettooltip", "NapalmTip2"));
        tooltips.add(Localization.translate("bullettooltip", "NapalmTip3"));
        return tooltips;
    }
}