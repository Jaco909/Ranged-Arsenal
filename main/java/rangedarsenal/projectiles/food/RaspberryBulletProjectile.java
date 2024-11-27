package rangedarsenal.projectiles.food;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class RaspberryBulletProjectile extends Projectile {
    private long spawnTime;
    public RaspberryBulletProjectile(){
    }
    public RaspberryBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.spawnTime = this.getWorldEntity().getTime();
        this.applyData(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 3;
            int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 3;
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getWorldEntity().getTime() - this.spawnTime, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getWorldEntity().getTime() - this.spawnTime, this.texture.getHeight() / 2);
        }
    }

    public Color getParticleColor() {
        return new Color(201, 31, 103);
    }
    protected Color getWallHitColor() {
        return new Color(121, 11, 63);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(192, 19, 102), 15.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
}
