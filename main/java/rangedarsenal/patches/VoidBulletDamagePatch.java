package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.entity.mobs.friendly.human.humanShop.GunsmithHumanMob;
import necesse.inventory.item.bulletItem.VoidBulletItem;
import net.bytebuddy.asm.Advice;

@ModConstructorPatch(
        target = VoidBulletItem.class,
        arguments = {}
)
public class VoidBulletDamagePatch {
    public VoidBulletDamagePatch() {
    }
    @Advice.OnMethodExit()
    static void onExit(@Advice.This VoidBulletItem item) {
        item.damage = 6;
    }
}