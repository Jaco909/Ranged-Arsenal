package rangedarsenal.items.misc;

public class AmmoBagPlus extends AmmoPouchPlus {
    public AmmoBagPlus() {
        this.rarity = Rarity.RARE;
    }

    public int getInternalInventorySize() {
        return 20;
    }
}