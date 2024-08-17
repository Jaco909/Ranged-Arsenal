package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.ZombieMob;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.SurfaceGrassObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;

@ModMethodPatch(target = SurfaceGrassObject.class, name = "getLootTable", arguments = {Level.class, int.class, int.class})
public class GrassDropsPatch {
    @Advice.OnMethodEnter(
            skipOn = Advice.OnNonDefaultValue.class
    )
    static boolean onEnter(@Advice.This SurfaceGrassObject grassObject, @Advice.Argument(0) Level level) {
        return true;
    }
    @Advice.OnMethodExit()
    static void onExit(@Advice.This SurfaceGrassObject grassObject, @Advice.Argument(0) Level level, @Advice.Argument(1) int x, @Advice.Argument(2) int y, @Advice.Return(readOnly = false) LootTable lootTable){
        float baitChance = 35.0F;
        if (level.weatherLayer.isRaining()) {
            baitChance = 15.0F;
        }
        lootTable = new LootTable(new LootItemInterface[]{new ChanceLootItem(1.0F / baitChance, "wormbait"), new ChanceLootItem(0.077f, "grassseed")});
    }
    static void onExit(@Advice.This SurfaceGrassObject grassObject, @Advice.Argument(0) Level level, @Advice.Return(readOnly = false) LootTable lootTable) {

    }
}
