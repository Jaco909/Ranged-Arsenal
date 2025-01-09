package rangedarsenal.projectiles.shells;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;
import rangedarsenal.events.GrenadeLauncherExplosionEvent;
import rangedarsenal.events.GrenadeLauncherFireExplosionEvent;
import rangedarsenal.projectiles.bullets.SapphireSplosionBulletProjectile;
import rangedarsenal.projectiles.fuel.NapalmBulletProjectile;

import java.awt.*;
import java.util.List;

public class GrenadeLauncherFireProjectile extends Projectile {
    private long spawnTime;
    public float staticAngle;
    public GrenadeLauncherFireProjectile() {
    }

    public GrenadeLauncherFireProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.applyData(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
        staticAngle = this.getAngleToTarget(owner.x,owner.y,targetX,targetY);
    }

    public void init() {
        super.init();
        this.setWidth(15.0F);
        this.height = 18.0F;
        this.heightBasedOnDistance = true;
        this.spawnTime = this.getWorldEntity().getTime();
        this.doesImpactDamage = false;
        this.trailOffset = 4.0F;
        this.canBounce = false;
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(0, 0, 0), 0.0F, 10, 1.0F);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(staticAngle, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.texture.getHeight() / 2);
        }
    }


    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        GameRandom random = GameRandom.globalRandom;
        if (this.isServer()) {
            GrenadeLauncherFireExplosionEvent event = new GrenadeLauncherFireExplosionEvent(x, y, new GameDamage(0), this.getOwner());
            this.getLevel().entityManager.addLevelEvent(event);
        }

        //spawn fire
        for(int i = 0; i <= 6; ++i) {
            Projectile projectile = new NapalmBulletProjectile(x, y, (float) (x + random.getIntBetween(-30, 30)), (float) (y + random.getIntBetween(-30, 30)), random.getFloatBetween(300f,500f), random.getIntBetween(10, 64), new GameDamage(20f, 100f), 0, this.getOwner());
            projectile.getUniqueID(random);
            this.getLevel().entityManager.projectiles.add(projectile);
            if (this.getLevel().isServer()) {
                this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, ((PlayerMob) this.getOwner()).getServerClient());
            }
        }
    }
}
