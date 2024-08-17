package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.state.MainMenu;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.bosses.PestWardenHead;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import net.bytebuddy.asm.Advice;

import java.util.ArrayList;
import java.util.Iterator;

//Unload vanilla recipes on Main Menu load
@ModMethodPatch(target = MainMenu.class, name = "init", arguments = {})
public class RecipePatch {
    @Advice.OnMethodExit()
    static void onExit() {

        Iterator recipes = Recipes.getRecipes().iterator();
        while(recipes.hasNext()) {
            Recipe recipe = (Recipe)recipes.next();
            if (recipe.resultStringID.equalsIgnoreCase("simplebullet") && recipe.tech.getStringID().equalsIgnoreCase("none")) {
                recipes.remove();
            }
            if (recipe.resultStringID.equalsIgnoreCase("frostbullet") && recipe.tech.getStringID().equalsIgnoreCase("none")) {
                recipes.remove();
            }
            if (recipe.resultStringID.equalsIgnoreCase("bouncingbullet") && recipe.tech.getStringID().equalsIgnoreCase("none")) {
                recipes.remove();
            }
            if (recipe.resultStringID.equalsIgnoreCase("voidbullet") && recipe.tech.getStringID().equalsIgnoreCase("none")) {
                recipes.remove();
            }
            if (recipe.resultStringID.equalsIgnoreCase("crystalbullet") && recipe.tech.getStringID().equalsIgnoreCase("fallen")) {
                recipes.remove();
            }
            if (recipe.resultStringID.equalsIgnoreCase("sapphirerevolver") && recipe.tech.getStringID().equalsIgnoreCase("demonic")) {
                recipes.remove();
            }
        }
    }
}
