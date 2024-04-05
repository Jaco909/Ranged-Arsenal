package rangedarsenal.projectiles.bullets;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.hudManager.floatText.DamageText;

import java.awt.*;

public class LeachBulletProjectile extends BulletProjectile {
    public LeachBulletProjectile() {
    }
    public LeachBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }

    public void init() {
        super.init();
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                if (!this.getOwner().buffManager.hasBuff("HealDelayBuff")) {
                    int damage = Math.round(this.getDamage().damage);
                    int armor = mob.getArmorFlat();
                    int result = damage - Math.round((armor - 4) / 2f);
                    if (result <= 0) {
                        result = 1;
                    }
                    result = Math.round(result / 3f);
                    if (result <= 1) {
                        result = 1;
                    } else if (result >= 5) {
                        result = 5;
                    }
                    this.getOwner().setHealth(this.getOwner().getHealth() + result);
                    if (this.getOwner().getResilience() < this.getOwner().getMaxResilience()) {
                        this.getOwner().addResilience(result * 1.5f);
                    }
                    ActiveBuff ab = new ActiveBuff("HealDelayBuff", mob, 0.25F, this.getOwner());
                    this.getOwner().addBuff(ab, true);
                    this.getOwner().spawnDamageText(result,16,false);
                    this.getOwner().getLevel().hudManager.addElement(new DamageText(this.getOwner().getX(), this.getOwner().getY(), result, (new FontOptions(16)).color(new Color(0, 178, 0)), 35));
                }

            }
        }
    }
}
