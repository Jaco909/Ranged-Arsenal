package rangedarsenal.buffs;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.OnFireBuff;
import necesse.entity.particle.Particle;

import java.awt.*;

public class FrostyDebuff extends Buff {
    public FrostyDebuff() {
        this.canCancel = false;
        this.isImportant = true;
    }
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.FROST_DAMAGE_FLAT, 8.0F);
    }
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0F, owner.dy / 10.0F).color(new Color(23, 114, 193)).givesLight(200.0F, 0.5F).height(16.0F);
        }

    }
}