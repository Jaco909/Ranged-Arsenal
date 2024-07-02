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
        @Advice.OnMethodEnter(
                skipOn = Advice.OnNonDefaultValue.class
        )
        static boolean onEnter() {
            return true;
        }
        @Advice.OnMethodExit()
        static void onExit(@Advice.This Projectile thiss, @Advice.Argument(0) Mob mob, @Advice.Argument(1) float x, @Advice.Argument(2) float y) {

            //too many things are protected variables, bytebuddy can't change them
            //and to avoid entirely replacing weapons, just do this
            //I mean, I'm already poking around in here anyway
            if (thiss.getOwner() != null) {
                if (thiss.getOwner().isPlayer) {
                    PlayerMob player = ((PlayerMob) thiss.getOwner());
                    if (player.getSelectedItem() != null) {
                        if (player.getSelectedItem().item.idData.getStringID().equalsIgnoreCase("handgun") && player.getSelectedItem().item.getUpgradeTier(player.getSelectedItem()) == 0) {
                            thiss.knockback = 16;
                        }
                        if (player.getSelectedItem().item.idData.getStringID().equalsIgnoreCase("deathripper") && player.getSelectedItem().item.getUpgradeTier(player.getSelectedItem()) == 0) {
                            thiss.setDamage( new GameDamage(thiss.getDamage().damage+3,thiss.getDamage().armorPen,thiss.getDamage().baseCritChance));
                        }

                        if (player.getSelectedItem().item.idData.getStringID().equalsIgnoreCase("shardcannon")) {
                            Buff crystallizeBuff = BuffRegistry.Debuffs.CRYSTALLIZE_BUFF;
                            ActiveBuff ab = new ActiveBuff(crystallizeBuff, mob, 10000, thiss.getAttackOwner());
                            mob.buffManager.addBuff(ab, true);
                        }

                        //sniper crit via distance
                        //fix for multiplayer
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
            mob.isServerHit(thiss.getDamage(), mob.x - x * -thiss.dx * 50.0F, mob.y - y * -thiss.dy * 50.0F, (float) thiss.knockback, thiss);
        }
    }
}
