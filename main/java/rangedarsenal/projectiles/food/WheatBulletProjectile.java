package rangedarsenal.projectiles.food;

import necesse.engine.gameLoop.tickManager.TickManager;
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
import necesse.level.maps.light.GameLight;

import java.awt.*;
import java.util.List;

public class WheatBulletProjectile extends Projectile {

    public WheatBulletProjectile(){
    }
    public WheatBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.applyData(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 6;
            int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 6;
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.texture.getHeight() / 2);
        }
    }

    public Color getParticleColor() {
        return new Color(250, 212, 119, 48);
    }
    protected Color getWallHitColor() {
        return new Color(255, 205, 113, 53);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(255, 235, 103), 5.0F, 150, this.getHeight());
        //trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
}
