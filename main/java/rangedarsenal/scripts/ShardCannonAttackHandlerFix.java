package rangedarsenal.scripts;

import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketFireShardCannon;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.gfx.GameResources;
import necesse.gfx.gameSound.GameSound;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import rangedarsenal.items.weapons.ShardCannonRework;

public class ShardCannonAttackHandlerFix extends MouseAngleAttackHandler {
    private final InventoryItem item;
    private final ShardCannonRework toolItem;
    private long lastTime;
    private long timeBuffer;
    private final int attackSeed;
    private int shots;
    private GameRandom random = new GameRandom();

    public ShardCannonAttackHandlerFix(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, ShardCannonRework toolItem, int seed, int startTargetX, int startTargetY) {
        super(player, slot, 20, 1000.0F, startTargetX, startTargetY);
        this.item = item;
        this.toolItem = toolItem;
        this.attackSeed = seed;
        this.lastTime = player.getWorldEntity().getLocalTime();
    }

    public void onUpdate() {
        super.onUpdate();
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.player.getX() + (int)(dir.x * 100.0F);
        int attackY = this.player.getY() + (int)(dir.y * 100.0F);
        long currentTime = this.player.getLevel().getWorldEntity().getLocalTime();
        if (this.toolItem.canAttack(this.player.getLevel(), attackX, attackY, this.player, this.item) == null) {
            this.timeBuffer += currentTime - this.lastTime;
            int seed = Item.getRandomAttackSeed(this.random.seeded((long)GameRandom.prime(this.attackSeed * this.shots)));
            Packet attackContent = new Packet();
            this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), attackX, attackY, this.player, this.item);
            this.player.showAttack(this.item, attackX, attackY, seed, attackContent);
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketShowAttack(this.player, this.item, attackX, attackY, seed, attackContent), client, client);
            }

            while(true) {
                int cooldown = this.getShootCooldown();
                if (this.timeBuffer < (long)cooldown) {
                    break;
                }

                this.timeBuffer -= (long)cooldown;
                seed = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));
                ++this.shots;
                this.toolItem.superOnAttack(this.player.getLevel(), attackX, attackY, this.player, this.player.getCurrentAttackHeight(), this.item, this.slot, 0, seed, new PacketReader(attackContent));
                if (this.player.isClient()) {
                    if (this.player.buffManager.hasBuff("ShardCannonCooldownDebuff")) {
                        Screen.playSound(GameResources.jingle, SoundEffect.effect(this.player).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 1.75F)));
                        Screen.playSound(GameResources.jingle, SoundEffect.effect(this.player).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1F, 1.5F)));
                        Screen.playSound(GameResources.jingle, SoundEffect.effect(this.player).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.7F, 1F)));
                        //Screen.playSound(GameResources.sniperrifle, SoundEffect.effect(this.player).volume(0.4F).pitch(GameRandom.globalRandom.getFloatBetween(0.7F, 1F)));
                        Screen.playSound((GameSound)GameRandom.globalRandom.getOneOf(new GameSound[]{GameResources.crystalHit2, GameResources.crystalHit3}), SoundEffect.effect(this.player).volume(1.2F).pitch(GameRandom.globalRandom.getFloatBetween(0.9F, 1.1F)));
                    } else {
                        Screen.playSound(GameResources.jingle, SoundEffect.effect(this.player).volume(3.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 1.75F)));
                        Screen.playSound((GameSound)GameRandom.globalRandom.getOneOf(new GameSound[]{GameResources.crystalHit2, GameResources.crystalHit3}), SoundEffect.effect(this.player).volume(2.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.9F, 1.1F)));
                    }
                } else if (this.player.isServer()) {
                    ServerClient client = this.player.getServerClient();
                    Server server = this.player.getLevel().getServer();
                    server.network.sendToClientsAtExcept(new PacketFireShardCannon(client.slot), client, client);
                }
            }
        }

        this.lastTime = currentTime;
    }

    private int getShootCooldown() {
        if (this.player.buffManager.hasBuff("ShardCannonCooldownDebuff")) {
            float multiplier = 3F / this.toolItem.getAttackSpeedModifier(this.item, this.player);
            return (int)(multiplier * 150.0F);
        } else {
            float multiplier = 1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.player);
            return (int)(multiplier * 150.0F);
        }
    }

    public void onEndAttack(boolean bySelf) {
        this.player.stopAttack();
        if (this.player.isServer()) {
            ServerClient client = this.player.getServerClient();
            this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketPlayerStopAttack(client.slot), client, client);
        }

    }
}