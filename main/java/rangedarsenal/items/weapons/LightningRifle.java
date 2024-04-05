package rangedarsenal.items.weapons;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;
import rangedarsenal.events.LightningJumperEvent;
import rangedarsenal.events.LightningRifleEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class LightningRifle extends GunProjectileToolItem implements ItemInteractAction {
    public LightningRifle() {
        super(NORMAL_AMMO_TYPES, 1500);
        this.rarity = Rarity.EPIC;
        this.attackAnimTime.setBaseValue(127);
        this.attackDamage.setBaseValue(20.0F).setUpgradedValue(1.0F, 70.0F);
        this.attackXOffset = 10;
        this.attackYOffset = 12;
        this.attackRange.setBaseValue(1000);
        this.velocity.setBaseValue(550);
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    //force crit chance
    public GameDamage getFlatAttackDamage(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        if (gndData.hasKey("damage")) {
            GNDItem gndItem = gndData.getItem("damage");
            if (gndItem instanceof GNDItemGameDamage) {
                return ((GNDItemGameDamage)gndItem).damage;
            }

            if (gndItem instanceof GNDItem.GNDPrimitive) {
                float damage = ((GNDItem.GNDPrimitive)gndItem).getFloat();
                return new GameDamage(this.getDamageType(item), damage);
            }
        }
        if (this.getUpgradeTier(item) > 0) {
            return new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)),10+this.getUpgradeTier(item)*2,0.05f);
        } else {
            return new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)),10,0.05f);
        }
    }

    public void playFireSound(AttackAnimMob mob) {
        Screen.playSound(GameResources.jinglehit, SoundEffect.effect(mob).pitch(GameRandom.globalRandom.getFloatBetween(1.2F, 1.3F)));
    }

    protected void addExtraGunTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraGunTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "CryoBlasterSecondaryTip"), 400);
    }

    public boolean animDrawBehindHand() {
        return super.animDrawBehindHand();
    }

    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return true;
    }
    protected void fireProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, BulletItem bullet, boolean consumeAmmo, PacketReader contentReader) {
        x=x+GameRandom.globalRandom.getIntBetween(-2, 2);
        y=y+GameRandom.globalRandom.getIntBetween(-2, 2);

        LevelEvent event = new LightningRifleEvent(player, new GameDamage(30F,10F,0.05f), 0.2f, player.getX(), player.getY(), x, y, GameRandom.globalRandom.getIntBetween(-5, 5), player);
        player.getLevel().entityManager.addLevelEventHidden(event);
        if (level.isServer()) {
            level.getServer().network.sendToClientsWithEntityExcept(new PacketLevelEvent(event), event, player.getServerClient());
        }
    }
    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {
        ActiveBuff ab = new ActiveBuff("CryoBlasterCooldownDebuff", player, 7F - this.getUpgradeTier(item), player);
        player.buffManager.addBuff(ab, true);
        for(int i = 0; i <= 7; ++i) {
            player.getLevel().entityManager.addParticle(player.x, player.y, Particle.GType.IMPORTANT_COSMETIC).colorRandom(230f, 0.65f, 1f, 0f, 0.15f, 0f).height(16.0F).lifeTime(2000).movesFriction(GameRandom.globalRandom.getFloatBetween(0, 40), GameRandom.globalRandom.getFloatBetween(0, 40),GameRandom.globalRandom.getFloatBetween(0.27f, 0.83f));
            player.getLevel().entityManager.addParticle(player.x, player.y, Particle.GType.IMPORTANT_COSMETIC).colorRandom(230f, 0.65f, 1f, 0f, 0.15f, 0f).height(16.0F).lifeTime(2000).movesFriction(GameRandom.globalRandom.getFloatBetween(0, -40), GameRandom.globalRandom.getFloatBetween(0, -40),GameRandom.globalRandom.getFloatBetween(0.27f, 0.83f));
            player.getLevel().entityManager.addParticle(player.x, player.y, Particle.GType.IMPORTANT_COSMETIC).colorRandom(230f, 0.65f, 1f, 0f, 0.15f, 0f).height(16.0F).lifeTime(2000).movesFriction(GameRandom.globalRandom.getFloatBetween(0, 40), GameRandom.globalRandom.getFloatBetween(0, -40),GameRandom.globalRandom.getFloatBetween(0.27f, 0.83f));
            player.getLevel().entityManager.addParticle(player.x, player.y, Particle.GType.IMPORTANT_COSMETIC).colorRandom(230f, 0.65f, 1f, 0f, 0.15f, 0f).height(16.0F).lifeTime(2000).movesFriction(GameRandom.globalRandom.getFloatBetween(0, -40), GameRandom.globalRandom.getFloatBetween(0, 40),GameRandom.globalRandom.getFloatBetween(0.27f, 0.83f));
        }
        if (player.getLevel().entityManager.mobs.streamArea(player.getX(),player.getY(),2) != null) {

            player.getLevel().entityManager.mobs.streamArea(player.getX(),player.getY(), 2).forEach((m) -> {
                if (m != player) {
                    if (((m.x <= (player.x+(100+this.getUpgradeTier(item)*12))) && (m.x >= (player.x-(100+this.getUpgradeTier(item)*12)))) && ((m.y <= (player.y+(100+this.getUpgradeTier(item)*12))) && (m.y >= (player.y-(100+this.getUpgradeTier(item)*12))))) {
                        ActiveBuff mb = new ActiveBuff("CryoFreezeDebuff", player, 3F + (this.getUpgradeTier(item)/2), player);
                        m.buffManager.addBuff(mb, true);
                    }
                }
            });
        }
        Screen.playSound(GameResources.swoosh, SoundEffect.effect(player).volume(0.52F).pitch(3F));
        Screen.playSound(GameResources.shatter2, SoundEffect.effect(player).volume(0.6F).pitch(2F));
        Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(player).volume(0.85F).pitch(3F));

        return item;
    }

    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        return 0F;
    }

    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y) {
            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }

}
