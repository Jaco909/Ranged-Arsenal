package rangedarsenal.projectiles.seed;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;

public class FireSeedBulletProjectile extends BulletProjectile {
    public FireSeedBulletProjectile() {
    }

    public FireSeedBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                ActiveBuff ab = new ActiveBuff("HellfireBuff", mob, 1F, this.getOwner());
                mob.addBuff(ab, true);
            }
        }
    }
    public Color getParticleColor() {
        return new Color(208, 74, 1);
    }
    protected Color getWallHitColor() {
        return new Color(133, 55, 12);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(185, 126, 92), 25.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 22.0F, this.lightSaturation);
    }
}
