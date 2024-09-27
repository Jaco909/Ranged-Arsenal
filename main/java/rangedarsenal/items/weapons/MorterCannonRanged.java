package rangedarsenal.items.weapons;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;

public class MorterCannonRanged extends GunProjectileToolItem {
    public MorterCannonRanged() {
        super(new String[]{"Grenade_Launcher_Shell","Grenade_Launcher_Mine_Shell","Grenade_Launcher_Proxy_Shell"}, 2000);
        this.rarity = Rarity.LEGENDARY;
        this.attackAnimTime.setBaseValue(20);
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
    public float getAttackMovementMod(InventoryItem item) {
        return 0.20F;
    }
}