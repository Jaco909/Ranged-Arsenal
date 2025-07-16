package rangedarsenal.projectiles.bullets;

import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.BuffRegistry;
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
import rangedarsenal.buffs.AmethystDebuff;

import java.awt.*;
import java.util.List;

public class AmethystBulletProjectile extends BulletProjectile {
    PlayerMob player;
    float crystallizeMod;
    int stackThreshold;
    float thresholdMod;
    public AmethystBulletProjectile() {
    }

    public AmethystBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
    }

    public void init() {
        super.init();
        this.particleSpeedMod = 0.03F;
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer() && mob != null) {
            BuffManager attackerBM = this.getAttackOwner().buffManager;
            if (attackerBM != null) {
                if (this.getOwner().isPlayer) {
                    player = (PlayerMob)this.getOwner();
                    thresholdMod = (Float)attackerBM.getModifier(BuffModifiers.CRIT_CHANCE) + (Float)attackerBM.getModifier(BuffModifiers.MELEE_CRIT_CHANCE);
                    crystallizeMod = (Float)attackerBM.getModifier(BuffModifiers.CRIT_DAMAGE) + (Float)attackerBM.getModifier(BuffModifiers.MELEE_CRIT_DAMAGE);
                    if (player.getSelectedItem().item.getStringID().equalsIgnoreCase("deathripper")) {
                        stackThreshold = (int)GameMath.limit((10.0F - (7.0F * thresholdMod)), 5.0F, 6.0F);
                    } else {
                        stackThreshold = (int)GameMath.limit((10.0F - (7.0F * thresholdMod)), 5.0F, Math.ceil(10.0F/(Math.ceil((float)player.getSelectedItem().item.getAttackAnimTime(player.getSelectedItem(), player) /160))));
                    }
                    //System.out.println(stackThreshold);
                } else {
                    thresholdMod = (Float)attackerBM.getModifier(BuffModifiers.CRIT_CHANCE) + (Float)attackerBM.getModifier(BuffModifiers.MELEE_CRIT_CHANCE);
                    crystallizeMod = (Float)attackerBM.getModifier(BuffModifiers.CRIT_DAMAGE) + (Float)attackerBM.getModifier(BuffModifiers.MELEE_CRIT_DAMAGE);
                    stackThreshold = (int)GameMath.limit((10.0F - (7.0F * thresholdMod)), 5.0F, 10.0F);
                }
                float crystallizeDamageMultiplier = GameMath.limit(0.5F + ((crystallizeMod-2)/2) + (thresholdMod/2), 0.5F, 2.0F);

                Buff crystallizeBuff = BuffRegistry.getBuff("AmethystDebuff");
                ActiveBuff ab = new ActiveBuff(crystallizeBuff, mob, 10000, this.getAttackOwner());
                mob.buffManager.addBuff(ab, true);
                if (mob.buffManager.getBuff(crystallizeBuff).getStacks() >= stackThreshold) {
                    this.getLevel().entityManager.addLevelEvent(new CrystallizeShatterEvent(mob, ParticleType.AMETHYST));
                    mob.buffManager.removeBuff(crystallizeBuff, true);
                    //GameDamage finalDamage = this.getDamage().modDamage(crystallizeDamageMultiplier);
                    //mob.isServerHit(finalDamage, 0.0F, 0.0F, 0.0F, this);

                    int shotCount = Math.round(GameMath.limit(this.getDamage().damage/15,1f,15f));
                    //System.out.println(shotCount);

                    for(int i = 0; i <= shotCount; ++i) {
                        GameRandom random = GameRandom.globalRandom;
                        int shootangle = GameRandom.globalRandom.getIntBetween(0, 7);
                        if (shootangle == 0) {
                            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(-5, 5);
                            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(5, 15);
                        } else if (shootangle == 1) {
                            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(5, 15);
                            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(5, 15);
                        } else if (shootangle == 2) {
                            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(5, 15);
                            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(-5, 5);
                        } else if (shootangle == 3) {
                            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(5, 15);
                            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(-5, -15);
                        } else if (shootangle == 4) {
                            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(-5, 5);
                            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(-5, -15);
                        } else if (shootangle == 5) {
                            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(-15, -5);
                            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(-15, -5);
                        } else if (shootangle == 6) {
                            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(-15, -5);
                            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(-5, 5);
                        } else if (shootangle == 7) {
                            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(-15, -5);
                            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(5, 15);
                        }
                        Projectile projectile = new SapphireSplosionBulletProjectile(mob.x, mob.y, targetX, targetY, 470, 97, this.getDamage().modDamage(2), 0, mob, this.getOwner());
                        projectile.getUniqueID(random);
                        this.getLevel().entityManager.projectiles.add(projectile);
                        //projectile.setAngle(projectile.getAngle() + (random.nextFloat() - 0.5F) * 2.0F);
                        if (this.getLevel().isServer()) {
                            //this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), (PlayerMob)this.getOwner().getRegionPositions(), ((PlayerMob)this.getOwner()).getServerClient());
                            if (this.getOwner().isPlayer) {
                                this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, ((PlayerMob) this.getOwner()).getServerClient());
                            } else {
                                this.getLevel().getServer().network.sendToAllClients(new PacketSpawnProjectile(projectile));
                            }
                        }
                    }
                }
            }
        }

        if (this.isClient() && this.bounced == this.getTotalBouncing()) {
            if (this.amountHit() >= this.piercing || object != null) {
                this.getLevel().entityManager.addParticle(new ProjectileHitStuckParticle(mob, this, x, y, (float) GameRandom.globalRandom.getIntBetween(10, 20), 5000L) {
                    public void addDrawables(Mob target, float x, float y, float angle, List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                        GameLight light = level.getLightLevel(this);
                        int drawX = camera.getDrawX(x) - 2;
                        int drawY = camera.getDrawY(y - AmethystBulletProjectile.this.height) - 2;
                        float alpha = 1.0F;
                        long lifeCycleTime = this.getLifeCycleTime();
                        int fadeTime = 1000;
                        if (lifeCycleTime >= this.lifeTime - (long) fadeTime) {
                            alpha = Math.abs((float) (lifeCycleTime - (this.lifeTime - (long) fadeTime)) / (float) fadeTime - 1.0F);
                        }

                        final TextureDrawOptions options = AmethystBulletProjectile.this.texture.initDraw().light(light).rotate(AmethystBulletProjectile.this.getAngle(), 2, 2).alpha(alpha).pos(drawX, drawY);
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
        Trail trail = new Trail(this, this.getLevel(), new Color(172, 118, 192), 22.0F, 100, this.height);
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    protected Color getWallHitColor() {
        return new Color(159, 13, 199);
    }

    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, this.getWallHitColor(), this.lightSaturation);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel(this);
            int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
            int drawY = camera.getDrawY(this.y);
            final TextureDrawOptions options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
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
