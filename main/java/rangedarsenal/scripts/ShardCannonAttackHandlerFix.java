package rangedarsenal.scripts;

import java.awt.geom.Point2D;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketFireShardCannon;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.engine.sound.gameSound.GameSound;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.ShardCannonProjectileToolItem;
import rangedarsenal.items.weapons.ShardCannonRework;

public class ShardCannonAttackHandlerFix extends MouseAngleAttackHandler {
    private final InventoryItem item;
    private final ShardCannonRework toolItem;
    private long lastTime;
    private long timeBuffer;
    private final int attackSeed;
    private int shots;
    private final GameRandom random = new GameRandom();

    public ShardCannonAttackHandlerFix(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, ShardCannonRework toolItem, int seed, int startTargetX, int startTargetY) {
        super(attackerMob, slot, 50, 1000.0F, startTargetX, startTargetY);
        this.item = item;
        this.toolItem = toolItem;
        this.attackSeed = seed;
        this.lastTime = attackerMob.getLocalTime();
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null && !ChaserAINode.hasLineOfSightToTarget(this.attackerMob, this.lastItemAttackerTarget, 5.0F)) {
            this.attackerMob.endAttackHandler(true);
        } else {
            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            int attackX = this.attackerMob.getX() + (int)(dir.x * 100.0F);
            int attackY = this.attackerMob.getY() + (int)(dir.y * 100.0F);
            long currentTime = this.attackerMob.getLevel().getLocalTime();
            if (this.toolItem.canAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.item) == null) {
                this.timeBuffer += currentTime - this.lastTime;
                int seed = Item.getRandomAttackSeed(this.random.seeded((long)GameRandom.prime(this.attackSeed * this.shots)));
                GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(this.item, attackX, attackY, 0, seed);

                while(true) {
                    int cooldown = this.getShootCooldown();
                    if (this.timeBuffer < (long)cooldown) {
                        break;
                    }

                    this.timeBuffer -= (long)cooldown;
                    seed = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));
                    ++this.shots;
                    this.toolItem.superOnAttack(this.attackerMob.getLevel(), attackX, attackY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), this.item, this.slot, 0, seed, attackMap);
                    if (this.attackerMob.isClient()) {
                        if (this.attackerMob.buffManager.hasBuff("ShardCannonCooldownDebuff")) {
                            SoundManager.playSound(GameResources.jingle, SoundEffect.effect(this.attackerMob).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 1.75F)));
                            SoundManager.playSound(GameResources.jingle, SoundEffect.effect(this.attackerMob).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(1F, 1.5F)));
                            SoundManager.playSound(GameResources.jingle, SoundEffect.effect(this.attackerMob).volume(1.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.7F, 1F)));
                            SoundManager.playSound((GameSound)GameRandom.globalRandom.getOneOf(new GameSound[]{GameResources.crystalHit2, GameResources.crystalHit3}), SoundEffect.effect(this.attackerMob).volume(1.2F).pitch(GameRandom.globalRandom.getFloatBetween(0.9F, 1.1F)));
                        } else {
                            SoundManager.playSound(GameResources.jingle, SoundEffect.effect(this.attackerMob).volume(3.0F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 1.75F)));
                            SoundManager.playSound((GameSound)GameRandom.globalRandom.getOneOf(new GameSound[]{GameResources.crystalHit2, GameResources.crystalHit3}), SoundEffect.effect(this.attackerMob).volume(2.0F).pitch(GameRandom.globalRandom.getFloatBetween(0.9F, 1.1F)));
                        }
                    } else if (this.attackerMob.isServer()) {
                        this.attackerMob.sendAttackerPacket(this.attackerMob, new PacketFireShardCannon(this.attackerMob));
                    }
                }
            }

            this.lastTime = currentTime;
        }
    }

    private int getShootCooldown() {
        if (this.attackerMob.buffManager.hasBuff("ShardCannonCooldownDebuff")) {
            float multiplier = 3F / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob);
            return (int)(multiplier * 150.0F);
        } else {
            float multiplier = 1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.attackerMob);
            return (int)(multiplier * 150.0F);
        }
    }

    public void onEndAttack(boolean bySelf) {
        this.attackerMob.doAndSendStopAttackAttacker(false);
    }
}