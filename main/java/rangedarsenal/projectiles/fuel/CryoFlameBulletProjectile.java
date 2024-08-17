package rangedarsenal.projectiles.fuel;

import java.awt.*;
import java.awt.geom.Point2D;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

public class CryoFlameBulletProjectile extends BulletProjectile {
    public CryoFlameBulletProjectile() {
    }

    public CryoFlameBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }


    public void init() {
        super.init();
        this.particleSpeedMod = 0.5F;
        this.piercing = 100;
    }
    public Color getParticleColor() {
        return new Color(90, 150, 250);
    }
    protected Color getWallHitColor() {
        return new Color(0, 130, 250);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(90, 150, 250), 7.0F, 30, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 210.0F, this.lightSaturation);
    }
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                float duration = mob.buffManager.getBuffDurationLeftSeconds("CryoBuildupDebuff");
                if (duration >= 29.0f) {
                    duration = 1f;
                    ActiveBuff ab4 = new ActiveBuff("CryoFreezeDebuff", mob, 5f, this.getOwner());
                    mob.buffManager.addBuff(ab4, true);
                    mob.buffManager.removeBuff("CryoBuildupDebuff",true);
                    SoundManager.playSound(GameResources.shatter1, SoundEffect.effect(mob)
                            .volume(1f)
                            .pitch(GameRandom.globalRandom.getFloatBetween(0.5f, 0.6f)));
                    /*for(int i = 0; i < 4; ++i) {
                        mob.getLevel().entityManager.addParticle(mob.x + GameRandom.globalRandom.getIntBetween(-10, 10), mob.y + GameRandom.globalRandom.getIntBetween(-10, 10), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(1, 2), 0, 12)).color(new Color(0, 190, 250)).sizeFadesInAndOut(24, 34, 50, 15).movesConstant(mob.dx / 10.0F, mob.dy / 10.0F).lifeTime(50).height(2.0F);
                    }*/
                }
                if (mob.buffManager.getBuffDurationLeftSeconds("CryoFreezeDebuff") > 0) {
                    mob.buffManager.removeBuff("CryoBuildupDebuff",true);
                    duration = 1f;
                }
                ActiveBuff ab = new ActiveBuff("FlamerSlow", mob, 0.1f, this.getOwner());
                ActiveBuff ab2 = new ActiveBuff("CryoBuildupDebuff", mob, duration+1f, this.getOwner());
                ActiveBuff ab3 = new ActiveBuff("FrostyDebuff", mob, 3F, this.getOwner());
                mob.addBuff(ab, true);
                mob.addBuff(ab2, true);
                mob.addBuff(ab3, true);
            }
        }
    }
    //prevent wall hit sounds
    public void playHitSound(float x, float y) {
    }
}
