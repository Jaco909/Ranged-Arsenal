package rangedarsenal.events;

import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class GrenadeLauncherExplosionEvent extends ExplosionEvent implements Attacker {
    public GrenadeLauncherExplosionEvent() {
        this(0F, 0F, new GameDamage(150.0F,25f), null);
        this.destroysTiles = false;
        this.targetRangeMod = 0.66F;
        this.range = 175;
        this.owner = -1;
        this.knockback = 500;
        this.sendOwnerData = true;
        this.sendCustomData = true;
    }

    public GrenadeLauncherExplosionEvent(float x, float y, GameDamage damage, Mob owner) {
        super(x, y, 175, damage, false, 0, owner);
        this.sendCustomData = true;
        this.sendOwnerData = true;
        try {
            if (owner.isPlayer) {
                this.hitsOwner = true;
            } else {
                this.hitsOwner = false;
            }
        } catch (Exception e) {
            this.hitsOwner = false;
        }
        //this.hitsOwner = owner.isPlayer;
    }

    public static void spawnExplosionParticles(Level level, float x, float y, int particles, float minRange, float maxRange, ExplosionSpawnFunction spawnFunction) {
        for(int i = 0; i <= particles; ++i) {
            float anglePerParticle = 360.0F / (float)particles;
            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
            float range = (GameRandom.globalRandom.getFloatBetween(minRange, maxRange))/2;
            float dx = (float)Math.sin(Math.toRadians((double)angle));
            float dy = (float)Math.cos(Math.toRadians((double)angle));
            spawnFunction.spawn(level, x + dx * range, y + dy * range, dx * 20.0F, dy * 20.0F, 400, range);
        }

    }

    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(2.0F).pitch(1.3F));
        this.level.getClient().startCameraShake(this.x, this.y, 400, 50, 3.0F, 3.0F,true);
    }
}
