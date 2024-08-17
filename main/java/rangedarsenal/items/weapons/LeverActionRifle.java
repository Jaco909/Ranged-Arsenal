package rangedarsenal.items.weapons;

import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class LeverActionRifle extends GunProjectileToolItem implements ItemInteractAction {
    public LeverActionRifle() {
        super(NORMAL_AMMO_TYPES, 500);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(1200).setUpgradedValue(1.0F, 1100).setUpgradedValue(5.0F, 700);
        this.attackDamage.setBaseValue(34.0F).setUpgradedValue(1.0F, 120.0F);
        this.attackXOffset = 20;
        this.attackYOffset = 10;
        this.velocity.setBaseValue(700).setUpgradedValue(1.0F, 750);
        this.attackRange.setBaseValue(1000).setUpgradedValue(1.0F, 1100).setUpgradedValue(5.0F, 1500);
        this.knockback.setBaseValue(50);
        this.resilienceGain.setBaseValue(0.4f).setUpgradedValue(1.0F, 0.401f).setUpgradedValue(4.0F, 0.405f);
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
            return new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)),0,(0.14f + (this.getUpgradeTier(item)*0.04f)));
        } else {
            return new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)),0,0.15f);
        }
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(mob));
    }
    protected void addExtraGunTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraGunTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "Lever_Action_RifleSecondaryTip"), 400);
        //tooltips.add(Localization.translate("itemtooltip", "Lever_Action_RifleTip"));
        //tooltips.add(Localization.translate("itemtooltip", "Lever_Action_RifleTip2"));
    }

    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return !player.buffManager.hasBuff("LeverActionRifleCooldownDebuff");
    }

    protected void fireProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, BulletItem bullet, boolean consumeAmmo, PacketReader contentReader) {
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(spreadRandom, player, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)player.getDistance((float)x, (float)y);
        } else {
            range = this.getAttackRange(item);
        }
        Projectile projectile = this.getProjectile(item, bullet, player.x, player.y, (float)x, (float)y, range, player);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.piercing = projectile.piercing+1;
        projectile.dropItem = consumeAmmo;
        projectile.getUniqueID(random);
        level.entityManager.projectiles.addHidden(projectile);
        if (this.moveDist != 0) {
            projectile.moveDist((double)this.moveDist);
        }

        projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 1.0F);
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
        }
    }
    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {
        Item bulletcheck = player.getInv().main.getFirstItem(level, player, this.ammoItems(), "bulletammo");
        if (bulletcheck != null) {
            ActiveBuff ab = new ActiveBuff("LeverActionRifleCooldownDebuff", player, 5F, player);
            player.buffManager.addBuff(ab, true);

            Item bullet = player.getInv().main.getFirstItem(level, player, this.ammoItems(), "bulletammo");
            int range;
            range = this.getAttackRange(item);
            GameRandom random = new GameRandom((long) seed);
            GameRandom spreadRandom = new GameRandom((long) (seed + 10));
            Projectile projectile = this.getProjectile(item, (BulletItem) bullet, player.x, player.y, (float) x, (float) y, range, player);
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.piercing = 5;
            player.getInv().removeItems(player.getInv().main.getFirstItem(level, player, this.ammoItems(), "bulletammo"),1,true,true,true, true,"bulletammo");
            projectile.setDamage(new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)),0,1f));
            projectile.getUniqueID(random);
            level.entityManager.projectiles.addHidden(projectile);
            if (this.moveDist != 0) {
                projectile.moveDist((double) this.moveDist);
            }

            projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 1.0F);
            if (level.isServer()) {
                level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
            }
            SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(player).volume(1.1f).pitch(GameRandom.globalRandom.getFloatBetween(0.85f, 0.9f)));
            SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(player).volume(0.5f).pitch(GameRandom.globalRandom.getFloatBetween(0.4f, 0.45f)));
            SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(player).volume(0.25F).pitch(1F));
        }
        return item;
    }

    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        return perspective.buffManager.getBuffDurationLeftSeconds("LeverActionRifleCooldownDebuff") / 5.0F;
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
