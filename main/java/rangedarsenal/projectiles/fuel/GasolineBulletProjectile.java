package rangedarsenal.projectiles.fuel;

import java.awt.Color;

import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry.Debuffs;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;
import rangedarsenal.buffs.*;

public class GasolineBulletProjectile extends BulletProjectile {
    public GasolineBulletProjectile() {
    }

    public GasolineBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }

    public void init() {
        super.init();
        this.particleSpeedMod = 0.5F;
        this.piercing = 100;
        this.canBounce = false;
    }
    public Color getParticleColor() {
        return new Color(230, 120, 0);
    }
    protected Color getWallHitColor() {
        return new Color(160, 50, 0);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(230, 100, 0), 25.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 50.0F, this.lightSaturation);
    }
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                ActiveBuff ab = new ActiveBuff("FlamerSlow", mob, 0.1F, this.getOwner());
                ActiveBuff ab2 = new ActiveBuff("GasolineDebuff", mob, 5.0F, this.getOwner());
                mob.addBuff(ab, true);
                mob.addBuff(ab2, true);
            }
        }
    }
    //prevent wall hit sounds
    public void playHitSound(float x, float y) {
    }
}
