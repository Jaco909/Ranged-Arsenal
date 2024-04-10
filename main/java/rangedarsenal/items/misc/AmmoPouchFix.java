package rangedarsenal.items.misc;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.Item.Rarity;
import necesse.inventory.item.Item.Type;
import necesse.inventory.item.miscItem.PouchItem;

public class AmmoPouchFix extends PouchItem {
    public AmmoPouchFix() {
        this.rarity = Rarity.UNCOMMON;
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "ammopouchtip1"));
        tooltips.add(Localization.translate("itemtooltip", "ammopouchtip2"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "storedammo", "items", this.getStoredItemAmounts(item)));
        return tooltips;
    }

    public boolean isValidPouchItem(InventoryItem item) {
        return this.isValidRequestType(item.item.type);
    }

    public boolean isValidRequestItem(Item item) {
        if (item == null) {
            return !this.isValidRequestType(null);
        } else {
            return this.isValidRequestType(item.type);
        }
    }

    public boolean isValidRequestType(Item.Type type) {
        return type == Type.ARROW || type == Type.BULLET;
    }

    public int getInternalInventorySize() {
        return 10;
    }
}