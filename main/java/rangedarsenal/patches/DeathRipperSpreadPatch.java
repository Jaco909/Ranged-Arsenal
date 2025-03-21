package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.BouncingBulletProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.DeathRipperProjectileToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;

import java.awt.*;

@ModMethodPatch(target = DeathRipperProjectileToolItem.class, name = "fireProjectiles", arguments = {Level.class, int.class, int.class, ItemAttackerMob.class, InventoryItem.class, int.class, BulletItem.class, boolean.class, GNDItemMap.class})
public class DeathRipperSpreadPatch {
    @Advice.OnMethodEnter(
            skipOn = Advice.OnNonDefaultValue.class
    )
    static boolean onEnter() {
        return true;
    }
    @Advice.OnMethodExit()
    static void onExit(@Advice.This DeathRipperProjectileToolItem thiss, @Argument(0) Level level, @Argument(1) int x, @Argument(2) int y, @Argument(3) ItemAttackerMob attackerMob, @Argument(4) InventoryItem item, @Argument(5) int seed, @Argument(6) BulletItem bullet, @Argument(7) boolean dropItem, @Argument(8) GNDItemMap mapContent){
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        int range;
        range = thiss.getAttackRange(item);

        Projectile projectile = thiss.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, range, attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(thiss.getResilienceGain(item)));
        projectile.setAngle(projectile.getAngle() + spreadRandom.getFloatOffset(0.0F, GameMath.limit(6.0F-thiss.getUpgradeTier(item),0,6)));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile(projectile, thiss.moveDist);
    }
}
