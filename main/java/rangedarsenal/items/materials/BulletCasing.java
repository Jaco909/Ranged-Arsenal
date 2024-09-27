package rangedarsenal.items.materials;

import necesse.inventory.item.matItem.MatItem;

public class BulletCasing extends MatItem {

    public BulletCasing() {
        super(1000, Rarity.NORMAL);
        this.setItemCategory(new String[]{"materials"});
    }

}