package rangedarsenal.patches;

import java.util.ArrayList;
import java.util.Iterator;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.world.WorldEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.GunsmithHumanMob;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;

@ModMethodPatch(
        target = GunsmithHumanMob.class,
        name = "getShopItems",
        arguments = {VillageShopsData.class, ServerClient.class}
)
public class GunsmithItems {
    public GunsmithItems() {
    }
    @Advice.OnMethodExit()
    static void onExit(@Advice.This GunsmithHumanMob gunsmith, @Argument(0) VillageShopsData data, @Argument(1) ServerClient client, @Advice.Return(readOnly = false) ArrayList<ShopItem> list) {

        //remove vanilla weps
        if (list == null) {
            //stops crashes from wandering gunsmiths
        } else {
            int listcount = 0;
            while (listcount < list.size()) {
                if (list.get(listcount) != null) {
                    if (list.get(listcount).item.item.idData.getStringID().equalsIgnoreCase("handgun") || list.get(listcount).item.item.idData.getStringID().equalsIgnoreCase("shotgun") || list.get(listcount).item.item.idData.getStringID().equalsIgnoreCase("machinegun") || list.get(listcount).item.item.idData.getStringID().equalsIgnoreCase("sniperrifle") || list.get(listcount).item.item.idData.getStringID().equalsIgnoreCase("deathripper")) {
                        list.remove(listcount);
                    } else {
                        listcount++;
                    }
                } else {
                    listcount++;
                }
            }


            //add new items
            GameRandom random = new GameRandom(gunsmith.getShopSeed() + 5L);


        /*if (client.characterStats().items_obtained.isItemObtained("simplebullet") ) {
            list.add(ShopItem.item(new InventoryItem("simplebullet", 100), gunsmith.getRandomHappinessPrice(random, 40, 80, 10)));
        }
        if (client.characterStats().items_obtained.isItemObtained("bouncingbullet") ) {
            list.add(ShopItem.item(new InventoryItem("bouncingbullet", 100), gunsmith.getRandomHappinessPrice(random, 40, 80, 10)));
        }
        if (client.characterStats().items_obtained.isItemObtained("Standard_Bullet") ) {
            list.add(ShopItem.item(new InventoryItem("Standard_Bullet", 100), gunsmith.getRandomHappinessPrice(random, 40, 80, 10)));
        }
        if (client.characterStats().items_obtained.isItemObtained("Frozen_Bullet") ) {
            list.add(ShopItem.item(new InventoryItem("Frozen_Bullet", 100), gunsmith.getRandomHappinessPrice(random, 40, 80, 10)));
        }*/

            if (client.characterStats().mob_kills.getKills("evilsprotector") > 0 && client.characterStats().mob_kills.getKills("queenspider") > 0) {
                list.add(ShopItem.item(new InventoryItem("ammopouch", 1), gunsmith.getRandomHappinessPrice(random, 400, 800, 100)));
            }
            if (client.characterStats().mob_kills.getKills("piratecaptain") > 0) {
                list.add(ShopItem.item(new InventoryItem("Gunpowder", 1), gunsmith.getRandomHappinessPrice(random, 40, 80, 10)));
                list.add(ShopItem.item(new InventoryItem("Mechanical_Parts", 1), gunsmith.getRandomHappinessPrice(random, 100, 300, 50)));
            }
            if (client.characterStats().mob_kills.getKills("fallenwizard") > 0) {
                list.add(ShopItem.item(new InventoryItem("Mechanical_Parts_Good", 1), gunsmith.getRandomHappinessPrice(random, 200, 500, 50)));
                list.add(ShopItem.item(new InventoryItem("obsidian", 1), gunsmith.getRandomHappinessPrice(random, 100, 200, 20)));
            }
        /*list.add(ShopItem.item(new InventoryItem("simplebullet", 100), gunsmith.getRandomHappinessPrice(random, 50, 100, 10)));
        if (client.characterStats().mob_kills.getKills("evilsprotector") > 0 || client.characterStats().mob_kills.getKills("queenspider") > 0) {
            list.add(ShopItem.item(new InventoryItem("LoadingBench", 1), gunsmith.getRandomHappinessPrice(random, 250, 500, 50)));
        }*/
        }
   }
}
