package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.SeedPouch;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;

@ModMethodPatch(target = SeedPouch.class, name = "isValidRequestItem", arguments = {Item.class})
public class SeedPouchPatch {
    @Advice.OnMethodEnter(
            skipOn = Advice.OnNonDefaultValue.class
    )
    static boolean onEnter() {
        return true;
    }
    @Advice.OnMethodExit()
    static boolean onExit(@Advice.This SeedPouch pouch, @Argument(0) Item item, @Advice.Return(readOnly = false) boolean itemtype){
        if (item == null) {
            itemtype = !pouch.isValidRequestType(null);
            return itemtype;
        } else {
            itemtype = pouch.isValidRequestType(item.type);
            return itemtype;
        }
    }
}
