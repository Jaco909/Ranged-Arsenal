package rangedarsenal.patches;

import necesse.engine.localization.Localization;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;

@ModMethodPatch(target = Item.class, name = "getTooltips", arguments = {InventoryItem.class, PlayerMob.class, GameBlackboard.class})
public class FoodExtraTooltipPatch {
    @Advice.OnMethodExit()
    static void onExit(@Advice.This Item food, @Argument(0) InventoryItem item, @Argument(1) PlayerMob perspective, @Argument(2) GameBlackboard blackboard, @Advice.Return(readOnly = false) ListGameTooltips tooltips){
        if (perspective != null) {
            if ((perspective.getInv().main.getFirstItem(perspective.getLevel(), perspective, new Item[]{ItemRegistry.getItem("ProduceCannon")}, "bulletammo") != null) || (perspective.getInv().main.getFirstItem(perspective.getLevel(), perspective, new Item[]{ItemRegistry.getItem("ProduceCannonMega")}, "bulletammo") != null)) {
                if (food.getStringID().equalsIgnoreCase("sugarbeet") || food.getStringID().equalsIgnoreCase("wheat") || food.getStringID().equalsIgnoreCase("coffeebeans") || food.getStringID().equalsIgnoreCase("rice")) {
                    tooltips.add(GameColor.ITEM_QUEST.getColorCode() + Localization.translate("itemtooltip", food.getStringID() + "ammotip"));
                }
            }
        }
    }
}
