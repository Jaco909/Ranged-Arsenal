package rangedarsenal.buffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class CryoBuildupDebuff extends Buff {
    public CryoBuildupDebuff() {
        this.canCancel = false;
        this.isImportant = true;
        this.isVisible = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.FROST_DAMAGE, 0.0F);
    }
}
