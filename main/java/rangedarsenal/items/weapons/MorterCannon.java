package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.*;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.*;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.CrimsonSkyArrowProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.TheCrimsonSkyProjectileToolItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import rangedarsenal.events.LightningRifleEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import static rangedarsenal.rangedarsenal.SHELL_AMMO_TYPES;

public class MorterCannon extends GunProjectileToolItem implements ItemInteractAction {
    public int projectileMaxHeight;
    public int specialAttackProjectileCount;
    public MorterCannon() {
        super(SHELL_AMMO_TYPES, 2000);
        this.rarity = Rarity.LEGENDARY;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(5F);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(330);
        this.velocity.setBaseValue(115);
        this.knockback.setBaseValue(0);
        this.resilienceGain.setBaseValue(0.1F);
        this.ammoConsumeChance = 0.8F;
    }
    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.explosionLight, SoundEffect.effect(mob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.15f, 1.15f)));
    }
    public GameMessage getSettlerCanUseError(HumanMob mob, InventoryItem item) {
        return new LocalMessage("ui", "settlercantuseitem");
    }

    public CrimsonSkyArrowProjectile getTheCrimsonSkyProjectile(Level level, int x, int y, Mob owner, GameDamage damage, float velocity, int knockback, float resilienceGain) {
        Point2D.Float targetPoints = new Point2D.Float((float)x, (float)y);
        Point2D.Float normalizedVector = GameMath.normalize(targetPoints.x - owner.x, targetPoints.y - owner.y);
        RayLinkedList<LevelObjectHit> hits = GameUtils.castRay(level, (double)owner.x, (double)owner.y, (double)normalizedVector.x, (double)normalizedVector.y, targetPoints.distance((double)owner.x, (double)owner.y), 0, (new CollisionFilter()).projectileCollision().addFilter((tp) -> {
            return tp.object().object.isWall || tp.object().object.isRock;
        }));
        if (!hits.isEmpty()) {
            Ray<LevelObjectHit> first = (Ray)hits.getLast();
            targetPoints.x = (float)first.x2;
            targetPoints.y = (float)first.y2;
        }

        return new CrimsonSkyArrowProjectile(level, owner, owner.x, owner.y, owner.x, owner.y - 1.0F, velocity, this.projectileMaxHeight, damage, resilienceGain, knockback, targetPoints, false);
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        super.addAmmoTooltips(tooltips, item);
        tooltips.add(Localization.translate("itemtooltip", "thecrimsonskytip"), 400);
        tooltips.add(Localization.translate("itemtooltip", "thecrimsonskysecondarytip"), 400);
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.rotation(-85.0F);
    }



    public boolean canLevelInteract(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        return true;
    }

    public float getItemCooldownPercent(InventoryItem item, PlayerMob perspective) {
        return 0.01F;
    }
    public Projectile getProjectile(Level level, int x, int y, Mob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, PacketReader contentReader) {
        return this.getTheCrimsonSkyProjectile(level, x, y, owner, damage, velocity, knockback, resilienceGain);
    }
    protected void fireProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, BulletItem bullet, boolean consumeAmmo, PacketReader contentReader) {
        final GameRandom random = new GameRandom((long)seed);
        Point2D.Float targetPoints = new Point2D.Float((float)x, (float)y);
        int rndX = random.getIntBetween(-75, 75);
        int rndY = random.getIntBetween(-75, 75);
        targetPoints.x += (float)rndX;
        targetPoints.y += (float)rndY;
        RayLinkedList<LevelObjectHit> hits = GameUtils.castRay(level, (double)player.x, (double)player.y, (double)(targetPoints.x - player.x), (double)(targetPoints.y - player.y), targetPoints.distance((double)player.x, (double)player.y), 0, (new CollisionFilter()).projectileCollision().addFilter((tp) -> {
            return tp.object().object.isWall || tp.object().object.isRock;
        }));
        if (!hits.isEmpty()) {
            Ray<LevelObjectHit> first = (Ray)hits.getLast();
            targetPoints.x = (float)first.x2;
            targetPoints.y = (float)first.y2;
        }

        GameDamage specialAttackDmg = this.getAttackDamage(item).modFinalMultiplier(1.25F);
        Projectile projectile = new CrimsonSkyArrowProjectile(level, player, player.x, player.y, player.x, player.y - 1.0F, (float)this.getProjectileVelocity(item, player), this.projectileMaxHeight, specialAttackDmg, this.getResilienceGain(item), this.getKnockback(item, player), targetPoints, false);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = consumeAmmo;
        projectile.getUniqueID(new GameRandom((long)seed));
        level.entityManager.projectiles.addHidden(projectile);
        if (this.moveDist != 0) {
            projectile.moveDist((double)this.moveDist);
        }
        if (level.isServer()) {
            level.getServer().network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, player.getServerClient());
        }
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int seed, PacketReader contentReader) {
        if (player.buffManager.hasBuff("MorterPlacementBuff")) {
            player.buffManager.removeBuff("MorterPlacementBuff",true);
        } else {
            ActiveBuff ab = new ActiveBuff("MorterPlacementBuff", player, 0.01F, player);
            player.buffManager.addBuff(ab, true);
        }
        return item;
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

    public Point getControllerAttackLevelPos(Level level, float aimDirX, float aimDirY, PlayerMob player, InventoryItem item) {
        float range = 500.0F;
        return new Point((int)(player.x + aimDirX * range), (int)(player.y + aimDirY * range));
    }
}