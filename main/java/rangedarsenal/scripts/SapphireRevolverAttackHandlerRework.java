package rangedarsenal.scripts;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketFireDeathRipper;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.particle.Particle;
import necesse.entity.particle.Particle.GType;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SapphireRevolverProjectileToolItem;
import rangedarsenal.items.weapons.SapphireRevolverRework;

import java.awt.geom.Point2D;

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

    public SapphireRevolverAttackHandlerRework(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, SapphireRevolverRework toolItem, int seed, int startTargetX, int startTargetY, float tier) {
        super(player, slot, 20, 1000.0F, startTargetX, startTargetY);
        this.item = item;
        this.toolItem = toolItem;
        this.seed = seed;
        this.startTime = player.getWorldEntity().getLocalTime();
        this.chargeDelay = seed;
        this.tier = tier;
    }

    public long getTimeSinceStart() {
        return this.player.getWorldEntity().getLocalTime() - this.startTime;
    }

    public float getChargePercent() {
        return Math.min((float) this.getTimeSinceStart() / this.getChargeTime(), 3.0F);
    }

    public float getChargeTime() {
        float multiplier = (1.0F-(this.tier/10)) / this.toolItem.getAttackSpeedModifier(this.item, this.player);
        return (float) ((int) (multiplier * 1000.0F));
    }

    public void onUpdate() {
        super.onUpdate();
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.player.getX() + (int) (dir.x * 100.0F);
        int attackY = this.player.getY() + (int) (dir.y * 100.0F);
        long currentTime = this.player.getLevel().getWorldEntity().getLocalTime();
        if (this.toolItem.canAttack(this.player.getLevel(), attackX, attackY, this.player, this.item) == null) {
            Packet attackContent = new Packet();
            this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), attackX, attackY, this.player, this.item);
            this.player.showAttack(this.item, attackX, attackY, this.seed, attackContent);
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketShowAttack(this.player, this.item, attackX, attackY, this.seed, attackContent), client, client);
            } else if (this.getChargePercent() >= 3.0F && !this.charged3) {
                this.charged3 = true;
                Screen.playSound(GameResources.cling, SoundEffect.effect(this.player).volume(1.0F).pitch(2.0F));
                Screen.playSound(GameResources.jingle, SoundEffect.effect(this.player).volume(1.0F).pitch(1.0F));
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{GType.CRITICAL, GType.IMPORTANT_COSMETIC, GType.COSMETIC});
                float anglePerParticle = 12.0F;

                for (int i = 0; i < 30; ++i) {
                    int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float) Math.sin(Math.toRadians((double) angle)) * 50.0F;
                    float dy = (float) Math.cos(Math.toRadians((double) angle)) * 50.0F * 0.8F;
                    this.player.getLevel().entityManager.addParticle(this.player, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(new Color(253, 44, 44)).heightMoves(0.0F, 10.0F).sizeFades(22, 44).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).lifeTime(500);
                }
            } else if (this.getChargePercent() >= 2.0F && !this.charged2) {
                this.charged2 = true;
                Screen.playSound(GameResources.cling, SoundEffect.effect(this.player).volume(1.0F).pitch(1.2F));
                Screen.playSound(GameResources.jingle, SoundEffect.effect(this.player).volume(0.75F).pitch(1.0F));
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{GType.CRITICAL, GType.IMPORTANT_COSMETIC, GType.COSMETIC});
                float anglePerParticle = 24.0F;

                for (int i = 0; i < 15; ++i) {
                    int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float) Math.sin(Math.toRadians((double) angle)) * 50.0F;
                    float dy = (float) Math.cos(Math.toRadians((double) angle)) * 50.0F * 0.8F;
                    this.player.getLevel().entityManager.addParticle(this.player, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(new Color(255, 225, 71)).heightMoves(0.0F, 10.0F).sizeFades(22, 44).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).lifeTime(500);
                }
            } else if (this.getChargePercent() >= 1.0F && !this.charged) {
                this.charged = true;
                Screen.playSound(GameResources.cling, SoundEffect.effect(this.player).volume(1.0F).pitch(0.7F));
                Screen.playSound(GameResources.jingle, SoundEffect.effect(this.player).volume(0.5F).pitch(1.0F));
                ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{GType.CRITICAL, GType.IMPORTANT_COSMETIC, GType.COSMETIC});
                float anglePerParticle = 36.0F;

                for (int i = 0; i < 10; ++i) {
                    int angle = (int) ((float) i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                    float dx = (float) Math.sin(Math.toRadians((double) angle)) * 50.0F;
                    float dy = (float) Math.cos(Math.toRadians((double) angle)) * 50.0F * 0.8F;
                    this.player.getLevel().entityManager.addParticle(this.player, typeSwitcher.next()).movesFriction(dx, dy, 0.8F).color(new Color(116, 245, 253)).heightMoves(0.0F, 10.0F).sizeFades(22, 44).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).lifeTime(500);
                }
            }
        }

    }

    public void onEndAttack(boolean bySelf) {
        if (this.getChargePercent() >= 1.0F) {
            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            int attackX = this.player.getX() + (int) (dir.x * 100.0F);
            int attackY = this.player.getY() + (int) (dir.y * 100.0F);
            Packet attackContent = new Packet();
            this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), attackX, attackY, this.player, this.item);
            this.toolItem.onAttack(this.player.getLevel(), attackX, attackY, this.player, this.player.getCurrentAttackHeight(), this.item, this.slot, 0, this.seed, new PacketReader(attackContent),this.getChargePercent());
            if (this.player.isClient() && this.charged3) {
                Screen.playSound(GameResources.shatter1, SoundEffect.effect(this.player).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.3F, 1.7F)));
                Screen.playSound(GameResources.sniperrifle, SoundEffect.effect(this.player).volume(1.1F).pitch(GameRandom.globalRandom.getFloatBetween(0.4F, 0.6F)));
                Screen.playSound(GameResources.laserBlast1, SoundEffect.effect(this.player).volume(2.2F).pitch(GameRandom.globalRandom.getFloatBetween(0.95F, 1.25F)));
            } else if (this.player.isClient() && this.charged2) {
                Screen.playSound(GameResources.shatter1, SoundEffect.effect(this.player).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.3F, 1.7F)));
                Screen.playSound(GameResources.sniperrifle, SoundEffect.effect(this.player).volume(0.7F).pitch(GameRandom.globalRandom.getFloatBetween(0.4F, 0.6F)));
                Screen.playSound(GameResources.laserBlast1, SoundEffect.effect(this.player).volume(1.5F).pitch(GameRandom.globalRandom.getFloatBetween(0.75F, 0.9F)));
            } else if (this.player.isClient() && this.charged) {
                Screen.playSound(GameResources.shatter1, SoundEffect.effect(this.player).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.3F, 1.7F)));
                Screen.playSound(GameResources.sniperrifle, SoundEffect.effect(this.player).volume(0.5F).pitch(GameRandom.globalRandom.getFloatBetween(0.4F, 0.6F)));
                Screen.playSound(GameResources.laserBlast1, SoundEffect.effect(this.player).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.45F, 0.65F)));
            } else if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                Server server = this.player.getLevel().getServer();
                server.network.sendToClientsAtExcept(new PacketFireDeathRipper(client.slot), client, client);
            }
        }

        this.player.stopAttack();
        if (this.player.isServer()) {
            ServerClient client = this.player.getServerClient();
            this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketPlayerStopAttack(client.slot), client, client);
        }

    }
}
