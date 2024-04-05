package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.level.maps.Level;

public class ShotgunRework extends GunProjectileToolItem implements ItemInteractAction {
    public ShotgunRework() {
        super(NORMAL_AMMO_TYPES, 700);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(1200);
        this.attackDamage.setBaseValue(14.0F).setUpgradedValue(1.0F, 33.0F);
        this.attackXOffset = 12;
        this.attackYOffset = 10;
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(350);
        this.moveDist = 20;
        this.knockback.setBaseValue(9).setUpgradedValue(1.0F, 20);
        this.resilienceGain.setBaseValue(0.2F);
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    protected void addExtraGunTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraGunTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "Shotguntier","pellets",Math.round(this.getUpgradeTier(item) + 3)));
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

        for(int i = 0; i <= (3 + this.getUpgradeTier(item)); ++i) {
            Projectile projectile = this.getProjectile(item, bullet, player.x, player.y, (float)x, (float)y, range, player);
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.dropItem = consumeAmmo;
            projectile.getUniqueID(random);
            level.entityManager.projectiles.addHidden(projectile);
            if (this.moveDist != 0) {
                projectile.moveDist((double)this.moveDist);
            }

            projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * (20.0F - this.getUpgradeTier(item)));
            if (level.isServer()) {
                level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
            }
       }

    }

    protected void fireSettlerProjectiles(Level level, HumanMob mob, Mob target, InventoryItem item, int seed, BulletItem bullet, boolean consumeAmmo) {
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        int velocity = this.getVelocity(item, mob);
        Point2D.Float targetPos = Projectile.getPredictedTargetPos(target, mob.x, mob.y, (float)velocity, -10.0F);
        int x = (int)targetPos.x;
        int y = (int)targetPos.y;
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(spreadRandom, mob, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)mob.getDistance((float)x, (float)y);
        } else {
            range = this.getAttackRange(item);
        }

        for(int i = 0; i <= (3 + this.getUpgradeTier(item)); ++i) {
            Projectile projectile = this.getProjectile(item, bullet, mob.x, mob.y, (float)x, (float)y, range, mob);
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.dropItem = consumeAmmo;
            projectile.getUniqueID(random);
            level.entityManager.projectiles.addHidden(projectile);
            if (this.moveDist != 0) {
                projectile.moveDist((double)this.moveDist);
            }

            projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * (20.0F - this.getUpgradeTier(item)));
            if (level.isServer()) {
                level.getServer().network.sendToClientsAt(new PacketSpawnProjectile(projectile), level);
            }
        }

    }

    public void playFireSound(AttackAnimMob mob) {
        Screen.playSound(GameResources.shotgun, SoundEffect.effect(mob));
    }
}
