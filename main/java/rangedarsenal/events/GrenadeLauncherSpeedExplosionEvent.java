package rangedarsenal.events;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class GrenadeLauncherSpeedExplosionEvent extends ExplosionEvent implements Attacker {
    public GrenadeLauncherSpeedExplosionEvent() {
        this(0F, 0F, new GameDamage(57.0F,15f), null);
        this.destroysTiles = false;
        this.targetRangeMod = 0.66F;
        this.range = 68;
        this.owner = -1;
        this.knockback = 75;
        this.hitsOwner = true;
        this.sendOwnerData = true;
        this.sendCustomData = true;
    }

    public GrenadeLauncherSpeedExplosionEvent(float x, float y, GameDamage damage, Mob owner) {
        super(x, y, 75, damage, false, 0, owner);
        this.sendCustomData = true;
        this.sendOwnerData = true;
    }

    public static void spawnExplosionParticles(Level level, float x, float y, int particles, float minRange, float maxRange, ExplosionSpawnFunction spawnFunction) {
        for(int i = 0; i <= particles; ++i) {
            float anglePerParticle = 360.0F / (float)particles;
            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
            float range = (GameRandom.globalRandom.getFloatBetween(minRange, maxRange))/5;
            float dx = (float)Math.sin(Math.toRadians((double)angle));
            float dy = (float)Math.cos(Math.toRadians((double)angle));
            spawnFunction.spawn(level, x + dx * range, y + dy * range, dx * 20.0F, dy * 20.0F, 400, range);
        }

    }

    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(1.0F).pitch(1.6F));
        this.level.getClient().startCameraShake(this.x, this.y, 200, 50, 1.5F, 1.5F,true);
    }
}
