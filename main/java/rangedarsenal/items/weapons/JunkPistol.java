package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.entity.projectile.Projectile;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.level.maps.Level;

import java.awt.*;

public class JunkPistol extends GunProjectileToolItem {
    public JunkPistol() {
        super(NORMAL_AMMO_TYPES, 200);
        this.rarity = Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(576);
        this.attackDamage.setBaseValue(19.0F).setUpgradedValue(1.0F, 37F);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(655);
        this.velocity.setBaseValue(234);
        this.knockback.setBaseValue(0).setUpgradedValue(1.0F, 25);
        this.resilienceGain.setBaseValue(0.0F).setUpgradedValue(1.0F, 0.01F).setUpgradedValue(5.0F, 0.1F);
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    protected void addExtraGunTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraGunTooltips(tooltips, item, perspective, blackboard);
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        super.addAmmoTooltips(tooltips, item);
    }

    public void playFireSound(AttackAnimMob mob) {
        SoundManager.playSound(GameResources.handgun, SoundEffect.effect(mob).volume(0.68f).pitch(GameRandom.globalRandom.getFloatBetween(0.5f, 0.78f)));
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
        projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * (10F - this.getUpgradeTier(item)*2));
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
        }
    }
}
