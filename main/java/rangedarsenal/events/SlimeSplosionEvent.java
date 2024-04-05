package rangedarsenal.events;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.Particle.GType;
import necesse.gfx.GameResources;


public class SlimeSplosionEvent extends ExplosionEvent implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher;

    public SlimeSplosionEvent() {
        this(0.0F, 0.0F, 125, new GameDamage(223.0F,20.0F), false, 0, (Mob) null);
    }

    public SlimeSplosionEvent(float x, float y, int range, GameDamage damage, boolean destructive, int toolTier, Mob owner) {
        super(x, y, range, damage, destructive, toolTier, owner);
        this.explosionTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{GType.IMPORTANT_COSMETIC, GType.COSMETIC, GType.CRITICAL});
    }

    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(10.0F);
    }

    protected void playExplosionEffects() {
        Screen.playSound(GameResources.splash, SoundEffect.effect(this.x, this.y).volume(0.9F).pitch(0.5F));
        Screen.playSound(GameResources.fireworkCrack, SoundEffect.effect(this.x, this.y).volume(0.7F).pitch(2.5F));
        Screen.playSound(GameResources.explosionHeavy, SoundEffect.effect(this.x, this.y).volume(0.6F).pitch(3.6F));
        Screen.playSound(GameResources.pop, SoundEffect.effect(this.x, this.y).volume(1.0F).pitch(2.9F));
        this.level.getClient().startCameraShake(this.x, this.y, 220, 14, 4F, 4F,true);
    }

    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange) / 1.3f;
    }

    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        float dx;
        float dy;
        if (GameRandom.globalRandom.getChance(0.7F) && range < 25.0F) {
            dx = dirX * (float) GameRandom.globalRandom.getIntBetween(20, 70);
            dy = dirY * (float) GameRandom.globalRandom.getIntBetween(10, 60) * 0.8F;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.liquidBlobParticle.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(70, 80).movesFriction(dx * 0.05F, dy * 0.05F, 0.8F).color(new Color(0, 128, 0)).heightMoves(0.0F, 70.0F).lifeTime(lifeTime * 2);
        }

        if (range <= (float) Math.max(this.range - 125, 25)) {
            dx = dirX * (float) GameRandom.globalRandom.getIntBetween(70, 80);
            dy = dirY * (float) GameRandom.globalRandom.getIntBetween(60, 70) * 0.8F;
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.liquidBlobParticle.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(30, 50).movesFriction(dx * 0.1F, dy * 0.1F, 2.1F).color(new Color(0, 166, 0)).heightMoves(0.0F, 10.0F).lifeTime(lifeTime * 3);
        }
    }
}
