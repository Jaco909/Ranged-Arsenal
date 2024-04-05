package rangedarsenal.buffs;

import necesse.engine.localization.message.StaticMessage;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class HealDelayBuff extends Buff {
    public HealDelayBuff() {
        this.displayName = new StaticMessage("cooldown");
        this.canCancel = true;
        this.shouldSave = false;
        this.isImportant = true;
        this.isPassive = false;
        this.isVisible = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.SLOW, 0.0F);
    }
}
