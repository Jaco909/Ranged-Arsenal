package rangedarsenal.patches;

import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.loading.ServerLoader;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import net.bytebuddy.asm.Advice;

import java.util.Arrays;
import java.util.Iterator;

//Unload vanilla recipes on Main Menu load
@ModMethodPatch(target = Server.class, name = "addClient", arguments = {NetworkInfo.class, long.class, String.class, boolean.class})
public class RecipePatchDedicatedServer {
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
