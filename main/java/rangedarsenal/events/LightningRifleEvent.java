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
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;
import rangedarsenal.scripts.LightningTrailToFixFade;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.function.Function;

public class LightningRifleEvent extends MobAbilityLevelEvent implements Attacker {
    //modified clone of LightningTrailEvent
    private static final int totalPoints = 16;
    private static final int distance = 70;
    private static final float distanceMod = 21F;
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
    private LightningTrailToFixFade trail;
    float closest;

    public LightningRifleEvent() {
    }
    public Mob source;
    public LightningRifleEvent(Mob owner, GameDamage damage, float resilienceGain, int startX, int startY, int targetX, int targetY, int seed, Mob mob) {
        super(owner, new GameRandom((long)seed));

        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.damage = damage;
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
        this.trail = new LightningTrailToFixFade(new TrailVector((float)this.startX, (float)this.startY, this.xDir, this.yDir, 35.0F, 18.0F), this.level, new Color(130, 79, 196),92,new GameSprite(GameResources.chains, 7, 0, 32));
        this.trail.addNewPoint(new TrailVector((Point2D.Float)this.points.get(0), this.xDir, this.yDir, this.trail.thickness, 18.0F));
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
                this.trail.addNewPoint(new TrailVector(point, this.xDir, this.yDir, this.trail.thickness, 18.0F));
                Point2D.Float lastPoint = (Point2D.Float)this.points.get(this.pointCounter - 1);
                Point2D.Float midPoint = new Point2D.Float((point.x + lastPoint.x) / 2.0F, (point.y + lastPoint.y) / 2.0F);
                Point2D.Float norm = GameMath.normalize(point.x - lastPoint.x, point.y - lastPoint.y);
                float distance = (float)point.distance(lastPoint);

                int j;
                for(j = 0; j < 2; ++j) {
                    this.level.entityManager.addParticle(midPoint.x + norm.x * GameRandom.globalRandom.nextFloat() * distance, midPoint.y + norm.y * GameRandom.globalRandom.nextFloat() * distance, GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 4.0), (float)(GameRandom.globalRandom.nextGaussian() * 4.0)).color(this.trail.col).height(18.0F);
                }

                if (this.pointCounter == 15) {
                    for(j = 0; j < 20; ++j) {
                        this.level.entityManager.addParticle(lastPoint.x + norm.x * 4.0F, lastPoint.y + norm.y * 4.0F, GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 20.0), (float)(GameRandom.globalRandom.nextGaussian() * 20.0)).color(this.trail.col).height(18.0F).lifeTime(50);
                    }
                }

                Line2D line = new Line2D.Double(lastPoint.getX(), lastPoint.getY(), point.getX(), point.getY());
                LineHitbox hitbox = new LineHitbox(line, 20.0F);
                this.handleHits(hitbox, (m) -> {
                    return m.canBeHit(this) && !this.hasHit(m);
                }, (Function)null);
                if (this.pointCounter > 4) {
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

                Point2D p1 = (Point2D)this.points.get(this.pointCounter - 1);
                Point2D p2 = (Point2D)this.points.get(this.pointCounter);
                Line2D line = new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
                LineHitbox hitbox = new LineHitbox(line, 20.0F);
                this.handleHits(hitbox, (m) -> {
                    return !this.hasHit(m);
                }, (Function)null);
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
            float fluctuation = (random.nextFloat() - 0.5F) * lastDist;
            lastDist = (random.nextFloat() + 0.5F) * distanceMod;
            lastPoint = new Point2D.Float(lastPoint.x + this.xDir * lastDist - perp.x * fluctuation, lastPoint.y + this.yDir * lastDist - perp.y * fluctuation);
            out.add(lastPoint);
        }

        return out;
    }

    public void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        this.hits.add(target.getUniqueID());
    }

    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        super.serverHit(target, content, clientSubmitted);
        target.isServerHit(this.damage, 0.0F, 0.0F, 0.0F, this);
        this.hits.add(target.getUniqueID());
        if (target.canGiveResilience(this.owner) && this.resilienceGain != 0.0F) {
            this.owner.addResilience(this.resilienceGain);
            this.resilienceGain = 0.0F;
        }

    }

    public void hit(LevelObjectHit hit) {
        super.hit(hit);
        hit.getLevelObject().attackThrough(this.damage, this);
    }

    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("lightning", 2);
    }

    public GameMessage getAttackerName() {
        return (GameMessage)(this.owner != null ? this.owner.getAttackerName() : new LocalMessage("deaths", "unknownatt"));
    }

    public Mob getFirstAttackOwner() {
        return this.owner;
    }

    public boolean hasHit(Mob mob) {
        return this.hits.contains(mob.getUniqueID());
    }
}
