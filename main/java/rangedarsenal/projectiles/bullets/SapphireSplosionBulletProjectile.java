package rangedarsenal.projectiles.bullets;

import java.util.LinkedList;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry.Debuffs;
import necesse.engine.sound.SoundEffect;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystallizeShatterEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystallizeShatterEvent.ParticleType;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle.GType;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.projectile.Projectile;
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
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class SapphireSplosionBulletProjectile extends BulletProjectile {
    Mob master;
    public SapphireSplosionBulletProjectile() {
    }

    public SapphireSplosionBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob source, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(source);
        this.master = owner;
    }

    public void init() {
        super.init();
    }

    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        if (this.modifier == null || !this.modifier.onHit(mob, object, x, y, fromPacket, packetSubmitter)) {
            this.hit(mob, x, y, fromPacket, packetSubmitter);
            if (mob == null) {
                if (this.isClient()) {
                    this.spawnWallHitParticles(x, y);
                    this.playHitSound(x, y);
                }

                this.doHitLogic(mob, object, x, y);
                int bouncing = this.bouncing;
                Mob owner = this.getOwner();
                if (owner != null) {
                    bouncing += (Integer)owner.buffManager.getModifier(BuffModifiers.PROJECTILE_BOUNCES);
                }

                if (this.bounced >= bouncing || !this.canBounce) {
                    if (this.isBoomerang) {
                        this.returnToOwner();
                    } else {
                        if (this.dropItem && this.isServer()) {
                            this.dropItem();
                        }

                        this.remove();
                        this.sendRemovePacket = false;
                    }
                }
            } else {
                if (mob != this.master) {
                    boolean canHit = this.checkHitCooldown(mob, !this.isServer() || packetSubmitter == null && this.handlingClient == null && (!this.clientHandlesHit || !mob.isPlayer) ? 0 : 100);
                    if (canHit && (this.amountHit() <= this.piercing || this.returningToOwner)) {
                        boolean addHit = true;
                        if (this.isServer()) {
                            boolean isClientProjectile = this.handlingClient != null || this.clientHandlesHit && mob.isPlayer;
                            if (packetSubmitter == null && isClientProjectile) {
                                addHit = false;
                            } else {
                                if (this.doesImpactDamage) {
                                    this.applyDamage(mob, x, y);
                                }

                                this.doHitLogic(mob, object, x, y);
                                if (packetSubmitter != null) {
                                    this.getLevel().getServer().network.sendToClientsAtExcept(new PacketProjectileHit(this, x, y, mob), packetSubmitter, packetSubmitter);
                                } else {
                                    this.getLevel().getServer().network.sendToClientsAt(new PacketProjectileHit(this, x, y, mob), this.getLevel());
                                }
                            }
                        } else if (this.isClient()) {
                            if (fromPacket) {
                                this.doHitLogic(mob, object, x, y);
                            } else if (this.getLevel().getClient().allowClientsPower()) {
                                ClientClient client = this.getLevel().getClient().getClient();
                                if (this.clientHandlesHit && mob == client.playerMob || this.handlingClient == client) {
                                    this.getLevel().getClient().network.sendPacket(new PacketProjectileHit(this, x, y, mob));
                                    mob.startHitCooldown();
                                    this.doHitLogic(mob, object, x, y);
                                }
                            }
                        } else if (this.doesImpactDamage) {
                            this.applyDamage(mob, x, y);
                        }

                        if (addHit) {
                            this.addHit(mob);
                        }
                    }

                    if (this.amountHit() > this.piercing) {
                        if (this.isBoomerang) {
                            if (!this.returningToOwner) {
                                this.returnToOwner();
                            }
                        } else {
                            this.remove();
                        }
                    }
                }
            }
        }
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isClient() && this.bounced == this.getTotalBouncing()) {
            if (this.amountHit() >= this.piercing || object != null) {
                this.getLevel().entityManager.addParticle(new ProjectileHitStuckParticle(mob, this, x, y, (float) GameRandom.globalRandom.getIntBetween(10, 20), 5000L) {
                    public void addDrawables(Mob target, float x, float y, float angle, List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                        GameLight light = level.getLightLevel(this);
                        int drawX = camera.getDrawX(x) - 2;
                        int drawY = camera.getDrawY(y - SapphireSplosionBulletProjectile.this.height) - 2;
                        float alpha = 1.0F;
                        long lifeCycleTime = this.getLifeCycleTime();
                        int fadeTime = 200;
                        if (lifeCycleTime >= this.lifeTime - (long) fadeTime) {
                            alpha = Math.abs((float) (lifeCycleTime - (this.lifeTime - (long) fadeTime)) / (float) fadeTime - 1.0F);
                        }

                        final TextureDrawOptions options = SapphireSplosionBulletProjectile.this.texture.initDraw().light(light).rotate(SapphireSplosionBulletProjectile.this.getAngle(), 2, 2).alpha(alpha).pos(drawX, drawY);
                        EntityDrawable drawable = new EntityDrawable(this) {
                            public void draw(TickManager tickManager) {
                                options.draw();
                            }
                        };
                        if (target != null) {
                            topList.add(drawable);
                        } else {
                            list.add(drawable);
                        }
                    }
                }, GType.IMPORTANT_COSMETIC);
            }
        }
    }

    public void onMaxMoveTick() {
        if (this.isClient()) {
            this.spawnSpinningParticle();
        }

    }

    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(116, 245, 253), 22.0F, 100, this.height);
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    protected Color getWallHitColor() {
        return new Color(116, 245, 253);
    }

    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, this.getWallHitColor(), this.lightSaturation);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y);
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight()).color(255,0,0,100);
            list.add(new EntityDrawable(this) {
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
            this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
        }
    }

    public void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.crystalHit1, SoundEffect.effect(this).volume(2.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.9F, 1.1F)));
    }
}
