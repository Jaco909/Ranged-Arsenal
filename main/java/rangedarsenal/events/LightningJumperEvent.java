package rangedarsenal.events;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.mobAbilityLevelEvent.LightningTrailEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle.GType;
import necesse.entity.trails.LightningTrail;
import necesse.entity.trails.TrailVector;
import necesse.level.maps.LevelObjectHit;

public class LightningJumperEvent extends LightningTrailEvent implements Attacker {
    private static final int totalPoints = 14;
    private static final int distance = 70;
    private static final float distanceMod = 1.9F;
    private static final int ticksToComplete = 2;
    private int startX;
    private int startY;
    private int targetX;
    private int targetY;
    private float xDir;
    private float yDir;
    private GameDamage damage;
    private float resilienceGain;
    private int seed;
    private int tickCounter;
    private int pointCounter;
    private ArrayList<Point2D.Float> points;
    private ArrayList<Integer> hits;
    private LightningTrail trail;

    public LightningJumperEvent() {
    }
    public Mob source;
    public LightningJumperEvent(Mob owner, GameDamage damage, float resilienceGain, int startX, int startY, int targetX, int targetY, int seed, Mob mob) {
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        if (damage.damage == 0f) {
            this.damage = new GameDamage(30F,0F);
        } else {
            this.damage = new GameDamage(damage.damage/6,damage.armorPen,0f);
        }
        this.resilienceGain = resilienceGain;
        this.seed = seed;
        this.source = mob;
    }
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startX = reader.getNextInt();
        this.startY = reader.getNextInt();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
        this.seed = reader.getNextInt();
        this.tickCounter = reader.getNextShortUnsigned();
    }
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.startX);
        writer.putNextInt(this.startY);
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
        writer.putNextInt(this.seed);
        writer.putNextShortUnsigned(this.tickCounter);
    }
    public void init() {
        super.init();
        float l = (float)(new Point(this.startX, this.startY)).distance((double)this.targetX, (double)this.targetY);
        this.xDir = (float)(this.targetX - this.startX) / l;
        this.yDir = (float)(this.targetY - this.startY) / l;
        this.points = this.generatePoints();
        this.trail = new LightningTrail(new TrailVector((float)this.startX, (float)this.startY, this.xDir, this.yDir, 12.5F, 15.0F), this.level, new Color(128, 100, 194));
        this.trail.addNewPoint(new TrailVector((Point2D.Float)this.points.get(0), this.xDir, this.yDir, this.trail.thickness, 15.0F));
        if (this.isClient()) {
            this.level.entityManager.addTrail(this.trail);
        }
        this.hits = new ArrayList();
    }

    public void clientTick() {
        if (!this.isOver()) {
            ++this.tickCounter;
            int expectedCounter = this.tickCounter * totalPoints / ticksToComplete;

            while(this.pointCounter < expectedCounter) {
                ++this.pointCounter;
                if (this.pointCounter >= totalPoints) {
                    this.over();
                    break;
                }

                Point2D.Float point = (Point2D.Float)this.points.get(this.pointCounter);
                this.trail.addNewPoint(new TrailVector(point, this.xDir, this.yDir, this.trail.thickness, 15.0F));
                Point2D.Float lastPoint = (Point2D.Float)this.points.get(this.pointCounter - 1);
                Point2D.Float midPoint = new Point2D.Float((point.x + lastPoint.x) / 2.0F, (point.y + lastPoint.y) / 2.0F);
                Point2D.Float norm = GameMath.normalize(point.x - lastPoint.x, point.y - lastPoint.y);
                float distance = (float)point.distance(lastPoint);

                int j;
                for(j = 0; j < 1; ++j) {
                    this.level.entityManager.addParticle(midPoint.x + norm.x * GameRandom.globalRandom.nextFloat() * distance, midPoint.y + norm.y * GameRandom.globalRandom.nextFloat() * distance, GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 1.0), (float)(GameRandom.globalRandom.nextGaussian() * 1.0)).color(this.trail.col).height(15.0F);
                }

                if (this.pointCounter == 2) {
                    for(j = 0; j < 1; ++j) {
                        this.level.entityManager.addParticle(lastPoint.x + norm.x * 4.0F, lastPoint.y + norm.y * 4.0F, GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 20.0), (float)(GameRandom.globalRandom.nextGaussian() * 20.0)).color(this.trail.col).height(25.0F).lifeTime(250);
                    }
                }

                //This is gross and stupid
                //But the original way the vanilla event works is very buggy
                //This at least guarantees damage and keeps it localized
                if (source.getLevel().entityManager.mobs.streamArea(point.x,point.y,1) != null) {
                    source.getLevel().entityManager.mobs.streamArea(point.x,point.y,1).forEach((m) -> {
                        if (m != source) {
                            if (((m.x <= (point.x+33)) && (m.x >= (point.x-33))) && ((m.y <= (point.y+33)) && (m.y >= (point.y-33)))) {
                                if (!hasHit(m)) {
                                    int damage = Math.round(this.damage.damage);
                                    if (damage > 100) {
                                        damage = 100;
                                    } else if (damage <= 0) {
                                        damage = 1;
                                    }
                                    m.setHealth(m.getHealth() - damage, this);
                                    m.spawnDamageText(damage, 12, false);
                                    this.hits.add(m.getUniqueID());
                                }
                            }
                        }
                    });
                }
            }

        }
    }

    public void serverTick() {
        if (!this.isOver()) {
            ++this.tickCounter;
            int expectedCounter = this.tickCounter * totalPoints / ticksToComplete;

            while(this.pointCounter < expectedCounter) {
                ++this.pointCounter;
                if (this.pointCounter >= totalPoints) {
                    this.over();
                    break;
                }

                Point2D.Float point = (Point2D.Float)this.points.get(this.pointCounter);
                if (source.getLevel().entityManager.mobs.streamArea((float) point.getX(),(float) point.getY(),1) != null) {
                    source.getLevel().entityManager.mobs.streamArea((float) point.getX(),(float) point.getY(),1).forEach((m) -> {
                        if (m != source) {
                            if (((m.x <= (point.getX()+33)) && (m.x >= (point.getX()-33))) && ((m.y <= (point.getY()+33)) && (m.y >= (point.getY()-33)))) {
                                if (!hasHit(m)) {
                                    int damage = Math.round(this.damage.damage);
                                    if (damage > 40) {
                                        damage = 40;
                                    } else if (damage <= 0) {
                                        damage = 1;
                                    }
                                    m.setHealth(m.getHealth() - damage, this);
                                    ActiveBuff ab = new ActiveBuff("LightningDebuff", m, 3F, this);
                                    m.addBuff(ab, true);
                                    this.hits.add(m.getUniqueID());
                                }
                            }
                        }
                    });
                }
            }

        }
    }

    private ArrayList<Point2D.Float> generatePoints() {
        ArrayList<Point2D.Float> out = new ArrayList();
        GameRandom random = new GameRandom((long)this.seed);
        Point2D.Float perp = new Point2D.Float(-this.yDir, this.xDir);
        float lastDist = 0.0F;
        Point2D.Float lastPoint = new Point2D.Float((float)this.startX, (float)this.startY);
        out.add(lastPoint);

        for(int i = 0; i < totalPoints; ++i) {
            float fluctuation = (random.nextFloat() - 0.5F) * lastDist * 2.0F;
            lastDist = (random.nextFloat() + distanceMod) * 8.5F;
            lastPoint = new Point2D.Float(lastPoint.x + this.xDir * lastDist - perp.x * fluctuation, lastPoint.y + this.yDir * lastDist - perp.y * fluctuation);
            out.add(lastPoint);
        }

        return out;
    }

    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        super.serverHit(target, content, clientSubmitted);
        target.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this);
        this.hits.add(target.getUniqueID());
        if (target.canGiveResilience(this.owner) && this.resilienceGain != 0.0F) {
            this.owner.addResilience(this.resilienceGain);
            this.resilienceGain = 0.2F;
        }
    }
    public boolean hasHit(Mob mob) {
        return this.hits.contains(mob.getUniqueID());
    }
}