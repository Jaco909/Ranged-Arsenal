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
import necesse.entity.particle.Particle.GType;
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

public class BeamRifleEvent extends MobAbilityLevelEvent implements Attacker {
    //modified clone of LightningTrailEvent
    private static final int totalPoints = 5;
    private static final float distanceMod = 21F;
    private static final int ticksToComplete = 1;
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
    private Color color;
    private int beam;
    private ArrayList<Point2D.Float> points;
    private ArrayList<Integer> hits;
    private LightningTrailToFixFade trail;
    private String time;
    private int second;
    float closest;

    public BeamRifleEvent() {
    }
    public Mob source;
    public BeamRifleEvent(Mob owner, GameDamage damage, float resilienceGain, int startX, int startY, int targetX, int targetY, int seed, Mob mob, Color color, int beam, int range) {
        super(owner, new GameRandom((long)seed));
        this.startX = startX;
        this.startY = startY;
        if (targetX > startX+range) {
            this.targetX = startX+range;
        } else if (targetX < startX-range) {
            this.targetX = startX-range;
        } else {
            if (targetX < 0) {
                this.targetX = targetX-5;
            } else {
                this.targetX = targetX+20;
            }
        }
        if (targetY > startY+range-111) {
            this.targetY = startY+range-111;
        } else if (targetY < startY-range-111) {
            this.targetY = startY-range-111;
        } else {
            this.targetY = targetY;
        }
        this.damage = damage;
        this.resilienceGain = resilienceGain;
        this.seed = seed;
        this.source = mob;
        this.color = color;
        this.beam = beam;
        this.time = Long.toString(owner.getWorldTime());
        this.second = this.time.charAt(this.time.length()-3);
        this.second-=48;
        if (this.beam == 1) {
            if (this.second == 5 || this.second == 0) {
                this.color = new Color(0, 94, 224);
            } else if (this.second == 2 || this.second == 7) {
                this.color = new Color(94, 147, 225);
            }
        }
        if (this.beam == 2) {
            if (this.second == 5 || this.second == 0) {
                this.color = new Color(252, 105, 22);
            } else if (this.second == 2 || this.second == 7) {
                this.color = new Color(225, 125, 79);
            }
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
        this.trail = new LightningTrailToFixFade(new TrailVector((float)this.startX, (float)this.startY, this.xDir, this.yDir, 40.0F, 18.0F), this.level, this.color,30,new GameSprite(GameResources.chains, 7, 0, 32));
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
                    //this.level.entityManager.addParticle(midPoint.x + norm.x * GameRandom.globalRandom.nextFloat() * distance, midPoint.y + norm.y * GameRandom.globalRandom.nextFloat() * distance, GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 4.0), (float)(GameRandom.globalRandom.nextGaussian() * 4.0)).color(this.trail.col).height(18.0F);
                }

                if (this.pointCounter == 15) {
                    for(j = 0; j < 20; ++j) {
                        //this.level.entityManager.addParticle(lastPoint.x + norm.x * 4.0F, lastPoint.y + norm.y * 4.0F, GType.COSMETIC).movesConstant((float)(GameRandom.globalRandom.nextGaussian() * 20.0), (float)(GameRandom.globalRandom.nextGaussian() * 20.0)).color(this.trail.col).height(18.0F).lifeTime(50);
                    }
                }

                Line2D line = new Line2D.Double(lastPoint.getX(), lastPoint.getY(), point.getX(), point.getY());
                LineHitbox hitbox = new LineHitbox(line, 20.0F);
                this.handleHits(hitbox, (m) -> {
                    return m.canBeHit(this) && !this.hasHit(m);
                }, (Function)null);
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
        if (this.beam == 1) {
            //System.out.println(this.time);
            out.add(new Point2D.Float(this.startX, this.startY));
            if (this.second < 5) {
                out.add(new Point2D.Float(this.startX+30, this.startY+10-this.second*5f));
                out.add(new Point2D.Float((this.targetX+this.startX)/2f, this.targetY+20-this.second*10f));
                out.add(new Point2D.Float(this.targetX-30, this.targetY+10-this.second*5f));
            } else {
                out.add(new Point2D.Float(this.startX+30, this.startY-35+this.second*5f));
                out.add(new Point2D.Float((this.targetX+this.startX)/2f, this.targetY-70+this.second*10f));
                out.add(new Point2D.Float(this.targetX-30, this.targetY-35+this.second*5f));
            }
            out.add(new Point2D.Float(this.targetX-18, this.targetY));
        } else {
            out.add(new Point2D.Float(this.startX, this.startY));
            if (this.second < 5) {
                out.add(new Point2D.Float(this.startX+30, this.startY-10+this.second*5f));
                out.add(new Point2D.Float((this.targetX+this.startX)/2f, this.targetY-20+this.second*10f));
                out.add(new Point2D.Float(this.targetX-30, this.targetY-10+this.second*5f));
            } else {
                out.add(new Point2D.Float(this.startX+30, this.startY+35-this.second*5f));
                out.add(new Point2D.Float((this.targetX+this.startX)/2f, this.targetY+70-this.second*10f));
                out.add(new Point2D.Float(this.targetX-30, this.targetY+35-this.second*5f));
            }
            out.add(new Point2D.Float(this.targetX-18, this.targetY));
        }


        /*for(int i = 0; i < totalPoints; ++i) {
            float fluctuation = (random.nextFloat() - 0.5F) * lastDist;
            lastDist = (random.nextFloat() + 0.5F) * distanceMod;
            lastPoint = new Point2D.Float(lastPoint.x + this.xDir * lastDist - perp.x, lastPoint.y + this.yDir * lastDist - perp.y);
            out.add(lastPoint);
        }*/

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
        return this.getDeathMessages("beamrifle", 2);
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
