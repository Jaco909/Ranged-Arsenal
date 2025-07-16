package rangedarsenal.projectiles.modifiers;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CaveSpiderWebEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CrystallizeShatterEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.level.maps.LevelObjectHit;

public class WebbedModifier extends ProjectileModifier {
    private float resilienceGain;
    private boolean hasGained = false;

    public WebbedModifier() {
    }

    public WebbedModifier(float resilienceGain) {
        this.resilienceGain = resilienceGain;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.resilienceGain);
        writer.putNextBoolean(this.hasGained);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.resilienceGain = reader.getNextFloat();
        this.hasGained = reader.getNextBoolean();
    }

    public void initChildProjectile(Projectile projectile, float childStrength, int childCount) {
        super.initChildProjectile(projectile, childStrength, childCount);
        projectile.setModifier(new WebbedModifier(this.resilienceGain/(float)childCount));
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.projectile.isServer() && mob != null) {
            BuffManager attackerBM = this.projectile.getAttackOwner().buffManager;
            if (attackerBM != null) {
                float thresholdMod = (Float)attackerBM.getModifier(BuffModifiers.CRIT_CHANCE) + (Float)attackerBM.getModifier(BuffModifiers.RANGED_CRIT_CHANCE);
                float crystallizeMod = (Float)attackerBM.getModifier(BuffModifiers.CRIT_DAMAGE)-2 + (Float)attackerBM.getModifier(BuffModifiers.RANGED_CRIT_DAMAGE);
                System.out.println(thresholdMod);
                System.out.println(crystallizeMod);
                int stackThreshold = (int)GameMath.limit(5.0F - 5.0F * (thresholdMod + crystallizeMod), 2.0F, 5.0F);
                System.out.println(stackThreshold);
                Buff crystallizeBuff = BuffRegistry.getBuff("WebbedGunDebuff");
                ActiveBuff ab = new ActiveBuff(crystallizeBuff, mob, 10000, this.projectile.getAttackOwner());
                mob.buffManager.addBuff(ab, true);
                if (mob.buffManager.getBuff(crystallizeBuff).getStacks() >= stackThreshold) {
                    this.getLevel().entityManager.addLevelEvent(new CaveSpiderWebEvent(this.projectile.getOwner(),mob.getX(),mob.getY(), new GameRandom((long)this.projectile.getUniqueID())));
                    mob.buffManager.removeBuff(crystallizeBuff, true);
                    if (!mob.isBoss()) {
                        mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW, mob, 3.0F, this.projectile.getOwner()), true);
                    }
                }
            }
        }
        if (!this.hasGained) {
            Mob owner = this.projectile.getOwner();
            if (mob != null && owner != null && mob.canGiveResilience(owner)) {
                owner.addResilience(this.resilienceGain);
                this.hasGained = true;
            }
        }
    }
}