package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.state.MainMenu;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import net.bytebuddy.asm.Advice;

import java.util.Iterator;

//Unload vanilla recipes on Main Menu load
@ModMethodPatch(target = MainMenu.class, name = "init", arguments = {})
public class RecipePatch {
    @Advice.OnMethodExit()
    static void onExit(@Advice.This MainMenu menu) {
        //if (menu.isRunning()) {

            Iterator recipes = Recipes.getRecipes().iterator();
            while (recipes.hasNext()) {
                Recipe recipe = (Recipe) recipes.next();
                if (recipe.resultStringID.equalsIgnoreCase("simplebullet") && recipe.resultAmount == 50) {
                    recipes.remove();
                }
                if (recipe.resultStringID.equalsIgnoreCase("frostbullet") && recipe.resultAmount == 50) {
                    recipes.remove();
                }
                if (recipe.resultStringID.equalsIgnoreCase("bouncingbullet") && recipe.ingredients.length != 4) {
                    recipes.remove();
                }
                if (recipe.resultStringID.equalsIgnoreCase("voidbullet") && recipe.ingredients.length == 2) {
                    recipes.remove();
                }
                if (recipe.resultStringID.equalsIgnoreCase("crystalbullet") && recipe.ingredients.length == 1) {
                    recipes.remove();
                }
                if (recipe.resultStringID.equalsIgnoreCase("sapphirerevolver") && recipe.tech.getStringID().equalsIgnoreCase("demonicanvil")) {
                    recipes.remove();
                }
                if (recipe.resultStringID.equalsIgnoreCase("shardcannon") && recipe.ingredients.length == 3) {
                    recipes.remove();
                }
                if (recipe.resultStringID.equalsIgnoreCase("cryoblaster") && recipe.ingredients.length == 1) {
                    recipes.remove();
                }
                if (recipe.resultStringID.equalsIgnoreCase("deathripper") && recipe.ingredients.length == 1) {
                    recipes.remove();
                }
                if (recipe.resultStringID.equalsIgnoreCase("antiquerifle") && recipe.ingredients.length == 1) {
                    recipes.remove();
                }
                if (recipe.resultStringID.equalsIgnoreCase("livingshotty") && recipe.ingredients.length == 1) {
                    recipes.remove();
                }
                if (recipe.resultStringID.toLowerCase().contains("bullet") && !recipe.resultStringID.equalsIgnoreCase("Bullet_Casing")) {
                    //moves mod bullets
                    recipe.setCraftingCategory("bullets");
                }
                if (recipe.resultStringID.equalsIgnoreCase("cannonball")) {
                    recipe.setCraftingCategory("shells");
                }
                if (recipe.resultStringID.equalsIgnoreCase("handgun") || recipe.resultStringID.equalsIgnoreCase("machinegun") || recipe.resultStringID.equalsIgnoreCase("sniperrifle") || recipe.resultStringID.equalsIgnoreCase("shotgun")) {
                    //moves mod recipes
                    if (recipe.getCraftingCategory() == null) {
                        recipe.setCraftingCategory("ballistic");
                        recipe.showAfter("Junk_Pistol");
                    }
                }
            }
        //}
    }
}
