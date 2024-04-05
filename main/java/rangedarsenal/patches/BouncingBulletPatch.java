package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.inventory.item.bulletItem.BouncingBulletItem;
import net.bytebuddy.asm.Advice;

@ModConstructorPatch(target = BouncingBulletItem.class, arguments = {})

public class BouncingBulletPatch {
    @Advice.OnMethodExit
    static void onExit(@Advice.This BouncingBulletItem bullet) {
        bullet.damage = 7;
    }
}
