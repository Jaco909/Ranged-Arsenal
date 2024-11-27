package rangedarsenal.projectiles.bullets;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.engine.util.ComputedValue;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.awt.Point;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;


public class NewVoidBulletProjectile extends FollowingProjectile {
    private boolean isStoppedAtTarget;
    private float originalSpeed;
    private float originalAngle;
    private float currentAngle;
    private float highBound;
    private float lowBound;
    private float maxDeviation = 116f;
    private int flipped = 0;
    private int passes = 0;
    private boolean failed = false;
    private float previousAngle;
    public NewVoidBulletProjectile() {
        this.height = 18.0F;
    }

    public NewVoidBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
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
        this.originalAngle = this.getAngleToTarget(owner.x,owner.y,targetX,targetY);
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.height);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.height = reader.getNextFloat();
    }

    public void init() {
        super.init();
        this.turnSpeed = 0.15F;
        this.givesLight = true;
        this.trailOffset = 0.0F;
    }

    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(47, 0, 142), 22.0F, 100, this.height);
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    protected Color getWallHitColor() {
        return new Color(47, 0, 142);
    }

    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 260.0F, this.lightSaturation);
    }

    public void updateTarget() {
        if (this.traveledDistance > 40.0F) {
            this.findTarget((m) -> {
                return m.isHostile;
            }, 90.0F, 160.0F);
        }

    }

    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        //System.out.println("OA" + this.originalAngle);

        if (this.passes > 0) {
            //System.out.println("PA" + this.previousAngle);
            this.currentAngle = this.getAngleToTarget(this.x,this.y,targetX,targetY);

            //FOR SOME REASON
            //ANYTHING THAT PRE-SETS THE ANGLE CAN +- 180 TO THE ANGLE
            //I DON'T KNOW WHY
            //BUT I WILL BEAT IT INTO SUBMISSION
            if (this.currentAngle >= (this.previousAngle + 179) && (this.currentAngle <= (this.previousAngle  + 181))) {
                //System.out.println("BCA" + this.currentAngle);
                // && this.flipped == 0
                this.currentAngle = this.currentAngle - 180;
                //System.out.println("fix1----------------------------------------------");
            } else if (this.currentAngle <= (this.previousAngle  - 179) && (this.currentAngle >= (this.previousAngle  - 181))) {
                //System.out.println("BCA" + this.currentAngle);
                this.currentAngle = this.currentAngle + 180;
                //System.out.println("fix2----------------------------------------------");
            }

            //Fix for bullets flipping over 0/360 angle
            //System.out.println("CA" + this.currentAngle);
            if ((this.currentAngle > (this.previousAngle + 300)) || (this.flipped == -1)) {
                this.flipped = -1;
                this.currentAngle = this.currentAngle + 180;
                //System.out.println("FCA" + this.currentAngle);
            } else if ((this.currentAngle < (this.previousAngle - 300)) || (this.flipped == 1)) {
                this.flipped = 1;
                this.currentAngle = this.currentAngle - 180;
                //System.out.println("FCA" + this.currentAngle);
            }
            this.highBound = originalAngle + maxDeviation;
            //System.out.println("HB" + this.highBound);
            this.lowBound = originalAngle - maxDeviation;
            //System.out.println("LB" + this.lowBound);
        }

        //DO NOT SIMPLIFY THE THIS.FAILED CHECKS
        //I DON'T KNOW WHY
        //BUT IT READS THE ! CHECKS WRONG
        //JAVA WHY
        //WHY
        if ((((this.currentAngle >= this.highBound) || (this.currentAngle <= this.lowBound)) && (this.passes > 0)) || (this.failed == true)) {
            //System.out.println("wack+++++++++++++++++++++++++++++++++++++++++++");
            this.turnSpeed = this.turnSpeed / 2.6f;
            this.failed = true;
            this.previousAngle = this.currentAngle;
            this.passes++;
        } else if (this.failed == false) {
            //System.out.println("cool");
            this.passes++;
            if (this.currentAngle == 0) {
                this.previousAngle = this.getAngleToTarget(this.x, this.y, targetX, targetY);
            } else {
                this.previousAngle = this.currentAngle;
            }
            if (!this.isStoppedAtTarget) {
                this.updateTarget();
                if (this.hasTarget()) {
                    float delta = (float) movedDist;
                    int tx = this.getTargetX();
                    int ty = this.getTargetY();
                    float angle = this.getTurnSpeed(tx, ty, delta);
                    if (this.angleLeftToTurn >= 0.0F) {
                        angle = Math.min(this.angleLeftToTurn, angle);
                        this.angleLeftToTurn -= angle;
                    }

                    if (this.turnToward((float) tx, (float) ty, angle)) {
                        if (this.clearTargetPosWhenAligned) {
                            this.targetPos = null;
                        }

                        if (this.clearTargetWhenAligned) {
                            this.target = null;
                        }

                        if (this.stopsAtTarget) {
                            float toTarget = this.hasTarget() ? (float) (new Point(this.getTargetX(), this.getTargetY())).distance((double) this.x, (double) this.y) : 1.0F;
                            if ((double) toTarget <= movedDist) {
                                this.isStoppedAtTarget = true;
                                this.originalSpeed = this.speed;
                            }
                        }
                    }
                }
            }

            if (this.isStoppedAtTarget) {
                this.speed = 0.0F;
                this.x = (float) this.getTargetX();
                this.y = (float) this.getTargetY();
            }
        }
    }

    public float getTurnSpeed(int targetX, int targetY, float delta) {
        return this.getTurnSpeed(delta) * this.getTurnSpeedMod(targetX, targetY, 20.0F, 90.0F, 160.0F);
    }

    public float getTurnSpeedMod(int targetX, int targetY, float maxMod, float maxAngle, float maxDistance) {
        float distance = (float)(new Point(targetX, targetY)).distance((double)this.getX(), (double)this.getY());
        if (distance < maxDistance && distance > 5.0F) {
            float deltaAngle = Math.abs(this.getAngleDifference(this.getAngleToTarget((float)targetX, (float)targetY)));
            float angleMod = deltaAngle > maxAngle ? 1.0F : (deltaAngle - maxAngle) / maxAngle;
            float distMod = Math.abs(distance - maxDistance) / maxDistance;
            return 1.0F + distMod * maxMod + angleMod * maxMod;
        } else {
            return 1.0F;
        }
    }

    public float getTurnSpeedMod(int targetX, int targetY, float maxAngle, float maxAngleMod, float maxDistance, float maxDistMod) {
        float distance = (float)(new Point(targetX, targetY)).distance((double)this.getX(), (double)this.getY());
        if (distance < maxDistance && distance > 5.0F) {
            float deltaAngle = Math.abs(this.getAngleDifference(this.getAngleToTarget((float)targetX, (float)targetY)));
            float angleMod = deltaAngle > maxAngle ? 1.0F : deltaAngle / maxAngle;
            float distMod = distance / maxDistance;
            return 1.0F + distMod * maxDistMod + angleMod * maxAngleMod;
        } else {
            return 1.0F;
        }
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    public void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.gunhit, SoundEffect.effect(x, y));
    }

}
