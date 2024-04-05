package rangedarsenal.patches;

import necesse.engine.localization.Localization;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BouncingBulletItem;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.LevelObjectHit;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;

@ModMethodPatch(target = BouncingBulletItem.class, name = "getTooltips", arguments = {InventoryItem.class, PlayerMob.class, GameBlackboard.class})
public class BouncingTooltipPatch {
    @Advice.OnMethodExit()
    static void onExit(@Advice.This BouncingBulletItem bullet, @Argument(0) InventoryItem item, @Argument(1) PlayerMob player, @Argument(2) GameBlackboard blackboard, @Advice.Return(readOnly = false) ListGameTooltips tooltips){
        tooltips.add(Localization.translate("itemtooltip", "bouncingammotipnew"));
    }
}
