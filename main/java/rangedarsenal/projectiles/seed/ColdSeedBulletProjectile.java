package rangedarsenal.projectiles.seed;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;

public class ColdSeedBulletProjectile extends BulletProjectile {
    public ColdSeedBulletProjectile() {
    }

    public ColdSeedBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                ActiveBuff ab = new ActiveBuff(BuffRegistry.Debuffs.FROSTSLOW, mob, 1.0F, this.getOwner());
                mob.addBuff(ab, true);
            }
        }
    }
    public Color getParticleColor() {
        return new Color(7, 176, 206);
    }
    protected Color getWallHitColor() {
        return new Color(3, 109, 128);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(78, 172, 189), 25.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 180.0F, this.lightSaturation);
    }
}
