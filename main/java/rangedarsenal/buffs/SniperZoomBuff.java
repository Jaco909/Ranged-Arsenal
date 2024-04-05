package rangedarsenal.buffs;

import necesse.engine.localization.message.StaticMessage;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class SniperZoomBuff extends Buff {
    public SniperZoomBuff() {
        this.displayName = new StaticMessage("zoom");
        this.canCancel = true;
        this.shouldSave = false;
        this.isImportant = true;
        this.isPassive = true;
        this.isVisible = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.RANGED_ATTACK_SPEED, -0.12F);
        //buff.setModifier(BuffModifiers.RANGED_CRIT_CHANCE, 0.1F);
    }
}
