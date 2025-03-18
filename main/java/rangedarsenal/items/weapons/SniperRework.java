package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.FloatTextFade;
import necesse.level.maps.hudManager.floatText.ItemPickupText;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class SniperRework extends GunProjectileToolItem implements ItemInteractAction {

    public SniperRework() {
        super(NORMAL_AMMO_TYPES, 1000);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(43.0F).setUpgradedValue(1.0F, 131F);
        this.attackXOffset = 20;
        this.attackYOffset = 10;
        this.attackCooldownTime.setBaseValue(850);
        this.velocity.setBaseValue(650);
        this.moveDist = 65;
        this.attackRange.setBaseValue(1600);
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    //cancel zoom on weapon switch
    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        if (perspective.getSelectedItem() == null) {
            perspective.buffManager.removeBuff("SniperZoomBuff",true);
        } else if (perspective.getSelectedItem() != null && !perspective.getSelectedItem().item.idData.getStringID().toLowerCase().equals("sniperrifle")) {
            perspective.buffManager.removeBuff("SniperZoomBuff",true);
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
        tooltips.add(Localization.translate("itemtooltip", "snipertip"));
        tooltips.add(Localization.translate("itemtooltip", "snipertipcrit"));
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.sniperrifle, SoundEffect.effect(mob));
    }
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob.isPlayer;
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (attackerMob.isPlayer) {
            if (attackerMob.buffManager.hasBuff("SniperZoomBuff") && attackerMob.isClient()) {
                attackerMob.buffManager.removeBuff("SniperZoomBuff", true);
            } else if (!attackerMob.buffManager.hasBuff("SniperZoomBuff") && attackerMob.isClient()) {
                ActiveBuff ab = new ActiveBuff("SniperZoomBuff", attackerMob, 0.01F, attackerMob);
                attackerMob.buffManager.addBuff(ab, true);
            }
            zoomAmount();
        }
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

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(spreadRandom, attackerMob, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)attackerMob.getDistance((float)x, (float)y);
        } else {
            range = this.getAttackRange(item);
        }
        Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, range, attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(random);
        level.entityManager.projectiles.addHidden(projectile);
        if (this.moveDist != 0) {
            projectile.moveDist((double)this.moveDist);
        }

        if (attackerMob.buffManager.hasBuff("SniperZoomBuff") || !attackerMob.isPlayer) {
            projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.01F) * 0F);
        } else {
            projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 8F);
        }
    }
}
