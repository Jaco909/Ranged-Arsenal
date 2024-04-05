package rangedarsenal.projectiles.fuel;

import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTexture.GameTexture;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;
import necesse.engine.util.GameRandom;
import java.awt.Color;
import java.awt.geom.Line2D;

public class NapalmBulletProjectile extends BulletProjectile {
    //I just realized the Venom Staff does the same things this does
    //but you know, properly instead of the garbage method I did
    //nevermind I just pulled it up and it does it basically the same way lmao
    public static GameTexture texture;
    int tickcount = 0;
    int tickcount2 = 0;
    int hitattempt = 0;
    int hitattempt2 = 0;
    public NapalmBulletProjectile() {
    }
    public NapalmBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    public void init() {
        super.init();
        this.particleSpeedMod = 0.5F;
        this.piercing = 6;
    }
    public Color getParticleColor() {
        return new Color(250, 70, 0);
    }
    protected Color getWallHitColor() {
        return new Color(200, 70, 0);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(250, 70, 0), 25.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 10.0F, this.lightSaturation);
    }
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                ActiveBuff ab = new ActiveBuff("FlamerSuperSlow", mob, 0.15F, this.getOwner());
                ActiveBuff ab2 = new ActiveBuff("NapalmDebuff", mob, 5.0F, this.getOwner());
                mob.addBuff(ab, true);
                mob.addBuff(ab2, true);
            }
        }
    }
    public void serverTick() {
        if (this.sendPositionUpdate && this.handlingClient == null) {
            this.sendServerUpdatePacket();
            this.sendPositionUpdate = false;
        }
        tickcount++;
        if (tickcount >= 5) {
            tickcount = 0;
            hitattempt++;
            if (hitattempt > 26) {
                this.remove();
            }
            this.clearHits();
            this.checkHitCollision(new Line2D.Float(this.x, this.y, this.x + 3, this.y + 3));
        }
    }
    public void clientTick() {
        if (this.sendPositionUpdate) {
            if (this.isClient() && this.handlingClient == this.getLevel().getClient().getClient()) {
                this.sendClientUpdatePacket();
            }

            this.sendPositionUpdate = false;
        }
        if (this.givesLight && this.isClient()) {
            this.refreshParticleLight();
        }
        float particleChance = this.getParticleChance();
        if (particleChance > 0.0F && (particleChance >= 1.0F || GameRandom.globalRandom.nextFloat() <= particleChance)) {
            if (this.traveledDistance < (float)this.distance) {
                this.spawnSpinningParticle();
            } else {
                //GameResources.loadTextures();
                //this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/NapalmPool")).sprite(GameRandom.globalRandom.getIntBetween(0, 5), 2, 32)).color(new Color(187, 80, 0)).sizeFadesInAndOut(10, 16, 50, 200).movesConstant(this.dx / 10.0F, this.dy / 10.0F).lifeTime(300).height(2.0F);
                for(int i = 0; i < 2; ++i) {
                    this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getIntBetween(-10, 10), this.y + GameRandom.globalRandom.getIntBetween(-10, 10), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.getIntBetween(1, 2), 0, 12)).color(new Color(187, GameRandom.globalRandom.getIntBetween(60, 100), 0)).sizeFadesInAndOut(24, 34, 50, 15).movesConstant(this.dx / 2.0F, this.dy / 2.0F).lifeTime(500).height(2.0F);
                }
            }
        }
        tickcount2++;
        if (tickcount2 >= 5) {
            tickcount2 = 0;
            hitattempt2++;
            if (hitattempt2 > 26) {
                this.remove();
            }
            this.clearHits();
            this.checkHitCollision(new Line2D.Float(this.x, this.y, this.x + 3, this.y + 3));
        }
    }
    protected void spawnDeathParticles() {
        //null
    }
    public float tickMovement(float delta) {
        if (this.removed()) {
            return 0.0F;
        } else {
            float moveX = this.getMoveDist(this.dx * this.speed, delta);
            float moveY = this.getMoveDist(this.dy * this.speed, delta);
            double totalDist = Math.sqrt((double)(moveX * moveX + moveY * moveY));
            if (Double.isNaN(totalDist) || Double.isInfinite(totalDist)) {
                totalDist = 0.0;
            }
            this.moveDist(totalDist);
            return (float)totalDist;
        }
    }
    public void checkRemoved() {
        if (this.traveledDistance >= (float)this.distance) {
            this.doHitLogic((Mob)null, (LevelObjectHit)null, this.x, this.y);
            if (this.isServer() && this.dropItem) {
                this.dropItem();
            }
        }
    }
    //prevent wall hit sounds
    public void playHitSound(float x, float y) {
    }
}
