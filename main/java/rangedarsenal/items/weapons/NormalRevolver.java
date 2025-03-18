package rangedarsenal.items.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.ShardCannonAttackHandler;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.level.maps.Level;
import rangedarsenal.scripts.BurstRevolverAttackHandler;

public class NormalRevolver extends GunProjectileToolItem {
    public NormalRevolver() {
        super(NORMAL_AMMO_TYPES, 1000);
        this.rarity = Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(180).setUpgradedValue(1.0F, 172).setUpgradedValue(2.0F, 164).setUpgradedValue(3.0F, 156).setUpgradedValue(4.0F, 148);
        this.attackDamage.setBaseValue(42.0F).setUpgradedValue(1.0F, 55.0F).setUpgradedValue(2.0F, 60.0F).setUpgradedValue(3.0F, 65.0F).setUpgradedValue(4.0F, 70.0F);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(1000);
        this.knockback.setBaseValue(70);
        this.resilienceGain.setBaseValue(0.2f);
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    protected void addExtraGunTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraGunTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "RevolverTip"));
        tooltips.add(Localization.translate("itemtooltip", "RevolverTip2"));
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        super.addAmmoTooltips(tooltips, item);
    }

   public int getAvailableAmmoNR(ItemAttackerMob attackerMob, int bullets) {
       return bullets;
   }

    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new BurstRevolverAttackHandler(attackerMob, slot, item, this, seed, x, y));
        return item;
    }
}
