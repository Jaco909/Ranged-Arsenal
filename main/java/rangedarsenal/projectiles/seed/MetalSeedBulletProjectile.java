package rangedarsenal.projectiles.seed;

import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;

import java.awt.*;

public class MetalSeedBulletProjectile extends BulletProjectile {
    public MetalSeedBulletProjectile() {
    }

    public MetalSeedBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed*0.8f, distance, damage, knockback, owner);
    }
    public void init() {
        super.init();
    }

    public Color getParticleColor() {
        return new Color(115, 115, 115);
    }
    protected Color getWallHitColor() {
        return new Color(54, 54, 54);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(108, 108, 108), 15.0F, 100, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
}
