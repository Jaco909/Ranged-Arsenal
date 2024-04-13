package rangedarsenal.items.misc;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;

public class AmmoBagFix extends AmmoPouchFix {
    public AmmoBagFix() {
        this.rarity = Item.Rarity.RARE;
    }

    public int getInternalInventorySize() {
        return 20;
    }
}