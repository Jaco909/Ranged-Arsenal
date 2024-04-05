package rangedarsenal.patches;

import java.util.ArrayList;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.Advice.Argument;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.humanShop.TravelingMerchantMob;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;

@ModMethodPatch(target = TravelingMerchantMob.class, name = "getShopItems", arguments = {VillageShopsData.class, ServerClient.class})
public class MerchantItems {
    @Advice.OnMethodExit()
    static void onExit(@Advice.This TravelingMerchantMob merchant, @Argument(1) ServerClient client, @Advice.Return(readOnly = false) ArrayList<ShopItem> list) {

        //remove ammo pouch
        int listcount = 0;
        while (listcount < list.size()) {
            System.out.println(list.get(listcount).item.getItemDisplayName());
            if (list.get(listcount).item.item.idData.getStringID().equalsIgnoreCase("ammopouch")){
                list.remove(listcount);
            }else {
                listcount++;
            }
        }
    }
}
