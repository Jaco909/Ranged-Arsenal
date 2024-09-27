package rangedarsenal.items.materials;

import necesse.inventory.item.matItem.MatItem;

public class MechanicalPartsGood extends MatItem {

    public MechanicalPartsGood() {
        super(100, Rarity.COMMON);
        this.setItemCategory(new String[]{"materials"});
    }

}