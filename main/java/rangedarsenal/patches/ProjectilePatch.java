package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.projectile.Projectile;
import net.bytebuddy.asm.Advice;

//This is a stupid way to do things, but hey it works
public class ProjectilePatch {
    public ProjectilePatch(){
    }
    @ModMethodPatch(target = Projectile.class, name = "applyDamage", arguments = {Mob.class, float.class, float.class})
    public static class DamagePatch {
        public DamagePatch(){
        }
        @Advice.OnMethodEnter()
        static void onEnter(@Advice.This Projectile thiss, @Advice.Argument(0) Mob mob, @Advice.Argument(1) float x, @Advice.Argument(2) float y) {
            if (thiss.getOwner() != null) {
                if (thiss.getOwner().isPlayer) {
                    PlayerMob player = ((PlayerMob) thiss.getOwner());
                    if (player.getSelectedItem() != null) {

                        //what is this
                        //why did I add this
                        /*if (player.getSelectedItem().item.idData.getStringID().equalsIgnoreCase("shardcannon")) {
                            Buff crystallizeBuff = BuffRegistry.Debuffs.CRYSTALLIZE_BUFF;
                            ActiveBuff ab = new ActiveBuff(crystallizeBuff, mob, 10000, thiss.getAttackOwner());
                            mob.buffManager.addBuff(ab, true);
                        }*/

                        if (thiss.getOwner().buffManager.hasBuff("SniperZoomBuff")) {
                            float critmod = (thiss.traveledDistance / 1500) / 1.5f;
                            if (critmod > 0.5) {
                                critmod = 0.5f;
                            }
                            thiss.setDamage(thiss.getDamage().addCritChance(critmod));
                        } else if (thiss.getOwner().buffManager.hasBuff("AWPZoomBuff")) {
                            float critmod = (thiss.traveledDistance / 1500) / 1.5f;
                            if (critmod > 0.75) {
                                critmod = 0.75f;
                            }
                            thiss.setDamage(thiss.getDamage().addCritChance(critmod));
                        }
                    }
                }
            }
        }
    }
}
