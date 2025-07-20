package rangedarsenal.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.state.MainMenu;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

//Unload vanilla recipes on Main Menu load
@ModMethodPatch(target = MainMenu.class, name = "init", arguments = {})
public class RecipePatch {
    @Advice.OnMethodExit()
    static void onExit(@Advice.This MainMenu menu) throws NoSuchFieldException {
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
                if (recipe.resultStringID.equalsIgnoreCase("cannonball")) {
                    recipe.setCraftingCategory("shells");
                }

                //Mod items
                Class baseItem = recipe.resultItem.item.getClass().getSuperclass();
                if (baseItem.getName().equalsIgnoreCase("necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem")) {
                    if (recipe.getCraftingCategory() == null) {
                        recipe.setCraftingCategory("ballistic");
                    }
                } else if (baseItem.getName().equalsIgnoreCase("necesse.inventory.item.bulletItem.BulletItem") && !recipe.resultStringID.equalsIgnoreCase("Bullet_Casing") && !recipe.resultItem.item.getStringID().equalsIgnoreCase("flamerfuel")) {
                    if (recipe.getCraftingCategory() == null) {
                        recipe.setCraftingCategory("bullets");
                    }
                }
                if (recipe.resultStringID.contains("flamethrower") || recipe.resultStringID.equalsIgnoreCase("flamer")) {
                    recipe.setCraftingCategory("flame");
                }
            }
        //}
    }
}
