package rangedarsenal.projectiles.bullets;

import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;
import rangedarsenal.events.LightningJumperEvent;

import java.awt.*;

public class LightningBulletProjectile extends BulletProjectile {
    float closest;
    int targetingX;
    int targetingY;
    int counter;
    int players;
    int hostiles;
    int critters;
    boolean done;
    int hostilescheck = 1;
    public LightningBulletProjectile() {
    }
    public LightningBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    public void init() {
        super.init();
    }
    PlayerMob player = (PlayerMob)this.getOwner();

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
                    //SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(mob.getAttackOwner()).volume(0.3f).pitch(GameRandom.globalRandom.getFloatBetween(1f, 2f)));
                    //SoundManager.playSound(GameResources.fireworkCrack, SoundEffect.effect(mob.getAttackOwner()).volume(2f).pitch(GameRandom.globalRandom.getFloatBetween(4f, 4f))); //4, 3f

                    SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(mob).volume(0.6f).pitch(GameRandom.globalRandom.getFloatBetween(1f, 2f)));
                    SoundManager.playSound(GameResources.fireworkCrack, SoundEffect.effect(mob).volume(4f).pitch(GameRandom.globalRandom.getFloatBetween(4f, 4f))); //4, 3f


                    if (mob.getLevel().entityManager.mobs.streamArea(mob.getX(),mob.getY(),1) != null) {
                        done = false;
                        counter = 0;
                        mob.getLevel().entityManager.mobs.streamArea(mob.getX(),mob.getY(), 1).forEach((m) -> {
                            if (((m.x <= (mob.x + 260)) && (m.x >= (mob.x - 260))) && ((m.y <= (mob.y + 260)) && (m.y >= (mob.y - 260)))) {
                                counter++;
                            }});
                        //System.out.println("count: "+ counter);
                        players = 0;
                        mob.getLevel().entityManager.mobs.streamArea(mob.getX(),mob.getY(), 1).forEach((m) -> {
                            if (m.isPlayer && !m.isSameTeam(player) && player.getServerClient().pvpEnabled && (((m.x <= (mob.x + 260)) && (m.x >= (mob.x - 260))) && ((m.y <= (mob.y + 260)) && (m.y >= (mob.y - 260))))) {
                                players++;
                            }});
                        //System.out.println("players: "+ players);
                        hostiles = 0;
                        mob.getLevel().entityManager.mobs.streamArea(mob.getX(),mob.getY(), 1).forEach((m) -> {
                            if (m.isHostile && (((m.x <= (mob.x + 260)) && (m.x >= (mob.x - 260))) && ((m.y <= (mob.y + 260)) && (m.y >= (mob.y - 260))))) {
                                hostiles++;
                            }});
                        //System.out.println("hostiles: "+ hostiles);
                        critters = 0;
                        mob.getLevel().entityManager.mobs.streamArea(mob.getX(),mob.getY(), 1).forEach((m) -> {
                            if (m.isCritter && (((m.x <= (mob.x + 260)) && (m.x >= (mob.x - 260))) && ((m.y <= (mob.y + 260)) && (m.y >= (mob.y - 260))))) {
                                critters++;
                            }});
                        //System.out.println("critters: "+ critters);
                        hostilescheck = 1;
                        mob.getLevel().entityManager.mobs.streamArea(mob.getX(),mob.getY(), 1).forEach((m) -> {
                            if (((m.x <= (mob.x + 260)) && (m.x >= (mob.x - 260))) && ((m.y <= (mob.y + 260)) && (m.y >= (mob.y - 260)))) {
                                //found something in range
                                //System.out.println("found in range: "+ m.getStringID().toLowerCase());
                                if ((m != mob) && (m != this.getOwner())) {
                                    //found something not the target or owner
                                    //System.out.println("wack2");
                                    //this.player = this.getOwner().getFollowingPlayer();
                                    if (m.isPlayer && !m.isSameTeam(player) && player.getServerClient().pvpEnabled) {
                                        //System.out.println("PvP");
                                        float distancemob = m.getDistance(mob);
                                        if (closest == 0) {
                                            closest = distancemob;
                                            targetingX = Math.round(m.x);
                                            targetingY = Math.round(m.y);
                                        } else if (distancemob < closest) {
                                            closest = distancemob;
                                            targetingX = Math.round(m.x);
                                            targetingY = Math.round(m.y);
                                        }
                                        done = true;
                                    } else if (m.isHostile && players == 0) {
                                        //System.out.println("NPC");
                                        float distancemob = m.getDistance(mob);
                                        if (closest == 0) {
                                            closest = distancemob;
                                            targetingX = Math.round(m.x);
                                            targetingY = Math.round(m.y);
                                        } else if (distancemob < closest) {
                                            closest = distancemob;
                                            targetingX = Math.round(m.x);
                                            targetingY = Math.round(m.y);
                                        }
                                        done = true;
                                    } else if (m.isCritter && hostiles == 0 && players == 0) {
                                        //System.out.println("Critter");
                                        float distancemob = m.getDistance(mob);
                                        if (closest == 0) {
                                            closest = distancemob;
                                            targetingX = Math.round(m.x);
                                            targetingY = Math.round(m.y);
                                        } else if (distancemob < closest) {
                                            closest = distancemob;
                                            targetingX = Math.round(m.x);
                                            targetingY = Math.round(m.y);
                                        }
                                        done = true;
                                    } else if (m.canBeHit(player) && hostiles == 0 && players == 0 && critters == 0) {
                                        //System.out.println("Misc");
                                        float distancemob = m.getDistance(mob);
                                        if (closest == 0) {
                                            closest = distancemob;
                                            targetingX = Math.round(m.x);
                                            targetingY = Math.round(m.y);
                                        } else if (distancemob < closest) {
                                            closest = distancemob;
                                            targetingX = Math.round(m.x);
                                            targetingY = Math.round(m.y);
                                        }
                                        done = true;
                                    }
                                }
                                if ((counter <= hostilescheck) && !done) {
                                    //end of list, ents are non-valid
                                    //System.out.println("forced rando");
                                    //System.out.println("random");
                                    targetingX = mob.getX() + GameRandom.globalRandom.getIntBetween(-30, 30);
                                    targetingY = mob.getY() + GameRandom.globalRandom.getIntBetween(-30, 30);
                                }
                                //System.out.println("ignored");
                                //System.out.println(hostilescheck++);
                            }
                        });
                    } else {
                        //found nothing
                        //System.out.println("true rando");
                        targetingX = mob.getX()+GameRandom.globalRandom.getIntBetween(-30, 30);
                        targetingY = mob.getY()+GameRandom.globalRandom.getIntBetween(-30, 30);
                    }

                    for(int i = 0; i <= 2; ++i) {
                        LevelEvent event = new LightningJumperEvent(player, this.getDamage(), 0, mob.getX(), mob.getY(), targetingX, targetingY, GameRandom.globalRandom.getIntBetween(-50, 50), mob);
                        this.getOwner().getLevel().entityManager.addLevelEventHidden(event);
                        if (this.getLevel().isServer()) {
                            this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketLevelEvent(event), event, player.getServerClient());
                        }
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
                                this.getLevel().getServer().network.sendToClientsWithEntityExcept(new PacketProjectileHit(this, x, y, mob), this, packetSubmitter);
                            } else {
                                this.getLevel().getServer().network.sendToClientsWithEntity(new PacketProjectileHit(this, x, y, mob), this);
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
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isServer()) {
            if (mob != null) {
                if (!mob.buffManager.hasBuff("LightningDebuff")) {
                    ActiveBuff ab = new ActiveBuff("LightningDebuff", mob, 3F, this.getOwner());
                    mob.addBuff(ab, true);
                }
            }

        }
    }

    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(124, 92, 164), 25.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    protected Color getWallHitColor() {
        return new Color(98, 43, 176);
    }

    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0F, this.lightSaturation);
    }

}
