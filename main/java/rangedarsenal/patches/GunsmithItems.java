package rangedarsenal.patches;

import java.util.ArrayList;
import java.util.Iterator;

import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.ShopManager;
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

@ModConstructorPatch(
        target = GunsmithHumanMob.class,
        arguments = {}
)
public class GunsmithItems {
    public GunsmithItems() {
    }
    /*@Advice.OnMethodEnter()
    static void onEnter(@Advice.This GunsmithHumanMob mobShop) {
        //mobShop.shop.sellingShop.addItem("gunpowder",new SellingShopItem()).setItem(new InventoryItem("gunpowder", 10)).setStaticPriceBasedOnHappiness(250, 750, 50);
        //mobShop.shop.addSellingItem("gunpowder", new SellingShopItem()).setItem(new InventoryItem("gunpowder", 10)).setStaticPriceBasedOnHappiness(250, 750, 50);
        *//*mobShop.shop.addSellingItem("obsidian", new SellingShopItem()).setItem(new InventoryItem("obsidian", 5)).setStaticPriceBasedOnHappiness(100, 1000, 25).addKilledMobRequirement("fallenwizard");
        mobShop.shop.addSellingItem("Mechanical_Parts", new SellingShopItem()).setItem(new InventoryItem("Mechanical_Parts", 1)).setStaticPriceBasedOnHappiness(100, 400, 50).addKilledMobRequirement("piratecaptain");
        mobShop.shop.addSellingItem("Mechanical_Parts_Good", new SellingShopItem()).setItem(new InventoryItem("Mechanical_Parts_Good", 1)).setStaticPriceBasedOnHappiness(200, 700, 50).addKilledMobRequirement("fallenwizard");*//*
    }*/
    @Advice.OnMethodExit()
    static void onExit(@Advice.This GunsmithHumanMob mobShop) {
        mobShop.shop.sellingShop.getItem("handgun").addKilledMobRequirement("flameling");
        mobShop.shop.sellingShop.getItem("machinegun").addKilledMobRequirement("flameling");
        mobShop.shop.sellingShop.getItem("shotgun").addKilledMobRequirement("flameling");
        mobShop.shop.sellingShop.getItem("sniperrifle").addKilledMobRequirement("flameling");
        mobShop.shop.sellingShop.getItem("deathripper").addKilledMobRequirement("flameling");
        //mobShop.shop.sellingShop.getItem("debug").setItem("gunpowder");
        //mobShop.shop.sellingShop.getItem("handgun").setItem(new InventoryItem("gunpowder", 10)).setStaticPriceBasedOnHappiness(250, 750, 50);

        //mobShop.shop.sellingShop.addItem("gunpowder",new SellingShopItem()).setItem(new InventoryItem("gunpowder", 10)).setStaticPriceBasedOnHappiness(250, 750, 50);
        //mobShop.shop.addSellingItem("gunpowder", new SellingShopItem()).setItem(new InventoryItem("gunpowder", 10)).setStaticPriceBasedOnHappiness(250, 750, 50);
        /*mobShop.shop.addSellingItem("obsidian", new SellingShopItem()).setItem(new InventoryItem("obsidian", 5)).setStaticPriceBasedOnHappiness(100, 1000, 25).addKilledMobRequirement("fallenwizard");
        mobShop.shop.addSellingItem("Mechanical_Parts", new SellingShopItem()).setItem(new InventoryItem("Mechanical_Parts", 1)).setStaticPriceBasedOnHappiness(100, 400, 50).addKilledMobRequirement("piratecaptain");
        mobShop.shop.addSellingItem("Mechanical_Parts_Good", new SellingShopItem()).setItem(new InventoryItem("Mechanical_Parts_Good", 1)).setStaticPriceBasedOnHappiness(200, 700, 50).addKilledMobRequirement("fallenwizard");*/
    }
}