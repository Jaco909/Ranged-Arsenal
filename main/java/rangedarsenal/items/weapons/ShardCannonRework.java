package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.ShardCannonAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.CrystalBulletProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;
import rangedarsenal.scripts.ShardCannonAttackHandlerFix;
import rangedarsenal.projectiles.bullets.SapphireSplosionBulletProjectile;
import rangedarsenal.projectiles.shells.GrenadeLauncherProjectile;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.LinkedList;

import static rangedarsenal.rangedarsenal.ShardCannonREDInvTex;
import static rangedarsenal.rangedarsenal.ShardCannonREDtex;

public class ShardCannonRework extends GunProjectileToolItem implements ItemInteractAction {
    public ShardCannonRework() {
        super(NORMAL_AMMO_TYPES, 500);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(160);
        this.ammoConsumeChance = 1.00F;
        this.attackDamage.setBaseValue(28.0F).setUpgradedValue(1.0F, 28.0F);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.resilienceGain.setBaseValue(0.3F);
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(300);
        this.attackRange.setBaseValue(2000);
        this.controlledRange = false;
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        if (player != null) {
            if (player.buffManager.hasBuff("ShardCannonCooldownDebuff")) {
                return new GameSprite(ShardCannonREDtex, this.attackTexture.getWidth(), this.attackTexture.getHeight());
            } else {
                return this.attackTexture != null ? new GameSprite(this.attackTexture) : new GameSprite(this.getItemSprite(item, player), 24);
            }
        } else {
            return new GameSprite(this.attackTexture);
        }
    }
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        if (perspective == null) {
            //prevent crash when drawing in stat menu
            return new GameSprite(this.itemTexture, 32);
        } else {
            if (perspective.buffManager.hasBuff("ShardCannonCooldownDebuff")) {
                return new GameSprite(ShardCannonREDInvTex, 32);
            } else {
                return new GameSprite(this.itemTexture, 32);
            }
        }
    }
    protected float getAmmoConsumeChance(ItemAttackerMob attackerMob, InventoryItem item) {
        float playerMod = attackerMob == null ? 1.0F : (Float)attackerMob.buffManager.getModifier(BuffModifiers.BULLET_USAGE);
        return (this.ammoConsumeChance-(this.getUpgradeTier(item)/10)) * playerMod;
    }
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "shardcannontipalt"));
        //doing this dynamically looks gross, I have to make another if pyramid
        //I'm also lazy
        if (this.getUpgradeTier(item) == 1.0f) {
            tooltips.add(Localization.translate("itemtooltip", "shardcannontipammo","ammouse",10));
        } else if (this.getUpgradeTier(item) == 2.0f) {
            tooltips.add(Localization.translate("itemtooltip", "shardcannontipammo","ammouse",20));
        } else if (this.getUpgradeTier(item) == 3.0f) {
            tooltips.add(Localization.translate("itemtooltip", "shardcannontipammo","ammouse",30));
        } else if (this.getUpgradeTier(item) == 4.0f) {
            tooltips.add(Localization.translate("itemtooltip", "shardcannontipammo","ammouse",40));
        } else if (this.getUpgradeTier(item) == 5.0f) {
            tooltips.add(Localization.translate("itemtooltip", "shardcannontipammo","ammouse",50));
        } else {
            tooltips.add(Localization.translate("itemtooltip", "shardcannontipammo","ammouse",0));
        }

        return tooltips;
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new ShardCannonAttackHandlerFix(attackerMob, slot, item, this, seed, x, y));
        return item;
    }

    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob.isPlayer;
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));

        if (!attackerMob.buffManager.hasBuff("ShardCannonCooldownDebuff")) {
            Projectile projectile;
            if (!attackerMob.isPlayer) {
                projectile = this.getProjectile(item, (BulletItem) ItemRegistry.getItem("crystalbullet"), attackerMob.x, attackerMob.y, (float)x, (float)y, this.getAttackRange(item), attackerMob);
            } else {
                projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, this.getAttackRange(item), attackerMob);
            }
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.dropItem = dropItem;
            projectile.getUniqueID(random);
            attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist, spreadRandom.getFloatOffset(0.0F, 3.0F));
        } else {
            for(int i = 0; i <= 4; ++i) {
                Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, (float)x, (float)y, this.getAttackRange(item), attackerMob);
                projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
                projectile.dropItem = dropItem;
                projectile.getUniqueID(random);
                attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist, spreadRandom.getFloatOffset(0.0F, (17.0F - this.getUpgradeTier(item))));
            }
        }
    }

    public void playFireSound(AttackAnimMob mob) {
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (attackerMob.buffManager.hasBuff("ShardCannonCooldownDebuff") && attackerMob.isClient()) {
            attackerMob.buffManager.removeBuff("ShardCannonCooldownDebuff",true);
            SoundManager.playSound(GameResources.crystalHit3, SoundEffect.effect(attackerMob).volume(1.6f).pitch(1f));
        } else if (!attackerMob.buffManager.hasBuff("ShardCannonCooldownDebuff") && attackerMob.isClient()) {
            ActiveBuff ab = new ActiveBuff("ShardCannonCooldownDebuff", attackerMob, 999999F, attackerMob);
            attackerMob.buffManager.addBuff(ab, true);
            SoundManager.playSound(GameResources.crystalHit3, SoundEffect.effect(attackerMob).volume(1.6f).pitch(0.5f));
        }
        return item;
    }

    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        return 0.01F;
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