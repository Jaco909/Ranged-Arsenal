package rangedarsenal.scripts;

import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.entity.mobs.hostile.AncientSkeletonMob;
import necesse.inventory.item.bulletItem.BouncingBulletItem;
import net.bytebuddy.asm.Advice;

@ModConstructorPatch(target = AncientSkeletonMob.class, arguments = {})

public class SkeleDebug {
    @Advice.OnMethodExit
    static void onExit(@Advice.This AncientSkeletonMob mob) {
        mob.setSpeed(0.0001f);
        mob.setFriction(10.0F);
        mob.setMaxHealth(999999);
    }
}
