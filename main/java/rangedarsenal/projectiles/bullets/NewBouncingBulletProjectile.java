package rangedarsenal.projectiles.bullets;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle.GType;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.projectile.bulletProjectile.BouncingBulletProjectile;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class NewBouncingBulletProjectile extends BulletProjectile {
    public NewBouncingBulletProjectile() {
    }
    int bounced = 0;
    Trail trail;
    int R = 56;
    int G = 53;
    int B = 172;
    int R2 = 56;
    int G2 = 53;
    int B2 = 172;

    public NewBouncingBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }

    public void init() {
        super.init();
        this.bouncing = 20;
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob == null) {
            int offset = (new GameRandom((long)(this.getUniqueID() + this.bounced * 1337))).getIntBetween(-5, 5);
            this.setAngle(this.getAngle() + (float)offset);
            this.setDamage(new GameDamage(this.getDamage().damage*1.06f,this.getDamage().armorPen*1f,this.getDamage().baseCritChance+0.01f));
            this.bounced++;
            this.replaceTrail();
        }
    }

    public Trail getTrail() {
        System.out.println(this.bounced);
        R2 = inRange(R2+this.bounced);
        G2 = inRange(G2+this.bounced*6);
        B2 = inRange(B2+this.bounced*3);
        trail = new Trail(this, this.getLevel(), new Color(R2, G2, B2), 22.0F, 100, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
    public int inRange(int value){
        if (value > 255) {
            value = 255;
        }
        return value;
    }

    protected Color getWallHitColor() {
        R = inRange(R+this.bounced);
        G = inRange(G+this.bounced*6);
        B = inRange(B+this.bounced*3);
        return new Color(R, G, B);
    }
    public void clientTick() {
        super.clientTick();
        this.replaceTrail();
    }

    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 220.0F, this.lightSaturation);
    }
}
