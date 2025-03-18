package rangedarsenal.scripts;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.Particle.GType;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import rangedarsenal.items.weapons.SapphireRevolverRework;

import java.util.Iterator;

public class SapphireRevolverAttackHandlerRework extends MouseAngleAttackHandler {
    public int chargeDelay = 1000;
    private final long startTime;
    public SapphireRevolverRework toolItem;
    public InventoryItem item;
    private final int seed;
    private boolean charged;
    private boolean charged2;
    private boolean charged3;
    private float tier;
    protected int endAttackBuffer;

    public SapphireRevolverAttackHandlerRework(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, SapphireRevolverRework toolItem, int seed, int startTargetX, int startTargetY, float tier) {
        super(attackerMob, slot, 20, 1000.0F, startTargetX, startTargetY);
        this.item = item;
        this.toolItem = toolItem;
        this.seed = seed;
        this.startTime = attackerMob.getWorldEntity().getLocalTime();
        this.chargeDelay = seed;
        this.tier = tier;
    }

    public long getTimeSinceStart() {
        return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        return Math.min((float) this.getTimeSinceStart() / this.getChargeTime(), 3.0F);
    }

    public float getChargeTime() {
        float multiplier = (1.0F-(this.tier/10)) / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob);
        return (float) ((int) (multiplier * 1000.0F));
    }
    public void onUpdate() {
        super.onUpdate();
        float chargePercent = this.getChargePercent();
        if (!this.attackerMob.isPlayer && chargePercent >= 1.0F) {
            this.endAttackBuffer += this.updateInterval;
            if (this.endAttackBuffer >= 350) {
                this.endAttackBuffer = 0;
                this.attackerMob.endAttackHandler(true);
                return;
            }
        }

        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0F);
        int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0F);
        if (this.toolItem.canAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.item) == null) {
            this.attackerMob.showAttackAndSendAttacker(this.item, attackX, attackY, 0, this.seed);
            if (this.attackerMob.isClient() && this.getChargePercent() >= 3.0F && !this.charged3) {
                this.charged3 = true;
                SoundManager.playSound(GameResources.cling, SoundEffect.effect(this.attackerMob).volume(1.0F).pitch(2.0F));
                SoundManager.playSound(GameResources.jingle, SoundEffect.effect(this.attackerMob).volume(1.0F).pitch(1.0F));
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{GType.CRITICAL, GType.IMPORTANT_COSMETIC, GType.COSMETIC});
                float anglePerParticle = 12.0F;

                for (int i = 0; i < 30; ++i) {
                    int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float) Math.sin(Math.toRadians((double) angle)) * 50.0F;
                    float dy = (float) Math.cos(Math.toRadians((double) angle)) * 50.0F * 0.8F;
                    this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(new Color(253, 44, 44)).heightMoves(0.0F, 10.0F).sizeFades(22, 44).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).lifeTime(500);
                }
            } else if (this.attackerMob.isClient() && this.getChargePercent() >= 2.0F && !this.charged2) {
                this.charged2 = true;
                SoundManager.playSound(GameResources.cling, SoundEffect.effect(this.attackerMob).volume(1.0F).pitch(1.2F));
                SoundManager.playSound(GameResources.jingle, SoundEffect.effect(this.attackerMob).volume(0.75F).pitch(1.0F));
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{GType.CRITICAL, GType.IMPORTANT_COSMETIC, GType.COSMETIC});
                float anglePerParticle = 24.0F;

                for (int i = 0; i < 15; ++i) {
                    int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float) Math.sin(Math.toRadians((double) angle)) * 50.0F;
                    float dy = (float) Math.cos(Math.toRadians((double) angle)) * 50.0F * 0.8F;
                    this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(new Color(255, 225, 71)).heightMoves(0.0F, 10.0F).sizeFades(22, 44).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).lifeTime(500);
                }
            }
            else if (this.attackerMob.isClient() && this.getChargePercent() >= 1.0F && !this.charged) {
                this.charged = true;
                SoundManager.playSound(GameResources.cling, SoundEffect.effect(this.attackerMob).volume(1.0F).pitch(0.7F));
                SoundManager.playSound(GameResources.jingle, SoundEffect.effect(this.attackerMob).volume(0.5F).pitch(1.0F));
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{GType.CRITICAL, GType.IMPORTANT_COSMETIC, GType.COSMETIC});
                float anglePerParticle = 36.0F;

                for (int i = 0; i < 10; ++i) {
                    int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float) Math.sin(Math.toRadians((double) angle)) * 50.0F;
                    float dy = (float) Math.cos(Math.toRadians((double) angle)) * 50.0F * 0.8F;
                    this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(new Color(116, 245, 253)).heightMoves(0.0F, 10.0F).sizeFades(22, 44).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).lifeTime(500);
                }
            }
        }

    }

    public void onEndAttack(boolean bySelf) {
        if (this.getChargePercent() >= 1.0F) {
            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0F);
            int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0F);
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("charge", this.getChargePercent());
            if (this.attackerMob.isClient() && this.charged3) {
                attackItem.getGndData().setBoolean("charged3", true);
            } else if (this.attackerMob.isClient() && this.charged2) {
                attackItem.getGndData().setBoolean("charged2", true);
            } else if (this.attackerMob.isClient() && this.charged) {
                attackItem.getGndData().setBoolean("charged", true);
            }
            GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(attackItem, attackX, attackY, 0, this.seed);
            this.toolItem.superOnAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, this.seed, attackMap);
            Iterator var7 = this.attackerMob.buffManager.getArrayBuffs().iterator();

            while(var7.hasNext()) {
                ActiveBuff b = (ActiveBuff)var7.next();
                b.onItemAttacked(attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0);
            }
        }

        this.attackerMob.doAndSendStopAttackAttacker(false);
    }
}
