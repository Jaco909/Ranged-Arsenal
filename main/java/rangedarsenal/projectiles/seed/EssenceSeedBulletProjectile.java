package rangedarsenal.projectiles.seed;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;

import java.awt.*;

public class EssenceSeedBulletProjectile extends BulletProjectile {
    public EssenceSeedBulletProjectile() {
    }

    public EssenceSeedBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed*1.3f, distance*2, damage, knockback+5, owner);
    }
    public void init() {
        super.init();
    }

    public Color getParticleColor() {
        return new Color(162, 46, 206, 255);
    }
    protected Color getWallHitColor() {
        return new Color(70, 14, 91);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(172, 118, 192), 15.0F, 100, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 283.0F, this.lightSaturation);
    }
}
