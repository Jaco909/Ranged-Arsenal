package rangedarsenal.items.misc;

import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PotionPouch;

public class PotionBagFix extends PotionPouchFix {
    public PotionBagFix() {
        this.rarity = Rarity.RARE;
    }

    public int getInternalInventorySize() {
        return 20;
    }
}