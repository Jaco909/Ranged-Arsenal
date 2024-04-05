package rangedarsenal.projectiles.bullets;

import necesse.engine.Screen;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;
import rangedarsenal.events.ShrapnelEvent;

import java.awt.*;

public class SplinteringBulletProjectile extends BulletProjectile {
    public SplinteringBulletProjectile() {
    }
    public SplinteringBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, (new GameDamage(damage.damage/1.27f,damage.armorPen,damage.baseCritChance)), knockback, owner);
    }
    public void init() {
        super.init();
    }
    PlayerMob player = ((PlayerMob)this.getOwner());
    int count;

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
                boolean canHit = this.checkHitCooldown(mob, !this.isServer() || packetSubmitter == null && this.handlingClient == null && (!this.clientHandlesHit || !mob.isPlayer) ? 0 : 100);
                if (canHit && (this.amountHit() <= this.piercing || this.returningToOwner)) {
                    boolean addHit = true;
                    Screen.playSound(GameResources.fireworkCrack, SoundEffect.effect(player).volume(0.9f).pitch(GameRandom.globalRandom.getFloatBetween(2f, 4f)));
                    Screen.playSound(GameResources.fireworkCrack, SoundEffect.effect(mob).volume(4f).pitch(GameRandom.globalRandom.getFloatBetween(2f, 4f)));


                    for(int i = 0; i <= 7; ++i) {
                        LevelEvent event = new ShrapnelEvent(this.getOwner(), new GameDamage(0F,0F), 0, mob.getX(), mob.getY(), GameRandom.globalRandom.getIntBetween(-50, 50), mob, i);
                        this.getOwner().getLevel().entityManager.addLevelEventHidden(event);
                        if (this.getLevel().isServer()) {
                            this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketLevelEvent(event), event, player.getServerClient());
                        }
                    }
                    if (mob.getLevel().entityManager.mobs.streamArea(mob.getX(),mob.getY(),1) != null) {
                        float counter = mob.getLevel().entityManager.mobs.streamArea(mob.getX(),mob.getY(),1).count();

                        mob.getLevel().entityManager.mobs.streamArea(mob.getX(),mob.getY(), 1).forEach((m) -> {
                            if (m != mob) {
                                count++;
                                if (((m.x <= (mob.x+47)) && (m.x >= (mob.x-47))) && ((m.y <= (mob.y+47)) && (m.y >= (mob.y-47)))) {
                                    int damage = Math.round(this.getDamage().damage/4f);
                                    if (damage > 100) {
                                        damage = 100;
                                    } else if (damage <= 0) {
                                        damage = 1;
                                    }
                                    m.setHealth(m.getHealth() - damage, player);
                                    m.spawnDamageText(damage, 12, false);
                                }
                            }
                        });
                    }
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

    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(234, 164, 52), 19.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    protected Color getWallHitColor() {
        return new Color(159, 96, 6);
    }

    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0F, this.lightSaturation);
    }

}
