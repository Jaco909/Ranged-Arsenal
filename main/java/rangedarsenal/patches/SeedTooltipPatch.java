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
import necesse.inventory.item.bulletItem.BouncingBulletItem;
import necesse.inventory.item.placeableItem.objectItem.SeedObjectItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SeedGunProjectileToolItem;
import necesse.level.gameObject.SeedObject;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;

@ModMethodPatch(target = SeedObjectItem.class, name = "getTooltips", arguments = {InventoryItem.class, PlayerMob.class, GameBlackboard.class})
public class SeedTooltipPatch {
    @Advice.OnMethodExit()
    static void onExit(@Advice.This SeedObjectItem seed, @Argument(0) InventoryItem item, @Argument(1) PlayerMob perspective, @Argument(2) GameBlackboard blackboard, @Advice.Return(readOnly = false) ListGameTooltips tooltips){
        if (perspective != null) {
            if ((perspective.getInv().main.getFirstItem(perspective.getLevel(), perspective, new Item[]{ItemRegistry.getItem("SeedGun")}, "bulletammo") != null) || (perspective.getInv().main.getFirstItem(perspective.getLevel(), perspective, new Item[]{ItemRegistry.getItem("SeedGunShotgun")}, "bulletammo") != null) || (perspective.getInv().main.getFirstItem(perspective.getLevel(), perspective, new Item[]{ItemRegistry.getItem("SeedGunMega")}, "bulletammo") != null)) {
                if (perspective.getInv().main.getFirstItem(perspective.getLevel(), perspective, new Item[]{ItemRegistry.getItem("seedgun")}, "bulletammo") != null) {
                    return;
                } else {
                    tooltips.add(GameColor.ITEM_QUEST.getColorCode() + Localization.translate("itemtooltip", seed.getStringID() + "ammotip"));
                }
            }
        }
    }
}
