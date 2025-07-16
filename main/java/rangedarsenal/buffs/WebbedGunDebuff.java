package rangedarsenal.buffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class WebbedGunDebuff extends Buff {
    public WebbedGunDebuff() {
        this.canCancel = false;
        this.isImportant = true;
        this.isVisible = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        //buff.setModifier(BuffModifiers.SLOW, 0.0F);
    }
    public int getStackSize() {
        return 100;
    }
}