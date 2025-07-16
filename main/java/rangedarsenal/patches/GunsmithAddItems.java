package rangedarsenal.patches;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.Packet;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.*;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.ContainerQuest;
import necesse.inventory.container.mob.ShopContainer;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import net.bytebuddy.asm.Advice;

import java.util.ArrayList;
import java.util.List;

@ModMethodPatch(
        target = HumanShop.class,
        name = "getShopContainerData",
        arguments = {}
)
public class GunsmithAddItems {
    public GunsmithAddItems() {
    }
    /*@Advice.OnMethodEnter
    static boolean onEnter(@Advice.This GunsmithHumanMob mobShop) {
        return false;
    }*/
    @Advice.OnMethodEnter()
    static void onEnter(@Advice.This HumanShop mobShop) {
        //mobShop.shop.addSellingItem("handgun", new SellingShopItem()).setStaticPriceBasedOnHappiness(200, 400, 50);
        //mobShop.shop.sellingShop.getItem("handgun").setItem(new InventoryItem("gunpowder", 10)).setStaticPriceBasedOnHappiness(250, 750, 50);
        //gunGuy.shop.sellingShop.addItem("gunpowder",new SellingShopItem()).setItem(new InventoryItem("gunpowder", 10)).setStaticPriceBasedOnHappiness(250, 750, 50);
        //gunGuy.shop.addSellingItem("gunpowder", new SellingShopItem()).setItem(new InventoryItem("gunpowder", 10)).setStaticPriceBasedOnHappiness(250, 750, 50);
        //gunGuy.shop.addSellingItem("obsidian", new SellingShopItem()).setItem(new InventoryItem("obsidian", 5)).setStaticPriceBasedOnHappiness(100, 1000, 25).addKilledMobRequirement("fallenwizard");
        //gunGuy.shop.addSellingItem("Mechanical_Parts", new SellingShopItem()).setItem(new InventoryItem("Mechanical_Parts", 1)).setStaticPriceBasedOnHappiness(100, 400, 50).addKilledMobRequirement("piratecaptain");
        //gunGuy.shop.addSellingItem("Mechanical_Parts_Good", new SellingShopItem()).setItem(new InventoryItem("Mechanical_Parts_Good", 1)).setStaticPriceBasedOnHappiness(200, 700, 50).addKilledMobRequirement("fallenwizard");
    }
    /*@Advice.OnMethodExit()
    static ShopContainerData onExit(@Advice.This HumanShop mobShop, @Advice.Argument(1) ServerClient client, @Advice.Return(readOnly = false) ShopContainerData shopContainerData) {
        if (mobShop.settlerStringID.equalsIgnoreCase("")) {
            boolean isVisitorShop = mobShop.isVisitorShop();
            boolean isVisitor = mobShop.isVisitor();
            boolean isSettler = mobShop.isSettler();
            boolean isDowned = mobShop.downedState != null;
            VillageShopsData shopData = VillageShopsData.getShopData(mobShop.getLevel());
            List<ShopItem> shopItems = !isDowned && (!isVisitor || isVisitorShop) ? mobShop.getShopItems(shopData, client) : null;
        }

        VillageShopsData shopData = VillageShopsData.getShopData(mobShop.getLevel());
        boolean isVisitorShop = mobShop.isVisitorShop();
        boolean isVisitor = mobShop.isVisitor();
        boolean isSettler = mobShop.isSettler();
        boolean isDowned = mobShop.downedState != null;
        ArrayList<ContainerQuest> quests = !isDowned && !isVisitor ? mobShop.getQuests(client) : null;
        List<ShopItem> shopItems = !isDowned && (!isVisitor || isVisitorShop) ? mobShop.getShopItems(shopData, client) : null;
        GameMessage introMessage = mobShop.isTrapped() ? mobShop.getTrappedMessage(client) : mobShop.getDialogueIntroMessage(client);
        GameMessage recruitError;
        if (isSettler) {
            recruitError = null;
        } else {
            recruitError = mobShop.getBaseRecruitError(client);
            if (!mobShop.isDowned() && (!isVisitor || isVisitorShop) && recruitError == null) {
                recruitError = mobShop.getRecruitError(client);
            }
        }

        List<InventoryItem> recruitItems = null;
        if (recruitError == null) {
            if (mobShop.isDowned()) {
                recruitItems = new ArrayList();
                recruitItems.add(new InventoryItem("revivalpotion", 1));
            } else if (!isSettler) {
                recruitItems = mobShop.getRecruitItems(client);
            }
        }

        ShopManager shopManager = !isDowned && (!isVisitor || isVisitorShop) ? mobShop.getShop() : null;
        Packet content = ShopContainer.getContainerContent(mobShop, client, mobShop.getWorkInvMessage(), mobShop.getMissionFailedMessage(), introMessage, quests, shopItems, shopManager, recruitError, recruitItems, mobShop.startInRecruitForm(client), mobShop.getPossibleExpeditions(), mobShop.workSettings);
        return new ShopContainerData(content, shopManager);
    }*/
}