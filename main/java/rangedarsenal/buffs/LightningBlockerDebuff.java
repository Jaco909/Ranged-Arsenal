package rangedarsenal.buffs;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

import java.awt.*;

public class LightningBlockerDebuff extends Buff {
    public LightningBlockerDebuff() {
        this.canCancel = false;
        this.isImportant = true;
    }
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }
    public int getStackSize(ActiveBuff buff) {
        return 20;
    }
}
