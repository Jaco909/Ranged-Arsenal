package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemGameDamage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class AWP extends GunProjectileToolItem implements ItemInteractAction {

    public AWP() {
        super(NORMAL_AMMO_TYPES, 3000);
        this.rarity = Rarity.LEGENDARY;
        this.attackAnimTime.setBaseValue(1358).setUpgradedValue(1.0F, 1328).setUpgradedValue(2.0F, 1268).setUpgradedValue(3.0F, 1200).setUpgradedValue(4.0F, 1100).setUpgradedValue(4.0F, 989);
        this.attackDamage.setBaseValue(224F).setUpgradedValue(1.0F, 260F).setUpgradedValue(2.0F, 280F).setUpgradedValue(3.0F, 300F).setUpgradedValue(4.0F, 320F);
        this.attackXOffset = 20;
        this.attackYOffset = 10;
        this.attackCooldownTime.setBaseValue(350);
        this.velocity.setBaseValue(1137);
        this.moveDist = 65;
        this.attackRange.setBaseValue(2000);
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    //force armor pen
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

        return new GameDamage(this.getDamageType(item), this.attackDamage.getValue(this.getUpgradeTier(item)),20,0f);
    }

    //cancel zoom on weapon switch
    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        if (perspective.getSelectedItem() == null) {
            perspective.buffManager.removeBuff("AWPZoomBuff",true);
        } else if (perspective.getSelectedItem() != null && !perspective.getSelectedItem().item.idData.getStringID().toLowerCase().equals("awp")) {
            perspective.buffManager.removeBuff("AWPZoomBuff",true);
        }
        super.draw(item, perspective, x, y, inInventory);
        if (inInventory) {
            int ammoAmount = this.getAvailableAmmo(perspective);
            if (ammoAmount > 999) {
                ammoAmount = 999;
            }
            String amountString = String.valueOf(ammoAmount);
            int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
            FontManager.bit.drawString((float)(x + 28 - width), (float)(y + 16), amountString, tipFontOptions);
        }
    }
    protected void addExtraGunTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraGunTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "awptip"));
        tooltips.add(Localization.translate("itemtooltip", "snipertipcrit"));
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(mob).pitch(0.8f));
        SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(mob).volume(0.5f).pitch(0.8f));
    }
    float zoom = 0f;
    public float zoomAmount() {
        return zoom;
    }
    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return true;
    }
    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {
        if (player.buffManager.hasBuff("AWPZoomBuff")) {
            player.buffManager.removeBuff("AWPZoomBuff",true);
        } else {
            ActiveBuff ab = new ActiveBuff("AWPZoomBuff", player, 0.01F, player);
            player.buffManager.addBuff(ab, true);
        }
        zoomAmount();
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
        projectile.dropItem = consumeAmmo;
        projectile.getUniqueID(random);
        level.entityManager.projectiles.addHidden(projectile);
        if (this.moveDist != 0) {
            projectile.moveDist((double)this.moveDist);
        }

        if (player.buffManager.hasBuff("AWPZoomBuff")) {
            projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.01F) * 0F);
        } else {
            projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 16.5F);
        }
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
        }
    }
}
