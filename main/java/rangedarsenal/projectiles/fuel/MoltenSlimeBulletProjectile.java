package rangedarsenal.projectiles.fuel;

import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.explosionEvent.BloatedSpiderExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;
import rangedarsenal.events.SlimeSplosionEvent;

import java.awt.*;

import static necesse.entity.mobs.hostile.BloatedSpiderMob.explosionDamage;

public class MoltenSlimeBulletProjectile extends BulletProjectile {
    public MoltenSlimeBulletProjectile() {
    }

    public MoltenSlimeBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }


    public void init() {
        super.init();
        this.particleSpeedMod = 0.5F;
        this.piercing = 5;
    }
    public Color getParticleColor() {
        return new Color(60, 180, 70);
    }
    protected Color getWallHitColor() {
        return new Color(0, 180, 0);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(60, 180, 0), 25.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 130.0F, this.lightSaturation);
    }
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                float duration = mob.buffManager.getBuffDurationLeftSeconds("SlimeSplosionDebuff");
                int buildup = 0;
                float pentup = 0f;
                if (duration >= 10.0f && mob.getHealthPercent() <= 0.15f) {
                    if(!mob.isBoss()) {
                        buildup = Math.round(duration*3.25f);
                        pentup = duration*5.5f;
                        duration = 1f;
                        mob.setHealth(1);
                        mob.buffManager.removeBuff("SlimeSplosionDebuff", true);
                        explosionDamage = new GameDamage(pentup,20.0F);
                        SlimeSplosionEvent event = new SlimeSplosionEvent(this.x, this.y, buildup, explosionDamage, false, 0, null);
                        this.getLevel().entityManager.addLevelEvent(event);
                    }
                }
                if (duration >= 40.0f) {
                    duration = 1f;
                    explosionDamage = new GameDamage(275.0F,20.0F);
                    mob.buffManager.removeBuff("SlimeSplosionDebuff",true);
                    if(!mob.isBoss()) {
                        mob.setArmor(Math.round(mob.getArmorFlat()/2));
                    } else {
                        if(mob.getArmorFlat() >= 2) {
                            mob.setArmor((mob.getArmorFlat() - 2));
                        }
                    }
                    SlimeSplosionEvent event = new SlimeSplosionEvent(this.x, this.y, 125, explosionDamage, false, 0, null);
                    this.getLevel().entityManager.addLevelEvent(event);
                }
                ActiveBuff ab = new ActiveBuff("FlamerSuperSlow", mob, 0.2f, this.getOwner());
                ActiveBuff ab2 = new ActiveBuff("SlimeSplosionDebuff", mob, duration+1f, this.getOwner());
                ActiveBuff ab3 = new ActiveBuff("MoltenSlimeDebuff", mob, 5.0F, this.getOwner());
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
