package rangedarsenal.projectiles.seed;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;

import java.awt.*;

public class PierceSeedBulletProjectile extends BulletProjectile {
    public PierceSeedBulletProjectile() {
    }

    public PierceSeedBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    public void init() {
        super.init();
        this.particleSpeedMod = 1.08F;
        this.speed = 189F;
    }

    public Color getParticleColor() {
        return new Color(185, 245, 185);
    }
    protected Color getWallHitColor() {
        return new Color(80, 96, 80);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(91, 134, 91), 15.0F, 100, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 153.0F, this.lightSaturation);
    }
}
