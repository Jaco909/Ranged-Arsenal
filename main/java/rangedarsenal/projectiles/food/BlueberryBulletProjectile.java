package rangedarsenal.projectiles.food;

import necesse.engine.tickManager.TickManager;
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

public class BlueberryBulletProjectile extends Projectile {
    private long spawnTime;
    public BlueberryBulletProjectile(){
    }
    public BlueberryBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
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
        return new Color(15, 81, 203);
    }
    protected Color getWallHitColor() {
        return new Color(13, 39, 136);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(107, 123, 182), 15.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
}
