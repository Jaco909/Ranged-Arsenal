package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PotionPouch;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;

@ModMethodPatch(target = PotionPouch.class, name = "isValidRequestItem", arguments = {Item.class})
public class PotionPouchPatch {
    @Advice.OnMethodEnter(
            skipOn = Advice.OnNonDefaultValue.class
    )
    static boolean onEnter() {
        return true;
    }
    @Advice.OnMethodExit()
    static boolean onExit(@Advice.This PotionPouch pouch, @Argument(0) Item item, @Advice.Return(readOnly = false) boolean itemtype){
        if (item == null) {
            itemtype = false;
            return itemtype;
        } else {
            itemtype = item.isPotion();
            return itemtype;
        }
    }
}
