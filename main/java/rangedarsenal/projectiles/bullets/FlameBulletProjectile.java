package rangedarsenal.projectiles.bullets;

import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

import java.awt.*;

public class FlameBulletProjectile extends BulletProjectile {
    public FlameBulletProjectile() {
    }
    GameDamage ogdamage;
    public FlameBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }
    public void init() {
        super.init();
    }

    public void onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        //because the living shotty broke this yaaaaaaaaaaay
        GameDamage ogdamage = this.getDamage();
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
                    String mobname = mob.idData.getStringID().toString().toLowerCase();
                    if (mobname.contains("zombie") || mobname.contains("vampire") || mobname.contains("mummy")) {
                        this.setDamage(ogdamage.modDamage(1.5f));
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
                    this.setDamage(ogdamage);

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
                ActiveBuff ab = new ActiveBuff("HellfireBuff", mob, 3F, this.getOwner());
                mob.addBuff(ab, true);
            }

        }
    }

    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(250, 70, 0), 25.0F, 150, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    protected Color getWallHitColor() {
        return new Color(230, 90, 0);
    }

    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0F, this.lightSaturation);
    }
}