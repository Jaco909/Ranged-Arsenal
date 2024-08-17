package rangedarsenal.items.materials;

import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.matItem.MatItem;

public class Niter extends MatItem {

    public Niter() {
        super(100, Rarity.NORMAL);
        //this.keyWords.add(0,"!wack!");
    }
    /*public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(this.keyWords.get(0));
        return tooltips;
    }*/
}
