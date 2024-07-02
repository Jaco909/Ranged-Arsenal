package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;
import rangedarsenal.scripts.ShardCannonAttackHandlerFix;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
//I'M REALLY DUCK SO DURCKK JAKA THOUGHT THIS WAS EASIER THAN SWITCHING TEXTURES
//OH WELLLLLLLLLL

public class ShardCannonRED extends GunProjectileToolItem implements ItemInteractAction {
    public ShardCannonRED() {
        super(NORMAL_AMMO_TYPES, 500);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(160);
        this.ammoConsumeChance = 0.5F;
        if (ItemRegistry.itemExists("novafragment")) {
            this.attackDamage.setBaseValue(37.0F).setUpgradedValue(1.0F, 37.0F);
        } else {
            this.attackDamage.setBaseValue(37.0F).setUpgradedValue(1.0F, 37.0F);
        }
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.resilienceGain.setBaseValue(0.3F);
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(300);
        this.attackRange.setBaseValue(2000);
        this.controlledRange = false;
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }
}