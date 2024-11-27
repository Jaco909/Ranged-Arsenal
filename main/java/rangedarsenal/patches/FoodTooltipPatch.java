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
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;

@ModMethodPatch(target = FoodConsumableItem.class, name = "getTooltips", arguments = {InventoryItem.class, PlayerMob.class, GameBlackboard.class})
public class FoodTooltipPatch {
    @Advice.OnMethodExit()
    static void onExit(@Advice.This FoodConsumableItem food, @Argument(0) InventoryItem item, @Argument(1) PlayerMob perspective, @Argument(2) GameBlackboard blackboard, @Advice.Return(readOnly = false) ListGameTooltips tooltips) {
        if (perspective != null) {
            if ((perspective.getInv().main.getFirstItem(perspective.getLevel(), perspective, new Item[]{ItemRegistry.getItem("ProduceCannon")}, "bulletammo") != null) || (perspective.getInv().main.getFirstItem(perspective.getLevel(), perspective, new Item[]{ItemRegistry.getItem("ProduceCannonMega")}, "bulletammo") != null)) {
                if (ItemRegistry.itemExists(food.getStringID().replaceFirst(Character.toString(food.getStringID().charAt(0)), Character.toString(food.getStringID().charAt(0)).toUpperCase()) + "_Food_Bullet")) {
                    tooltips.add(GameColor.ITEM_QUEST.getColorCode() + Localization.translate("itemtooltip", food.getStringID() + "ammotip"));
                }
            }
        }
    }
}
