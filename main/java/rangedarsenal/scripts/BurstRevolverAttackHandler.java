package rangedarsenal.scripts;

import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseAngleAttackHandler;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import rangedarsenal.items.weapons.DoubleBarrel;
import rangedarsenal.items.weapons.NormalRevolver;

import java.awt.geom.Point2D;

public class BurstRevolverAttackHandler extends MouseAngleAttackHandler {
    private final InventoryItem item;
    private final NormalRevolver toolItem;
    private final int attackSeed;
    private int shotsRemaining = 6;
    private int shots;
    private int bullets;
    private long timeBuffer;
    private final GameRandom random = new GameRandom();
    private int timeBetweenReloads = 1700;
    private int timeBetweenBurstShots = 180;

    public BurstRevolverAttackHandler(PlayerMob player, PlayerInventorySlot slot, InventoryItem item, NormalRevolver toolItem, int seed, int startTargetX, int startTargetY) {
        super(player, slot, 20, 1000.0F, startTargetX, startTargetY);
        this.attackSeed = seed;
        this.timeBuffer = (long)this.timeBetweenReloads;
        this.item = item;
        this.toolItem = toolItem;
        this.timeBetweenReloads = 1700 - Math.round(toolItem.getUpgradeTier(item)*100);
        this.timeBetweenBurstShots = 180 - Math.round(toolItem.getUpgradeTier(item)*8);
    }

    public void onUpdate() {
        super.onUpdate();
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        int attackX = this.player.getX() + (int)(dir.x * 100.0F);
        int attackY = this.player.getY() + (int)(dir.y * 100.0F);
        bullets = this.toolItem.getAvailableAmmoNR(player, bullets);
        if (this.toolItem.canAttack(this.player.getLevel(), attackX, attackY, this.player, this.item) == null) {
            int seed = Item.getRandomAttackSeed(this.random.seeded((long)GameRandom.prime(this.attackSeed * this.shots)));
            Packet attackContent = new Packet();
            this.player.showAttack(this.item, attackX, attackY, seed, attackContent);
            this.toolItem.setupAttackContentPacket(new PacketWriter(attackContent), this.player.getLevel(), attackX, attackY, this.player, this.item);
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getLevel().getServer().network.sendToAllClientsExcept(new PacketShowAttack(this.player, this.item, attackX, attackY, seed, attackContent), client);
            }

            this.timeBuffer += (long)this.updateInterval;

            while(true) {
                float speedModifier = this.getSpeedModifier();
                if ((float)this.timeBuffer < (float)this.timeBetweenReloads * speedModifier) {
                    break;
                }

                seed = Item.getRandomAttackSeed(this.random.nextSeeded(GameRandom.prime(this.attackSeed * this.shots)));
                ++this.shots;
                --this.shotsRemaining;
                this.bullets--;
                this.toolItem.getAvailableAmmoNR(player,bullets);
                this.toolItem.superOnAttack(this.player.getLevel(), attackX, attackY, this.player, this.player.getCurrentAttackHeight(), this.item, this.slot, 0, seed, new PacketReader(attackContent));
                if (this.player.isClient()) {
                    Screen.playSound(GameResources.sniperrifle, SoundEffect.effect(this.player)
                            .volume(0.65f)
                            .pitch(GameRandom.globalRandom.getFloatBetween(0.7f, 0.8f)));
                }

                if (this.shotsRemaining <= 0) {
                    this.shotsRemaining = 6;
                    this.timeBuffer = 0L;
                    break;
                }
                int get = 1;

                this.timeBuffer = (long)((int)((float)(this.timeBetweenReloads - this.timeBetweenBurstShots) * speedModifier));
            }
        }
    }

    private float getSpeedModifier() {
        return 1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.player);
    }

    public void onEndAttack(boolean bySelf) {
        //reload quicker on partial bursts
        bullets = this.toolItem.getAvailableAmmoNR(player, bullets);
        if (bullets*-1 > 6) {
            while (bullets*-1 > 6) {
                bullets = bullets + 6;
            }
        }
        if (bullets*-1 != 6) {
            this.player.startItemCooldown(this.toolItem, (int) ((float) 530 * (bullets * -1) * this.getSpeedModifier()));
            this.player.stopAttack();
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getLevel().getServer().network.sendToAllClientsExcept(new PacketPlayerStopAttack(client.slot), client);
            }
        } else {
            //System.out.println(this.timeBuffer);
            this.player.startItemCooldown(this.toolItem, (int) ((float) (3160-timeBuffer) * this.getSpeedModifier()));
            this.player.stopAttack();
            if (this.player.isServer()) {
                ServerClient client = this.player.getServerClient();
                this.player.getLevel().getServer().network.sendToAllClientsExcept(new PacketPlayerStopAttack(client.slot), client);
            }
        }
    }
}
