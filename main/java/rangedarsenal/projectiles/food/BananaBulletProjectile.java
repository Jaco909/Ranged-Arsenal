package rangedarsenal.projectiles.food;

import necesse.engine.Screen;
import necesse.engine.sound.SoundEffect;
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

public class BananaBulletProjectile extends Projectile {
    protected int soundTimer;
    protected long spawnTime;

    public BananaBulletProjectile(){
        this.isBoomerang = true;
    }
    public void init() {
        super.init();
        if (this.getOwner() == null) {
            this.remove();
        }

        this.returningToOwner = false;
        this.spawnTime = this.getWorldEntity().getTime();
        this.trailOffset = 0.0F;
    }
    public void clientTick() {
        super.clientTick();
        --this.soundTimer;
        if (this.soundTimer <= 0) {
            this.soundTimer = 5;
            this.playMoveSound();
        }
    }
    public void playMoveSound() {
        Screen.playSound(GameResources.swing2, SoundEffect.effect(this));
    }
    public BananaBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.applyData(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(perspective), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(perspective), this.texture.getHeight() / 2);
        }
    }

    public float getAngle(PlayerMob player) {
        return (float)(this.getLifeTime()*800);
        //return this.angle % 360.0F;
    }

    public Color getParticleColor() {
        return new Color(204, 179, 13);
    }
    protected Color getWallHitColor() {
        return new Color(117, 108, 8);
    }
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(196, 193, 126), 25.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }
}
