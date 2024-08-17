package rangedarsenal.projectiles.bullets;

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
import java.util.List;

public class AmethystBulletProjectile extends BulletProjectile {
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
                float thresholdMod = (Float)attackerBM.getModifier(BuffModifiers.CRIT_CHANCE) + (Float)attackerBM.getModifier(BuffModifiers.MELEE_CRIT_CHANCE);
                float crystallizeMod = (Float)attackerBM.getModifier(BuffModifiers.CRIT_DAMAGE) + (Float)attackerBM.getModifier(BuffModifiers.MELEE_CRIT_CHANCE);
                int stackThreshold = (int)GameMath.limit(10.0F - 7.0F * thresholdMod, 3.0F, 10.0F);
                float crystallizeDamageMultiplier = GameMath.limit(crystallizeMod, 2.0F, (float)stackThreshold);
                Buff crystallizeBuff = Debuffs.CRYSTALLIZE_BUFF;
                ActiveBuff ab = new ActiveBuff(crystallizeBuff, mob, 10000, this.getAttackOwner());
                mob.buffManager.addBuff(ab, true);
                if (mob.buffManager.getBuff(crystallizeBuff).getStacks() >= stackThreshold) {
                    this.getLevel().entityManager.addLevelEvent(new CrystallizeShatterEvent(mob, ParticleType.SAPPHIRE));
                    mob.buffManager.removeBuff(crystallizeBuff, true);
                    GameDamage finalDamage = this.getDamage().modDamage(crystallizeDamageMultiplier);
                    mob.isServerHit(finalDamage, 0.0F, 0.0F, 0.0F, this);
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
                        int fadeTime = 10;
                        if (lifeCycleTime >= this.lifeTime - (long) fadeTime) {
                            alpha = Math.abs((float) (lifeCycleTime - (this.lifeTime - (long) fadeTime)) / (float) fadeTime - 1.0F);
                        }

                        //int cut = target == null;
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
