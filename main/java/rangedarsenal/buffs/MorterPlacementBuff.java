package rangedarsenal.buffs;

import necesse.engine.localization.message.StaticMessage;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class MorterPlacementBuff extends Buff {
    public MorterPlacementBuff() {
        this.displayName = new StaticMessage("morterplaced");
        this.canCancel = false;
        this.shouldSave = false;
        this.isImportant = true;
        this.isPassive = true;
        this.isVisible = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.RANGED_ATTACK_SPEED, 0.2F);
    }
}
