package rangedarsenal.scripts;

import java.awt.Color;
import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.hudManager.HudManager;
import necesse.level.maps.hudManager.floatText.ItemPickupText;

public class ItemPickupTextSpecial extends ItemPickupText {
    //override ammo bag text color & value
    private InventoryItem iteam;
    public ItemPickupTextSpecial(int x, int y, InventoryItem item) {
        super(x + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), y + (int)(GameRandom.globalRandom.nextGaussian() * 4.0), item);
    }
    public ItemPickupTextSpecial(Mob mob, InventoryItem itemz, Color avg) {
        this(mob.getX(), mob.getY() - 16, itemz);
        this.fontOptions = (new FontOptions(16)).outline().color(avg);
        this.iteam = itemz;
        this.iteam.setAmount(0);
        this.updateText(itemz);
    }
    public void updateText(InventoryItem itemz) {
        this.setText(itemz.getItemDisplayName());
    }
    public void init(HudManager manager) {
        super.init(manager);
        manager.removeElements((element) -> {
            if (element.isRemoved()) {
                return false;
            } else {
                if (element != this && element instanceof ItemPickupText) {
                    ItemPickupText other = (ItemPickupText)element;
                    if (other.getItemID() == this.getItemID()) {
                        return true;
                    }
                }

                return false;
            }
        });
    }
    public void updateText() {
        return;
    }
    /*public void addThis(Level level, ArrayList<HudDrawElement> elements) {
        for(int i = 0; i < elements.size(); ++i) {
            HudDrawElement oText = (HudDrawElement)elements.get(i);
            if (!oText.isRemoved() && oText != this && oText instanceof ItemPickupText) {
                ItemPickupText other = (ItemPickupText)oText;
                if (other.getItemID() == this.getItemID()) {
                    elements.remove(i);
                    --i;
                }
            }
        }

        super.addThis(level, elements);
    }*/
}
