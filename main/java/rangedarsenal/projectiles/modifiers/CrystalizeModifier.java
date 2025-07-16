package rangedarsenal.projectiles.modifiers;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
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

public class CrystalizeModifier extends ProjectileModifier {
    private float resilienceGain;
    private boolean hasGained = false;

    public CrystalizeModifier() {
    }

    public CrystalizeModifier(float resilienceGain) {
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
        projectile.setModifier(new CrystalizeModifier(this.resilienceGain/(float)childCount));
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.projectile.isServer() && mob != null) {
            BuffManager attackerBM = this.projectile.getAttackOwner().buffManager;
            if (attackerBM != null) {
                float thresholdMod = (Float)attackerBM.getModifier(BuffModifiers.CRIT_CHANCE) + (Float)attackerBM.getModifier(BuffModifiers.RANGED_CRIT_CHANCE);
                float crystallizeMod = (Float)attackerBM.getModifier(BuffModifiers.CRIT_DAMAGE) + (Float)attackerBM.getModifier(BuffModifiers.RANGED_CRIT_CHANCE);
                int stackThreshold = (int)GameMath.limit(10.0F - 7.0F * thresholdMod, 3.0F, 10.0F);
                float crystallizeDamageMultiplier = GameMath.limit(crystallizeMod, 2.0F, (float)stackThreshold);
                Buff crystallizeBuff = BuffRegistry.Debuffs.CRYSTALLIZE_BUFF;
                ActiveBuff ab = new ActiveBuff(crystallizeBuff, mob, 10000, this.projectile.getAttackOwner());
                mob.buffManager.addBuff(ab, true);
                if (mob.buffManager.getBuff(crystallizeBuff).getStacks() >= stackThreshold) {
                    this.getLevel().entityManager.addLevelEvent(new CrystallizeShatterEvent(mob, CrystallizeShatterEvent.ParticleType.SAPPHIRE));
                    mob.buffManager.removeBuff(crystallizeBuff, true);
                    GameDamage finalDamage = this.projectile.getDamage().modDamage(crystallizeDamageMultiplier);
                    mob.isServerHit(finalDamage, 0.0F, 0.0F, 0.0F, this.projectile);
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