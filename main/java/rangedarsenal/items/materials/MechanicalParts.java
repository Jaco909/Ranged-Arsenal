package rangedarsenal.items.materials;

import necesse.inventory.item.matItem.MatItem;

public class MechanicalParts extends MatItem {

    public MechanicalParts() {
        super(100, Rarity.NORMAL);
        this.setItemCategory(new String[]{"materials"});
    }

}