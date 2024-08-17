package rangedarsenal.events;

import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle.GType;
import necesse.gfx.GameResources;
import java.awt.*;

public class FruitBoomEvent extends ExplosionEvent implements Attacker {
    protected ParticleTypeSwitcher explosionTypeSwitcher;

    public String foodtype;

    public FruitBoomEvent() {
        this(0.0F, 0.0F, 45, new GameDamage(8, 0, 0), false, 0, (Mob) null, null);
        this.sendCustomData = false;
        this.sendOwnerData = true;
        //this.hitsOwner = false;
        this.knockback = 10;
    }

    public FruitBoomEvent(float x, float y, int range, GameDamage damage, boolean destructive, int toolTier, Mob owner, String food) {
        super(x, y, range, damage, destructive, toolTier, owner);
        this.explosionTypeSwitcher = new ParticleTypeSwitcher(new GType[]{GType.IMPORTANT_COSMETIC, GType.COSMETIC, GType.CRITICAL});
        this.sendCustomData = false;
        this.sendOwnerData = true;
        //this.hitsOwner = false;
        this.knockback = 10;
        this.foodtype = food;
    }

    protected GameDamage getTotalObjectDamage(float targetDistance) {
        return super.getTotalObjectDamage(targetDistance).modDamage(1.0F);
    }

    protected void playExplosionEffects() {
        if (foodtype != null) {
            if (foodtype.equals("corn")) {
                SoundManager.playSound(GameResources.fireworkCrack, SoundEffect.effect(this.x, this.y).volume(2F).pitch(1F));
            } else if (foodtype.equals("tomato")) {
                SoundManager.playSound(GameResources.npcdeath, SoundEffect.effect(this.x, this.y).volume(0.8F).pitch(1.8F));
            }
        }
    }

    public float getParticleCount(float currentRange, float lastRange) {
        return super.getParticleCount(currentRange, lastRange) / 15f;
    }

    public void spawnExplosionParticle(float x, float y, float dirX, float dirY, int lifeTime, float range) {
        float dx;
        float dy;
        dx = dirX * (float) 5;
        dy = dirY * (float) 5 * 0.8F;
        lifeTime = 165;
        if (foodtype != null) {
            if (foodtype.equals("corn")) {
                this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.liquidBlobParticle.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(30, 40).movesFriction(dx * 0.05F, dy * 0.05F, 0.1F).color(new Color(243, 236, 109)).heightMoves(0.0F, 1.0F).lifeTime(lifeTime);
            } else if (foodtype.equals("tomato")) {
                this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.liquidBlobParticle.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(30, 40).movesFriction(dx * 0.05F, dy * 0.05F, 0.1F).color(new Color(117, 2, 2)).heightMoves(0.0F, 1.0F).lifeTime(lifeTime);
            } else if (foodtype.equals("onion")) {
                this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.liquidBlobParticle.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(30, 40).movesFriction(dx * 0.05F, dy * 0.05F, 0.1F).color(new Color(117, 2, 2, 0)).heightMoves(0.0F, 1.0F).lifeTime(lifeTime);
            }
        } else {
            this.getLevel().entityManager.addParticle(x, y, this.explosionTypeSwitcher.next()).sprite(GameResources.liquidBlobParticle.sprite(GameRandom.globalRandom.getIntBetween(0, 4), 0, 12)).sizeFades(30, 40).movesFriction(dx * 0.05F, dy * 0.05F, 0.1F).color(new Color(238, 191, 10, 0)).heightMoves(0.0F, 1.0F).lifeTime(lifeTime);
        }
    }
}