package rangedarsenal.items.misc;

import java.util.Iterator;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.SlotPriority;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.Item.Rarity;
import necesse.inventory.item.miscItem.PotionPouch;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.inventory.item.placeableItem.consumableItem.AdventurePartyConsumableItem;
import necesse.level.maps.Level;

public class PotionPouchFix extends PotionPouch {

    public boolean isValidRequestItem(Item item) {
        if (item == null) {
            return !this.isValidRequestType(null);
        } else {
            return this.isValidRequestType(item.type);
        }
    }
}