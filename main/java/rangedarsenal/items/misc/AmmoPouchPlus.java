package rangedarsenal.items.misc;

import necesse.engine.sound.SoundManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.*;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.PouchItem;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.ItemPickupText;
import rangedarsenal.scripts.ItemPickupTextSpecial;

import java.awt.*;
import java.io.FileNotFoundException;

public class AmmoPouchPlus extends PouchItem {
    public AmmoPouchPlus() {
        this.rarity = Rarity.UNCOMMON;
    }
    //oh yeah this is written like shit
    int heldwep;
    //0 none
    //1 bow
    //2 gun
    //3 cannon
    //4 flamer
    //5 GL
    int count;
    int slot;
    int slot2;
    int startslot;
    int startslot2;
    int endslot2;
    public String report;
    String activebullet;
    String newbullet;

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "ammopouchtip1"));
        tooltips.add(Localization.translate("itemtooltip", "AmmoPouchPlusTip1"));
        tooltips.add(Localization.translate("itemtooltip", "AmmoPouchPlusTipPrimary"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        tooltips.add(Localization.translate("itemtooltip", "storedammo", "items", this.getStoredItemAmounts(item)));
        return tooltips;
    }

    public boolean isValidPouchItem(InventoryItem item) {
        return this.isValidRequestType(item.item.type);
    }

    public boolean isValidRequestItem(Item item) {
        if (item == null) {
            return !this.isValidRequestType(null);
        } else {
            return this.isValidRequestType(item.type);
        }
    }

    public boolean isValidRequestType(Item.Type type) {
        return type == Type.ARROW || type == Type.BULLET;
    }

    public int getInternalInventorySize() {
        return 10;
    }

    //get active weapon
    public int CurrentWeapon(PlayerMob perspective) {
        if (perspective.getSelectedItem() != null) {
            if (perspective.getSelectedItem().item.getClass().getSuperclass().toString().contains("bow")) {
                //System.out.println("BOW!");
                heldwep = 1;
            } else if (perspective.getSelectedItem().item.getClass().getSuperclass().toString().contains("gun")) {
                if (perspective.getSelectedItem().item.getClass().toString().toLowerCase().contains("handcannon")) {
                    //System.out.println("CANNON!");
                    heldwep = 3;
                } else if (perspective.getSelectedItem().item.getClass().toString().toLowerCase().contains("flamethrower")) {
                    //System.out.println("Flamethrower!");
                    heldwep = 4;
                } else if (perspective.getSelectedItem().item.getClass().toString().toLowerCase().contains("launcher")) {
                    //System.out.println("Launcher!");
                    heldwep = 5;
                } else {
                    //System.out.println("GUN!");
                    heldwep = 2;
                }
            } else {
                //none
                heldwep = 0;
            }
        } else {
            //empty
            heldwep = 0;
        }
        return heldwep;
    }
    //display valid ammo icon
    public void drawIcon(InventoryItem item, PlayerMob perspective, int x, int y, int size, String ctype) {
        Color col = this.getDrawColor(item, perspective);
        if (heldwep != 0 && (getStoredItemAmounts(item) != 0)) {
            Inventory internalInventory = this.getInternalInventory(item);
            for (int i = 0; i < internalInventory.getSize(); ++i) {
                if (!internalInventory.isSlotClear(i)) {
                    slot2 = i;
                }
            }
            if (this.getInternalInventory(item).getItem(slot2) != null) {
                if ((this.getInternalInventory(item).getItem(slot2).item.type == Type.BULLET) && !this.getInternalInventory(item).getItem(slot2).item.getClass().toString().toLowerCase().contains("ball") && !this.getInternalInventory(item).getItem(slot2).item.getClass().toString().toLowerCase().contains("fuel") && !this.getInternalInventory(item).getItem(slot2).item.getClass().toString().toLowerCase().contains("shells") && heldwep == 2) {
                    this.getItemSpriteBackground(item, perspective).initDraw().color(255f, 255f, 255f, 0.38f).size(35).draw(x, y);
                    this.getItemSprite(item, perspective).initDraw().color(col).size(size).draw(x, y - 4);
                } else {
                    this.getItemSpriteBackground(item, perspective).initDraw().color(255f, 255f, 255f, 0.38f).size(35).draw(x, y);
                    this.getItemSprite(item, perspective).initDraw().color(col).size(size).draw(x, y);
                }
            }
        } else {
            this.getItemSprite(item, perspective).initDraw().color(col).size(size).draw(x, y);
        }
    }
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        if (slot < 0) {
            if (this.fullTexture != null) {
                Inventory internalInventory = this.getInternalInventory(item);

                for (int i = 0; i < internalInventory.getSize(); ++i) {
                    if (!internalInventory.isSlotClear(i)) {
                        return new GameSprite(this.fullTexture, 32);
                    }
                }
            }
        } else {
            if (!(this.getInternalInventory(item).getItem(slot) == null)) {
                try {
                    return new GameSprite(GameTexture.fromFileRaw("items/" + this.getInternalInventory(item).getItem(slot).item.getStringID()));
                } catch (FileNotFoundException e) {
                    try {
                        return new GameSprite(GameTexture.fromFileRaw("items/" + this.getStringID()));
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } else {
                try {
                    return new GameSprite(GameTexture.fromFileRaw("items/" + this.getStringID()));
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return super.getItemSprite(item, perspective);
    }
    public GameSprite getItemSpriteBackground(InventoryItem item, PlayerMob perspective) {
        if (this.fullTexture != null) {
            Inventory internalInventory = this.getInternalInventory(item);

            for (int i = 0; i < internalInventory.getSize(); ++i) {
                if (!internalInventory.isSlotClear(i)) {
                    return new GameSprite(this.fullTexture);
                }
            }
        }
        return super.getItemSprite(item, perspective);
    }

    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        Inventory internalInventory = this.getInternalInventory(item);

        //will grab the first ammo type and display its total amount
        if (this.drawStoredItems) {

            //get held weapon
            heldwep = CurrentWeapon(perspective);

            //get first filled slot that matches weapon
            if (heldwep != 0) {
                for(int i = 0; i < internalInventory.getSize(); ++i) {
                    if (internalInventory.getAmount(i) > 0) {
                        if ((internalInventory.getItem(i).item.type == Type.ARROW) && heldwep == 1) {
                            slot = i;
                            report = "arrow";
                            break;
                        } else if (internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("ball") && heldwep == 3) {
                            slot = i;
                            report = "cannon";
                            break;
                        } else if (internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("fuel") && heldwep == 4) {
                            slot = i;
                            report = "flame";
                            break;
                        } else if (internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("shells") && heldwep == 5) {
                            slot = i;
                            report = "launcher";
                            break;
                        } else if ((internalInventory.getItem(i).item.type == Type.BULLET) && !internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("ball") && !internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("fuel") && !internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("shells") && heldwep == 2) {
                            slot = i;
                            report = "bullet";
                            break;
                        }
                        else {
                            slot = -1;
                        }
                    }
                    if (internalInventory.getAmount(i) < 0 && internalInventory.getSize() == i) {
                        slot = -1;
                        break;
                    }
                }
            } else {
                slot = -2;
            }
            //System.out.println("slot:" + slot);
            this.drawIcon(item, perspective, x, y, 32,report);

            //get all ammo that matches first filled slot
            if (internalInventory.getAmount(slot) > 0) {
                int total = 0;
                //get first grouping of ammo
                for(int i = 0; i < internalInventory.getSize(); ++i) {
                    if (internalInventory.getAmount(i) > 0) {
                        if (internalInventory.getItem(i).getItemDisplayName().equals(internalInventory.getItem(slot).getItemDisplayName())) {
                            total += internalInventory.getAmount(i);
                        } else if (!(internalInventory.getItem(i).item.type == Type.ARROW) && heldwep == 1) {
                            //ignore slot
                        } else if (!internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("ball") && heldwep == 3) {
                            //ignore slot
                        } else if (!internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("fuel") && heldwep == 4) {
                            //ignore slot
                        } else if (!internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("shells") && heldwep == 5) {
                            //ignore slot
                        } else if (((internalInventory.getItem(i).item.type == Type.ARROW) || internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("ball") || internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("fuel") || internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("shells")) && heldwep == 2) {
                            //ignore slot
                        } else {
                            break;
                        }
                    }
                }
                //if ammo stacks to 1000, why cap at 999
                if (total > 9999) {
                    total = 9999;
                }
                if (heldwep == 0) {
                    count = getStoredItemAmounts(item);
                    if (count > 9999) {
                        count = 9999;
                    }
                    String amountString = String.valueOf(count);
                    int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
                    FontManager.bit.drawString((float)(x + 32 - width), (float)(y + 20), amountString, tipFontOptions);
                } else {
                        String amountString = String.valueOf(total);
                        int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
                        FontManager.bit.drawString((float)(x + 32 - width), (float)(y + 20), amountString, tipFontOptions);
                }
            } else if (slot == -1) {
                if (heldwep != 0) {
                    count = 0;
                    String amountString = String.valueOf(count);
                    int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
                    FontManager.bit.drawString((float) (x + 32 - width), (float) (y + 20), amountString, tipFontOptions);
                }
            } else {
                count = getStoredItemAmounts(item);
                if (count > 9999) {
                    count = 9999;
                }
                String amountString = String.valueOf(count);
                int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
                FontManager.bit.drawString((float)(x + 32 - width), (float)(y + 20), amountString, tipFontOptions);
            }
        }
    }
    public InventoryItem onAttack(Level level, int x, int y, PlayerMob player, int attackHeight, InventoryItem item, PlayerInventorySlot slotP, int animAttack, int seed, PacketReader contentReader) {
        Inventory internalInventory = this.getInternalInventory(item);
        if (internalInventory.getItem(0) == null) {
            //internalInventory.sortItems();
        }
        boolean used = false;

        //get first item
        for(int i = 0; i < internalInventory.getSize(); ++i) {
            if (internalInventory.getAmount(i) > 0) {
                activebullet = internalInventory.getItem(i).item.idData.getStringID();
                break;
            } else {
                startslot++;
            }
        }
        //get last item
        /*for(int i = startslot; i < internalInventory.getSize(); ++i) {
            if (internalInventory.getAmount(i) > 0) {
                if (!internalInventory.getItem(i).item.idData.getStringID().equals(activebullet)) {
                    moveslots = i-1;
                   if (moveslots == 0) {
                       moveslots++;
                   }
                   break;
                } else {
                    moveslots++;
                }
            }
        }*/
        //get new item
        for(int i = 0; i < internalInventory.getSize(); ++i) {
            if (internalInventory.getAmount(i) > 0 && !(internalInventory.getItem(i).item.idData.getStringID().equals(activebullet))) {
                newbullet = internalInventory.getItem(i).item.idData.getStringID();
                startslot2 = i;
                break;
            }
        }
        //get last new item
        for(int i = startslot2; i < internalInventory.getSize(); ++i) {
            if (internalInventory.getItem(i) != null) {
                if (!(internalInventory.getItem(i).item.idData.getStringID().equals(newbullet))) {
                    endslot2 = i-1;
                    break;
                }
            }
        }
        //SHUFFLE TIME
        for (int i = 0; i < startslot2; ++i) {
            for (int z = 1; z < internalInventory.getSize(); ++z) {
                internalInventory.swapItems(z-1, z);
            }
        }
        used = true;
        if (used) {
            this.saveInternalInventory(item, internalInventory);
        }
        SoundManager.playSound(GameResources.cameraShutter, SoundEffect.effect(player)
                .volume(0.7f)
                .pitch(GameRandom.globalRandom.getFloatBetween(1.3f, 1.4f)));

        //Get average color, send to text
        for(int i = 0; i < internalInventory.getSize(); ++i) {
            if (internalInventory.getAmount(i) > 0) {
                if (!level.isServer()) {
                    if (internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("ball")) {
                        //cannon ball exclusive
                        Color avg = new Color(161, 161, 161);
                        level.hudManager.addElement((new ItemPickupTextSpecial(player, internalInventory.getItem(i), avg)));
                        break;
                    } else if (internalInventory.getItem(i).item.getClass().toString().toLowerCase().contains("arrow")) {
                        //arrow exclusive
                        if (this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getAlpha(4,6) != 0) {
                            //vanilla textures
                            int red = Math.toIntExact((this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getRed(4, 6) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getRed(6, 8) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getRed(8, 10)) / 3);
                            int green = Math.toIntExact((this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getGreen(4, 6) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getGreen(6, 8) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getGreen(8, 10)) / 3);
                            int blue = Math.toIntExact((this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getBlue(4, 6) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getBlue(6, 8) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getBlue(8, 10)) / 3);
                            Color avg = new Color(red, green, blue);
                            level.hudManager.addElement((new ItemPickupTextSpecial(player, internalInventory.getItem(i), avg)));
                        } else {
                            //OG textures
                            int red = Math.toIntExact((this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getRed(14, 7) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getRed(14, 9) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getRed(12, 11)) / 3);
                            int green = Math.toIntExact((this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getGreen(14, 7) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getGreen(14, 9) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getGreen(12, 11)) / 3);
                            int blue = Math.toIntExact((this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getBlue(14, 7) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getBlue(14, 9) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getBlue(12, 11)) / 3);
                            Color avg = new Color(red, green, blue);
                            level.hudManager.addElement((new ItemPickupTextSpecial(player, internalInventory.getItem(i), avg)));
                        }
                        break;
                    } else {
                        //bullets, fuel, shells
                        int red = Math.toIntExact((this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getRed(13, 15) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getRed(16, 15) + this.getInternalInventory(item).getItem(i).item.getItemSprite(item, player).texture.getRed(18, 15)) / 3);
                        int green = Math.toIntExact((internalInventory.getItem(i).item.getItemSprite(item, player).texture.getGreen(13, 15) + internalInventory.getItem(i).item.getItemSprite(item, player).texture.getGreen(16, 15) + internalInventory.getItem(i).item.getItemSprite(item, player).texture.getGreen(18, 15)) / 3);
                        int blue = Math.toIntExact((internalInventory.getItem(i).item.getItemSprite(item, player).texture.getBlue(13, 15) + internalInventory.getItem(i).item.getItemSprite(item, player).texture.getBlue(16, 15) + internalInventory.getItem(i).item.getItemSprite(item, player).texture.getBlue(18, 15)) / 3);
                        if (red + 50 < 255 && blue + 50 < 255 && green + 50 < 255) {
                            //brighten colors of bullets, typically very dark
                            red = red + 50;
                            blue = blue + 50;
                            green = green + 50;
                        }
                        Color avg = new Color(red, green, blue);
                        level.hudManager.addElement((new ItemPickupTextSpecial(player, internalInventory.getItem(i), avg)));
                        break;
                    }
                } else {
                    break;
                }
            }
        }

        return item;
    }
}
