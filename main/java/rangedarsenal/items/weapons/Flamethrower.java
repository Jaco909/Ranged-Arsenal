package rangedarsenal.items.weapons;

import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;

public class Flamethrower extends GunProjectileToolItem {
    public Flamethrower() {
        super(new String[]{"Gasoline", "CryoFlame", "Napalm", "MoltenSlime_Bullet"}, 1000);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(40).setUpgradedValue(1.0F, 35);
        this.attackDamage.setBaseValue(5.0F).setUpgradedValue(1.0F, 7).setUpgradedValue(2.0F, 8).setUpgradedValue(3.0F, 9).setUpgradedValue(4.0F, 10);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(230).setUpgradedValue(1.0F, 320);
        this.velocity.setBaseValue(68).setUpgradedValue(1.0F, 97);
        this.knockback.setBaseValue(0);
        this.keyWords.add("flamethrower");
        this.resilienceGain.setBaseValue(0.15F);
        this.ammoConsumeChance = 0.85F;
        this.controlledRange = false;
        this.controlledMinRange = 5;
    }
    public void playFireSound(AttackAnimMob mob) {
        Screen.playSound(GameResources.firespell1, SoundEffect.effect(mob)
                .volume(0.32f)
                .pitch(GameRandom.globalRandom.getFloatBetween(0.3f, 0.5f)));
    }
    public float getAttackMovementMod(InventoryItem item) {
        return 0.25F;
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        tooltips.add(Localization.translate("itemtooltip", "FlamethrowerTip"));
    }

    public boolean animDrawBehindHand() {
        return super.animDrawBehindHand();
    }
    protected void fireProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, BulletItem bullet, boolean consumeAmmo, PacketReader contentReader) {
        GameRandom random = new GameRandom((long)seed);
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        int range;
        if (bullet.idData.getStringID().equalsIgnoreCase("Napalm")) {
            this.controlledRange = true;
        } else {
            this.controlledRange = false;
        }
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

        projectile.setAngle(projectile.getAngle() + (spreadRandom.nextFloat() - 0.5F) * 6.5F);
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
        }
    }
}
