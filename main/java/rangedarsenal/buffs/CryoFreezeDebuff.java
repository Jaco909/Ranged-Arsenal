package rangedarsenal.buffs;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

import java.awt.*;

public class CryoFreezeDebuff extends Buff {
    private boolean particleShown = false;
    public CryoFreezeDebuff() {
        this.canCancel = false;
        this.isImportant = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.SLOW, 1F);
        buff.setModifier(BuffModifiers.FRICTION, 5F);
        buff.setModifier(BuffModifiers.ARMOR, -0.5F);
        buff.setModifier(BuffModifiers.ARMOR_FLAT, -10);
    }
    public void firstAdd(ActiveBuff buff) {
        Mob owner = buff.owner;
        if (owner.buffManager.hasBuff("CryoFreezeDebuff")) {
            for (int i = 0; i <= 14; ++i) {
                owner.getLevel().entityManager.addParticle(GameRandom.globalRandom.getFloatBetween(owner.x - (owner.getCollision().width*1.15f), (owner.getCollision().width*1.15f) + owner.x), GameRandom.globalRandom.getFloatBetween(owner.y - (owner.getCollision().height*1.15f), (owner.getCollision().height*1.15f) + owner.y), Particle.GType.IMPORTANT_COSMETIC).color(new Color(130, 215, 255, 26)).height(16.0F).lifeTime(buff.getDuration()+200).sizeFades(10,16).fadesAlphaTime(1,1);
            }
        }
    }

    /*public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
            //System.out.println(owner.getArmorFlat());
            //owner.ai.tree.interruptRunning();
            //owner.ai.blackboard.mover.stopMoving(owner);
            //owner.startAttackCooldown();
            //owner.stopMoving();
            //owner.ai.blackboard.mover.setMobTarget(owner.ai.blackboard.mover.getMovingFor(),owner,false);
        }
    }*/
}
