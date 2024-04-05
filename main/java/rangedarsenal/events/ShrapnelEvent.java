package rangedarsenal.events;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
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

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.Function;

public class ShrapnelEvent extends MobAbilityLevelEvent implements Attacker {
    //modified clone of LightningTrailEvent
    private static final int totalPoints = 4;
    private static final int distance = 70;
    private static final float distanceMod = 1F;
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
    float closest;
    int ray;

    public ShrapnelEvent() {
    }
    public Mob source;
    public ShrapnelEvent(Mob owner, GameDamage damage, float resilienceGain, int startX, int startY, int seed, Mob mob, int ray) {
        super(owner, new GameRandom((long)seed));

        this.startX = startX;
        this.startY = startY;
        this.damage = new GameDamage(0F,0F);
        this.seed = seed;
        this.source = mob;
        //this is gross but im druck fuck you eat my if pyriamid

        //huh, well I guess it works
        if (ray == 0) {
            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(-5, 5);
            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(5, 15);
        } else if (ray == 1) {
            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(5, 15);
            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(5, 15);
        } else if (ray == 2) {
            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(5, 15);
            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(-5, 5);
        } else if (ray == 3) {
            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(5, 15);
            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(-5, -15);
        } else if (ray == 4) {
            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(-5, 5);
            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(-5, -15);
        } else if (ray == 5) {
            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(-15, -5);
            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(-15, -5);
        } else if (ray == 6) {
            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(-15, -5);
            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(-5, 5);
        } else if (ray == 7) {
            this.targetX = mob.getX()+GameRandom.globalRandom.getIntBetween(-15, -5);
            this.targetY = mob.getY()+GameRandom.globalRandom.getIntBetween(5, 15);
        }
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
        this.trail = new LightningTrail(new TrailVector((float)this.startX, (float)this.startY, this.xDir, this.yDir, 6F, 15.0F), this.level, new Color(208, 148, 37));
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
                Point2D p1 = (Point2D)this.points.get(this.pointCounter - 1);
                Point2D p2 = (Point2D)this.points.get(this.pointCounter);
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
            lastDist = (random.nextFloat() + distanceMod) * 8.5F;
            lastPoint = new Point2D.Float(lastPoint.x + this.xDir * lastDist - perp.x, lastPoint.y + this.yDir * lastDist - perp.y);
            out.add(lastPoint);
        }

        return out;
    }

    public void clientHit(Mob target, Packet content) {
    }

    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
    }

    public void hit(LevelObjectHit hit) {
    }
}
