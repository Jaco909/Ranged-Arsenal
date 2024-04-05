package rangedarsenal.items.weapons;

import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
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
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;

import java.awt.*;

public class GrenadeLauncher extends GunProjectileToolItem {
    public GrenadeLauncher() {
        super(new String[]{"Grenade_Launcher_Shell","Grenade_Launcher_Mine_Shell","Grenade_Launcher_Proxy_Shell"}, 2000);
        this.rarity = Rarity.RARE;
        this.attackAnimTime.setBaseValue(1500).setUpgradedValue(1.0F, 1400).setUpgradedValue(2.0F, 1300).setUpgradedValue(3.0F, 1200).setUpgradedValue(4.0F, 1000);
        this.attackDamage.setBaseValue(100.0F).setUpgradedValue(1.0F, 150.0F).setUpgradedValue(4.0F, 175.0F);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(350);
        this.velocity.setBaseValue(110).setUpgradedValue(1.0F, 120).setUpgradedValue(2.0F, 130).setUpgradedValue(3.0F, 140).setUpgradedValue(4.0F, 150);
        this.knockback.setBaseValue(0);
        this.controlledRange = false;
    }
    public void playFireSound(AttackAnimMob mob) {
        Screen.playSound(GameResources.explosionLight, SoundEffect.effect(mob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.15f, 1.15f)));
    }
    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        tooltips.add(Localization.translate("itemtooltip", "GrenadeLauncherTip"));
        tooltips.add(Localization.translate("itemtooltip", "GrenadeLauncherTip2"));
    }
    public GameMessage getSettlerCanUseError(HumanMob mob, InventoryItem item) {
        return new LocalMessage("ui", "settlercantuseitem");
    }
    public boolean animDrawBehindHand() {
        return super.animDrawBehindHand();
    }

    public float getAttackMovementMod(InventoryItem item) {
        //makes proxy easier to use
        return 0.60F;
    }
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slot, int animAttack, int seed, PacketReader contentReader) {
        int bulletID = contentReader.getNextShortUnsigned();
        if (bulletID != 65535) {
            Item bullet = ItemRegistry.getItem(bulletID);
            if (bullet != null) {
                GameRandom random = new GameRandom((long)(seed + 5));
                float ammoConsumeChance = ((BulletItem)bullet).getAmmoConsumeChance() * this.getAmmoConsumeChance(player, item);
                boolean consumeAmmo = ammoConsumeChance >= 1.0F || ammoConsumeChance > 0.0F && random.getChance(ammoConsumeChance);
                if (!consumeAmmo || player.getInv().main.removeItems(level, player, bullet, 1, "bulletammo") >= 1) {
                    //player.getInv().removeItems(bullet,1,true,true,true,"bulletammo");
                    if (bullet.idData.getStringID().equalsIgnoreCase("Grenade_Launcher_Proxy_Shell")) {
                        this.controlledRange = true;
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    } else {
                        this.controlledRange = false;
                        this.fireProjectiles(level, x, y, player, item, seed, (BulletItem)bullet, consumeAmmo, contentReader);
                    }
                }
            } else {
                GameLog.warn.println(player.getDisplayName() + " tried to use item " + (bullet == null ? bulletID : bullet.getStringID()) + " as bullet.");
            }
        }
        return item;
    }
    public Projectile getProjectile(InventoryItem item, BulletItem bulletItem, float x, float y, float targetX, float targetY, int range, Mob owner) {
        float velocity = bulletItem.modVelocity((float)this.getProjectileVelocity(item, owner));
        range = bulletItem.modRange(range);
        GameDamage damage;
        if (bulletItem.idData.getStringID().equalsIgnoreCase("GrenadeLauncherProxyShell")) {
            damage = (new GameDamage(this.attackDamage.getValue(this.getUpgradeTier(item))/4,bulletItem.armorPen,bulletItem.critChance));
        } else {
            damage = (new GameDamage(bulletItem.damage+this.attackDamage.getValue(this.getUpgradeTier(item)),bulletItem.armorPen,bulletItem.critChance+this.getCritChance(item,owner)));
        }
        int knockback = bulletItem.modKnockback(this.getKnockback(item, owner));
        return bulletItem.overrideProjectile() ? bulletItem.getProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner) : this.getNormalProjectile(x, y, targetX, targetY, velocity, range, damage, knockback, owner);
    }
    protected void fireProjectiles(Level level, int x, int y, PlayerMob player, InventoryItem item, int seed, BulletItem bullet, boolean consumeAmmo, PacketReader contentReader) {
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(new GameRandom((long)(seed + 10)), player, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)player.getDistance((float)x, (float)y);
        } else {
            range = this.getAttackRange(item);
        }

        Projectile projectile = this.getProjectile(item, bullet, player.x, player.y, (float)x, (float)y, range, player);
        projectile.setDamage(new GameDamage(projectile.getDamage().damage+this.attackDamage.getValue(this.getUpgradeTier(item)),projectile.getDamage().armorPen,projectile.getDamage().baseCritChance));
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = consumeAmmo;
        projectile.getUniqueID(new GameRandom((long)seed));
        level.entityManager.projectiles.addHidden(projectile);
        if (this.moveDist != 0) {
            projectile.moveDist((double)this.moveDist);
        }
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(projectile), player.getServerClient(), player.getServerClient());
        }

    }
}
