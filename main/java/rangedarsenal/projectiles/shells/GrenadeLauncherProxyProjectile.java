package rangedarsenal.projectiles.shells;

import necesse.engine.sound.SoundEffect;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundManager;
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
import rangedarsenal.events.GrenadeLauncherProxyExplosionEvent;
import static rangedarsenal.rangedarsenal.GlProxyArmedTex;
import static rangedarsenal.rangedarsenal.GlProxyArmingTex;
import static rangedarsenal.rangedarsenal.proxyarm;
import java.awt.*;
import java.util.List;

public class GrenadeLauncherProxyProjectile extends Projectile {
    private long spawnTime;
    public float staticAngle;
    int tickcount = 0;
    int tickcount2 = 0;
    boolean active;
    boolean landed;
    public GrenadeLauncherProxyProjectile() {
    }

    public GrenadeLauncherProxyProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.applyData(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
        //this.spawnTime = this.getWorldTime();
        staticAngle = this.getAngleToTarget(owner.x,owner.y,targetX,targetY);
    }

    public void init() {
        super.init();
        this.setWidth(15.0F);
        this.height = 18.0F;
        this.heightBasedOnDistance = true;
        //this.spawnTime = this.getWorldEntity().getTime();
        this.doesImpactDamage = true;
        this.trailOffset = 4.0F;
    }

    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(124, 124, 124), 20.0F, 150, 1.0F);
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


    /*public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        *//*if (this.isServer()) {
            GrenadeLauncherExplosionEvent event = new GrenadeLauncherExplosionEvent(x, y, new GameDamage(0F,0F), this.getOwner());
            this.getLevel().entityManager.addLevelEvent(event);
            this.remove();
        }*//*
    }*/
    public void serverTick() {
        if (this.sendPositionUpdate && this.handlingClient == null) {
            this.sendServerUpdatePacket();
            this.sendPositionUpdate = false;
        }
        if (this.traveledDistance >= this.distance) {
            tickcount++;
        }

        if (tickcount >= 30) {
            if (tickcount > 339) {
                GrenadeLauncherProxyExplosionEvent event = new GrenadeLauncherProxyExplosionEvent(x, y, (new GameDamage(200+this.getDamage().damage*6,this.getDamage().armorPen+200,this.getDamage().baseCritChance+0)), this.getOwner());
                this.getLevel().entityManager.addLevelEvent(event);
                this.remove();
            }
            //this.clearHits();
            this.getLevel().entityManager.streamAreaMobsAndPlayersTileRange(this.getX(),this.getY(), 2).forEach((m) -> {
                if (m.isHostile || m.isPlayer || m.isCritter || (m.canLevelInteract() && !m.isHuman)) {
                    if (((m.x <= (this.x + 33)) && (m.x >= (this.x - 33))) && ((m.y <= (this.y + 33)) && (m.y >= (this.y - 33)))) {
                        //System.out.println(this.getTime());
                        GrenadeLauncherProxyExplosionEvent event = new GrenadeLauncherProxyExplosionEvent(x, y, (new GameDamage(200 + this.getDamage().damage * 6, this.getDamage().armorPen + 200, this.getDamage().baseCritChance + 0)), this.getOwner());
                        this.getLevel().entityManager.addLevelEvent(event);
                        this.remove();
                    }
                }
            });
        }
        /*if (this.getWorldTime() >= spawnTime+10000) {
            *//*if ((this.getWorldTime() >= spawnTime+10000) && !active) {
                active = true;
                Screen.playSound(GameSound.fromFile("activate"), SoundEffect.effect(this.getOwner()).volume(2f));
            }*//*
            if (this.getWorldTime() >= spawnTime+100000) {
                this.remove();
            }
            if (this.getWorldTime() >= spawnTime+10000) {
                this.getLevel().entityManager.streamAreaMobsAndPlayersTileRange(this.getX(),this.getY(), 2).forEach((m) -> {
                    if (((m.x <= (this.x+33)) && (m.x >= (this.x-33))) && ((m.y <= (this.y+33)) && (m.y >= (this.y-33)))) {
                        //System.out.println(this.getTime());
                        GrenadeLauncherExplosionEvent event = new GrenadeLauncherExplosionEvent(x, y, this.getDamage(), this.getOwner());
                        this.getLevel().entityManager.addLevelEvent(event);
                        this.remove();
                    }
                });
            }
        }*/
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
        if (this.traveledDistance >= this.distance) {
            if (!landed) {
                landed = true;
                this.texture = GlProxyArmingTex;
            }
            tickcount2++;
        }

        if (tickcount2 >= 30) {
            /*if (tickcount2 > 450) {
                this.remove();
            }*/
            if (!active) {
                active = true;
                this.texture = GlProxyArmedTex;
                SoundManager.playSound(proxyarm, SoundEffect.effect(this).volume(1.5f));
            }
            /*this.getLevel().entityManager.streamAreaMobsAndPlayersTileRange(this.getX(),this.getY(), 2).forEach((m) -> {
                if (((m.x <= (this.x+33)) && (m.x >= (this.x-33))) && ((m.y <= (this.y+33)) && (m.y >= (this.y-33)))) {
                    //System.out.println(this.getTime());
                    GrenadeLauncherExplosionEvent event = new GrenadeLauncherExplosionEvent(x, y, this.getDamage(), this.getOwner());
                    this.getLevel().entityManager.addLevelEvent(event);
                    this.remove();
                }
            });*/
        }
        /*if (this.getWorldTime() >= spawnTime+10000) {
            if ((this.getWorldTime() >= spawnTime+10000) && !active) {
                active = true;
                Screen.playSound(GameSound.fromFile("activate_single"), SoundEffect.effect(this.getOwner()).volume(1.36f));
            }
            if (this.getWorldTime() >= spawnTime+100000) {
                this.remove();
            }
            *//*if (this.getWorldTime() >= spawnTime+10000) {
                this.getLevel().entityManager.streamAreaMobsAndPlayersTileRange(this.getX(),this.getY(), 2).forEach((m) -> {
                    if (((m.x <= (this.x+33)) && (m.x >= (this.x-33))) && ((m.y <= (this.y+33)) && (m.y >= (this.y-33)))) {
                        //System.out.println(this.getTime());
                        GrenadeLauncherExplosionEvent event = new GrenadeLauncherExplosionEvent(x, y, this.getDamage(), this.getOwner());
                        this.getLevel().entityManager.addLevelEvent(event);
                        this.remove();
                    }
                });
            }*//*
        }*/
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
            //this.doHitLogic((Mob)null, (LevelObjectHit)null, this.x, this.y);
            if (this.isServer() && this.dropItem) {
                this.dropItem();
            }
        }
    }
}
