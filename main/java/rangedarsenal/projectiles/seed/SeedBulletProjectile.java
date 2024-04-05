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

public class SeedBulletProjectile extends BulletProjectile {
    public SeedBulletProjectile() {
    }

    public SeedBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }

    public Color getParticleColor() {
        return new Color(0, 112, 0);
    }
    protected Color getWallHitColor() {
        return new Color(43, 77, 43);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(0, 91, 0), 25.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 125.0F, this.lightSaturation);
    }
}
