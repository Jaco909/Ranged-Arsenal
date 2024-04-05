package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.bulletProjectile.BouncingBulletProjectile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;

@ModMethodPatch(target = BouncingBulletProjectile.class, name = "doHitLogic", arguments = {Mob.class, LevelObjectHit.class, float.class, float.class})
public class BouncingProjectilePatch {
    @Advice.OnMethodExit()
    static void onExit(@Advice.This BouncingBulletProjectile bullet, @Argument(0) Mob mob, @Argument(1) LevelObjectHit object){
        if (mob == null) {
            bullet.setDamage(new GameDamage(bullet.getDamage().damage*1.1f,bullet.getDamage().armorPen*1f,bullet.getDamage().baseCritChance+0.02f));
        }
    }
}
