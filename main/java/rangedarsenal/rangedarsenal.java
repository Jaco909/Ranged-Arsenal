package rangedarsenal;

import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.*;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.bosses.PestWardenHead;
import necesse.engine.sound.gameSound.GameSound;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.presets.CrateLootTable;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;

import rangedarsenal.buffs.*;
import rangedarsenal.events.*;
import rangedarsenal.items.bullets.food.*;
import rangedarsenal.items.bullets.fuel.*;
import rangedarsenal.items.bullets.gunbullets.*;
import rangedarsenal.items.bullets.shells.*;
import rangedarsenal.items.materials.*;
import rangedarsenal.items.misc.*;
import rangedarsenal.items.weapons.*;
import rangedarsenal.projectiles.bullets.*;
import rangedarsenal.projectiles.food.*;
import rangedarsenal.projectiles.fuel.*;
import rangedarsenal.projectiles.modifiers.CrystalizeModifier;
import rangedarsenal.projectiles.modifiers.WebbedModifier;
import rangedarsenal.projectiles.shells.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static necesse.inventory.item.ItemCategory.craftingManager;

@ModEntry
public class rangedarsenal {

    //TO-DO:
    /*
    CONTENT
    -draw sniper crit chance under mouse
    -web gun
    -journal tabs
    -laser weps
    -incursion drop pools
    -range flamethrower rework
    -better ammo bag?
     */

    //LOAD CONSTANTS
    //public static HashSet<String> SEED_AMMO_TYPES = new HashSet(Arrays.asList("grassseed","swampgrassseed","plainsgrassseed","overgrownplainsgrassseed","iceblossomseed","firemoneseed","sunflowerseed","wheatseed","cornseed","tomatoseed","cabbageseed","chilipepperseed","sugarbeetseed","eggplantseed","potatoseed","riceseed","carrotseed","onionseed","pumpkinseed","strawberryseed","kew_copper_seed","kew_iron_seed","kew_gold_seed","kew_tier_1_seed","kew_tier_2_seed"));
    public static LinkedHashSet<String> FOOD_AMMO_TYPES = new LinkedHashSet(Arrays.asList("apple","banana","blackberry","blueberry","cabbage","carrot","chilipepper","coconut","corn","eggplant","lemon","onion","potato","pumpkin","rice","strawberry","sugarbeet","tomato","wheat","coffeebeans","raspberry","beet"));
    public static LinkedHashSet<String> FLAME_AMMO_TYPES = new LinkedHashSet(Arrays.asList("Gasoline", "CryoFlame", "Napalm", "MoltenSlime"));
    public static LinkedHashSet<String> SHELL_AMMO_TYPES = new LinkedHashSet(Arrays.asList("Grenade_Launcher_Shell","Grenade_Launcher_Mine_Shell","Grenade_Launcher_Proxy_Shell","Grenade_Launcher_Fire_Shell"));

    public static GameSound proxyarm;
    public static GameTexture GlProxyArmingTex;
    public static GameTexture GlProxyArmedTex;


    public rangedarsenal(){
    }
    public void preInit() {
        //append all custom bullets to vanilla list
        //this prevents overriding bullets from other mods
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("Standard_Bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("Frozen_Bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("Flame_Bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("Blunt_Bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("Leach_Bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("Lightning_Bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("Splintering_Bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("Ruby_Bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("Sapphire_Bullet");
        GunProjectileToolItem.NORMAL_AMMO_TYPES.add("Amethyst_Bullet");

        //CATEGORIES
        ItemCategory.masterManager.createCategory("A-C-B", "bullets");
        craftingManager.createCategory("D-C-B", new String[]{"bullets"});

        ItemCategory.masterManager.createCategory("A-C-C", "shells");
        craftingManager.createCategory("D-C-C", new String[]{"shells"});

        ItemCategory.masterManager.createCategory("B-D-B", "fuel");
        craftingManager.createCategory("C-B-A", new String[]{"fuel"});

        ItemCategory.masterManager.createCategory("A-B-F", "ballistic");
        craftingManager.createCategory("D-B-F", new String[]{"ballistic"});

        ItemCategory.masterManager.createCategory("A-B-G", "explosive");
        craftingManager.createCategory("D-B-G", new String[]{"explosive"});

        ItemCategory.masterManager.createCategory("A-B-H", "flame");
        craftingManager.createCategory("D-B-H", new String[]{"flame"});

        ItemCategory.masterManager.createCategory("A-B-I", "farm");
        craftingManager.createCategory("D-B-I", new String[]{"farm"});
    }

    public void init() {
        //Jabe Archipelago makes a shit ton of overrides that assume no other mods replace an item
        //Run this so it doesn't crash if it loads after RA
        boolean arch = false;
        Iterator mods = ModLoader.getEnabledMods().iterator();
        while (mods.hasNext()) {
            LoadedMod mod = (LoadedMod) mods.next();
            if (mod.id.equalsIgnoreCase("fredyjabe.jabearchipelago")) {
                arch= true;
            }
        }
        if (!arch) {
            ItemRegistry.replaceItem("bouncingbullet", new NewBouncingBullet(), 0.1f, true, true);
            ItemRegistry.replaceItem("crystalbullet", new SapphireBullet(), 0.2f, true);
            ItemRegistry.replaceItem("sniperrifle", new SniperRework(),100f,true,true);
            ItemRegistry.replaceItem("cryoblaster", new CryoBlasterRework(),200f,true,true);
            ItemRegistry.replaceItem("shardcannon", new ShardCannonRework(),1500f,true,true);
            ItemRegistry.replaceItem("sapphirerevolver", new SapphireRevolverRework(),1500f,true,true);
            ItemRegistry.replaceItem("webbedgun", new WebGunRework(),300f,true,true);
            ItemRegistry.replaceItem("frostbullet", new FrozenBullet(),0.1f,true,true);
        }



        //ITEMS
        if (!ItemRegistry.itemExists("AmmoPouchPlus")) {
            ItemRegistry.registerItem("AmmoPouchPlus", new AmmoPouchPlus(), 100f, true);
            ItemRegistry.registerItem("AmmoBagPlus", new AmmoBagPlus(), 100f, true);
        }

        //MATERIALS
        ItemRegistry.registerItem("Niter", new Niter(), 0.05f, true);
        ItemRegistry.registerItem("Gunpowder", new Gunpowder(), 0.05f, true);
        ItemRegistry.registerItem("Gun_Parts", new GunParts(), 0.1f, false,false);
        ItemRegistry.registerItem("Bullet_Casing", new BulletCasing(), 0.05f, true);
        ItemRegistry.registerItem("Canister", new Canister(), 1f, false,false);
        ItemRegistry.registerItem("Mechanical_Parts", new MechanicalParts(), 5f, true);
        ItemRegistry.registerItem("Mechanical_Parts_Good", new MechanicalPartsGood(), 10f, true);
        ItemRegistry.registerItem("Mechanical_Parts_Great", new MechanicalPartsGreat(), 20f, true);

        //BULLETS
        ItemRegistry.registerItem("Standard_Bullet", new StandardBullet(), 0.1f, true);
        ItemRegistry.registerItem("Frozen_Bullet", new FrozenBullet(), 0.1f, true);
        ItemRegistry.registerItem("Leach_Bullet", new LeachBullet(), 0.3f, true);
        ItemRegistry.registerItem("Flame_Bullet", new FlameBullet(), 0.1f, true);
        ItemRegistry.registerItem("Blunt_Bullet", new BluntBullet(), 0.2f, true);
        ItemRegistry.registerItem("Lightning_Bullet", new LightningBullet(), 0.3f, true);
        ItemRegistry.registerItem("Splintering_Bullet", new SplinteringBullet(), 0.1f, true);
        ItemRegistry.registerItem("Ruby_Bullet", new RubyBullet(), 0.2f, true);
        ItemRegistry.registerItem("Amethyst_Bullet", new AmethystBullet(), 0.2f, true);

        //Produce Cannon
        ItemRegistry.registerItem("Apple_Food_Bullet", new AppleFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Banana_Food_Bullet", new BananaFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Blackberry_Food_Bullet", new BlackberryFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Blueberry_Food_Bullet", new BlueberryFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Cabbage_Food_Bullet", new CabbageFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Carrot_Food_Bullet", new CarrotFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Chilipepper_Food_Bullet", new ChilipepperFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Coconut_Food_Bullet", new CoconutFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Corn_Food_Bullet", new CornFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Coffee_Food_Bullet", new CoffeeFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Eggplant_Food_Bullet", new EggplantFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Lemon_Food_Bullet", new LemonFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Onion_Food_Bullet", new OnionFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Potato_Food_Bullet", new PotatoFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Pumpkin_Food_Bullet", new PumpkinFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Rice_Food_Bullet", new RiceFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Strawberry_Food_Bullet", new StrawberryFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Sugarbeet_Food_Bullet", new SugarbeetFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Tomato_Food_Bullet", new TomatoFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Wheat_Food_Bullet", new WheatFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Raspberry_Food_Bullet", new RaspberryFoodBullet(), 0f, false,false);
        ItemRegistry.registerItem("Beet_Food_Bullet", new BeetFoodBullet(), 0f, false,false);


        //Flamethrower
        ItemRegistry.registerItem("Gasoline", new GasolineBullet(), 0.2f, true);
        ItemRegistry.registerItem("CryoFlame", new CryoFlameBullet(), 0.2f, true);
        ItemRegistry.registerItem("Napalm", new NapalmBullet(), 0.2f, true);
        ItemRegistry.registerItem("MoltenSlime", new MoltenSlimeBullet(), 0.2f, true);

        //launcher
        ItemRegistry.registerItem("Grenade_Launcher_Shell", new GrenadeLauncherShell(), 0.3f, true,true);
        ItemRegistry.registerItem("Grenade_Launcher_Mine_Shell", new GrenadeLauncherMineShell(), 0.3f, true,true);
        ItemRegistry.registerItem("Grenade_Launcher_Proxy_Shell", new GrenadeLauncherProxyShell(), 0.3f, true,true);
        ItemRegistry.registerItem("Grenade_Launcher_Fire_Shell", new GrenadeLauncherFireShell(), 0.3f, true,true);

        //WEAPONS
        ItemRegistry.registerItem("Junk_Pistol", new JunkPistol(), 90f, true,true);
        ItemRegistry.registerItem("Lever_Action_Rifle", new LeverActionRifle(), 100f, true,true);
        ItemRegistry.registerItem("Double_Barrel", new DoubleBarrel(), 200f, true,true);
        ItemRegistry.registerItem("Light_Machinegun", new LightMachinegun(), 300f, true,true);
        ItemRegistry.registerItem("Normal_Revolver", new NormalRevolver(), 300f, true,true);
        ItemRegistry.registerItem("Flamethrower", new Flamethrower(), 300f, true,true);
        ItemRegistry.registerItem("Grenade_Launcher", new GrenadeLauncher(), 400f, true,true);
        ItemRegistry.registerItem("AWP", new AWP(),300f,true,true);
        ItemRegistry.registerItem("SeedGun", new SeedGun(),60f,true,true);
        ItemRegistry.registerItem("SeedGunShotgun", new SeedGunShotgun(),100f,true,true);
        ItemRegistry.registerItem("SeedGunMega", new SeedGunMega(),300f,true,true);
        ItemRegistry.registerItem("ProduceCannon", new ProduceCannon(),200f,true,true);
        ItemRegistry.registerItem("ProduceCannonMega", new ProduceCannonMega(),300f,true,true);


        //PROJECTILES
        ProjectileRegistry.registerProjectile("Frozen_Bullet_Projectile", FrozenBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Flame_Bullet_Projectile", FlameBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Blunt_Bullet_Projectile", BluntBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Leach_Bullet_Projectile", LeachBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Lightning_Bullet_Projectile", LightningBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Splintering_Bullet_Projectile", SplinteringBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Sapphire_Bullet_Projectile", SapphireBulletProjectile.class,"crystalbullet","crystalbullet_shadow");
        ProjectileRegistry.registerProjectile("Ruby_Bullet_Projectile", RubyBulletProjectile.class,"rubybullet","crystalbullet_shadow");
        ProjectileRegistry.registerProjectile("SapphireSplosion_Bullet_Projectile", SapphireSplosionBulletProjectile.class,"amethystbullet","crystalbullet_shadow");
        ProjectileRegistry.registerProjectile("bouncingbulletnew", NewBouncingBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Amethyst_Bullet_Projectile", AmethystBulletProjectile.class,"amethystbullet","crystalbullet_shadow");

        //food
        ProjectileRegistry.registerProjectile("Apple_Bullet_Projectile", AppleBulletProjectile.class,"Apple_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Banana_Bullet_Projectile", BananaBulletProjectile.class,"Banana_Bullet_Projectile","Carrot_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Coconut_Bullet_Projectile", CoconutBulletProjectile.class,"Coconut_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Tomato_Bullet_Projectile", TomatoBulletProjectile.class,"Tomato_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Blackberry_Bullet_Projectile", BlackberryBulletProjectile.class,"Blackberry_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Blueberry_Bullet_Projectile", BlueberryBulletProjectile.class,"Blueberry_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Cabbage_Bullet_Projectile", CabbageBulletProjectile.class,"Cabbage_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Carrot_Bullet_Projectile", CarrotBulletProjectile.class,"Carrot_Bullet_Projectile","Carrot_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Chilipepper_Bullet_Projectile", ChilipepperBulletProjectile.class,"Chilipepper_Bullet_Projectile","Carrot_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Corn_Bullet_Projectile", CornBulletProjectile.class,"Corn_Bullet_Projectile","Carrot_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Coffee_Bullet_Projectile", CoffeeBulletProjectile.class,"Coffee_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Rice_Bullet_Projectile", RiceBulletProjectile.class,"Rice_Bullet_Projectile","Rice_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Eggplant_Bullet_Projectile", EggplantBulletProjectile.class,"Eggplant_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Lemon_Bullet_Projectile", LemonBulletProjectile.class,"Lemon_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Onion_Bullet_Projectile", OnionBulletProjectile.class,"Onion_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Potato_Bullet_Projectile", PotatoBulletProjectile.class,"Potato_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Pumpkin_Bullet_Projectile", PumpkinBulletProjectile.class,"Pumpkin_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Strawberry_Bullet_Projectile", StrawberryBulletProjectile.class,"Strawberry_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Sugarbeet_Bullet_Projectile", SugarbeetBulletProjectile.class,"Sugarbeet_Bullet_Projectile","Carrot_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Wheat_Bullet_Projectile", WheatBulletProjectile.class,"Wheat_Bullet_Projectile","Rice_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Raspberry_Bullet_Projectile", RaspberryBulletProjectile.class,"Raspberry_Bullet_Projectile","Coconut_Bullet_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Beet_Bullet_Projectile", BeetBulletProjectile.class,"Beet_Bullet_Projectile","Carrot_Bullet_Projectile_shadow");

        ProjectileRegistry.registerProjectile("Gasoline_Bullet_Projectile", GasolineBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("CryoFlame_Bullet_Projectile", CryoFlameBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Napalm_Bullet_Projectile", NapalmBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("MoltenSlime_Bullet_Projectile", MoltenSlimeBulletProjectile.class,"","");

        ProjectileRegistry.registerProjectile("Grenade_Launcher_Projectile", GrenadeLauncherProjectile.class,"Grenade_Launcher_Projectile","Grenade_Launcher_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Grenade_Launcher_Mine_Projectile", GrenadeLauncherMineProjectile.class,"Grenade_Launcher_Mine_Projectile","Grenade_Launcher_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Grenade_Launcher_Fire_Projectile", GrenadeLauncherFireProjectile.class,"Grenade_Launcher_Fire_Projectile","Grenade_Launcher_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Grenade_Launcher_Proxy_Projectile", GrenadeLauncherProxyProjectile.class,"Grenade_Launcher_Proxy_Projectile","Coconut_Bullet_Projectile_shadow");


        //BUFFS
        BuffRegistry.registerBuff("HellfireBuff", new HellfireBuff());
        BuffRegistry.registerBuff("FlamerSlow", new FlamerSlow());
        BuffRegistry.registerBuff("FrostyDebuff", new FrostyDebuff());
        BuffRegistry.registerBuff("LightningDebuff", new LightningDebuff());
        BuffRegistry.registerBuff("FlamerSuperSlow", new FlamerSuperSlow());
        BuffRegistry.registerBuff("GasolineDebuff", new GasolineDebuff());
        BuffRegistry.registerBuff("NapalmDebuff", new NapalmDebuff());
        BuffRegistry.registerBuff("MoltenSlimeDebuff", new MoltenSlimeDebuff());
        BuffRegistry.registerBuff("CryoBuildupDebuff", new CryoBuildupDebuff());
        BuffRegistry.registerBuff("CryoFreezeDebuff", new CryoFreezeDebuff());
        BuffRegistry.registerBuff("SlimeSplosionDebuff", new SlimeSplosionDebuff());
        BuffRegistry.registerBuff("DoubleBarrelCooldownDebuff", new DoubleBarrelCooldownDebuff());
        BuffRegistry.registerBuff("LeverActionRifleCooldownDebuff", new LeverActionRifleCooldownDebuff());
        BuffRegistry.registerBuff("CryoBlasterCooldownDebuff", new CryoBlasterCooldownDebuff());
        BuffRegistry.registerBuff("SniperZoomBuff", new SniperZoomBuff());
        BuffRegistry.registerBuff("AWPZoomBuff", new AWPZoomBuff());
        BuffRegistry.registerBuff("BouncyDebuff", new BouncyDebuff());
        BuffRegistry.registerBuff("HealDelayBuff", new HealDelayBuff());
        BuffRegistry.registerBuff("FreezeNerfDebuff", new FreezeNerfDebuff());
        BuffRegistry.registerBuff("AmethystDebuff", new AmethystDebuff());
        BuffRegistry.registerBuff("WebbedGunDebuff", new WebbedGunDebuff());
        BuffRegistry.registerBuff("LightningBlockerDebuff", new LightningBlockerDebuff());

        //EVENTS
        LevelEventRegistry.registerEvent("GrenadeLauncherExplosionEvent", GrenadeLauncherExplosionEvent.class);
        LevelEventRegistry.registerEvent("GrenadeLauncherMineExplosionEvent", GrenadeLauncherMineExplosionEvent.class);
        LevelEventRegistry.registerEvent("GrenadeLauncherFireExplosionEvent", GrenadeLauncherFireExplosionEvent.class);
        LevelEventRegistry.registerEvent("GrenadeLauncherProxyExplosionEvent", GrenadeLauncherProxyExplosionEvent.class);
        LevelEventRegistry.registerEvent("SlimeSplosionEvent", SlimeSplosionEvent.class);
        LevelEventRegistry.registerEvent("LightningJumperEvent", LightningJumperEvent.class);
        LevelEventRegistry.registerEvent("FruitBoomEvent", FruitBoomEvent.class);
        LevelEventRegistry.registerEvent("ShrapnelEvent", ShrapnelEvent.class);

        ProjectileModifierRegistry.registerModifier("CrystalizeModifier", CrystalizeModifier.class);
        ProjectileModifierRegistry.registerModifier("WebbedModifier", WebbedModifier.class);

    }
    public void initResources() {

        //TEXTURES
        GlProxyArmingTex = GameTexture.fromFile("projectiles/Grenade_Launcher_Proxy_Projectile_arming");
        GlProxyArmedTex = GameTexture.fromFile("projectiles/Grenade_Launcher_Proxy_Projectile_armed");

        //SOUNDS
        proxyarm = GameSound.fromFile("activate_single_mono");
    }
    public void postInit() {
        if (ItemRegistry.itemExists("pinkflamerfuel")) {
            FLAME_AMMO_TYPES.add("flamerfuel");
            FLAME_AMMO_TYPES.add("pinkflamerfuel");
            FLAME_AMMO_TYPES.add("stickyflamerfuel");
        }
        if (ItemRegistry.itemExists("pinkflamerfuel")) {
            FLAME_AMMO_TYPES.add("archipelago_fuelcanister");
            FLAME_AMMO_TYPES.add("archipelago_nitrogencanister");
            FLAME_AMMO_TYPES.add("archipelago_ectoplasmcanister");
        }
        PestWardenHead.uniqueDrops.addPossibleLoot(new LootList().add("ProduceCannonMega"));
        CrateLootTable.basicCrate.items.add(new ChanceLootItem(0.20f, "simplebullet", GameRandom.globalRandom.getIntBetween(5, 8)));
        
        //Add recipes

        //BALISTIC
        Recipes.registerModRecipe(new Recipe(
                "Junk_Pistol",
                1,
                RecipeTechRegistry.IRON_ANVIL,
                new Ingredient[]{
                        new Ingredient("copperbar", 14),
                        new Ingredient("ironbar",6)
                }
        ).setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "handgun",
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("ironbar", 10),
                        new Ingredient("Mechanical_Parts",2)
                }
        ).showBefore("Junk_Pistol").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "machinegun",
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("frostshard", 15),
                        new Ingredient("demonicbar", 5),
                        new Ingredient("Mechanical_Parts",3)
                }
        ).showBefore("Junk_Pistol").showAfter("handgun").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "shotgun",
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("voidshard", 15),
                        new Ingredient("demonicbar", 15),
                        new Ingredient("Mechanical_Parts",4)
                }
        ).showBefore("Junk_Pistol").showAfter("machinegun").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "Lever_Action_Rifle",
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("runestone", 10),
                        new Ingredient("anylog", 5),
                        new Ingredient("Mechanical_Parts",2)
                }
        ).showBefore("Junk_Pistol").showAfter("shotgun").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "sniperrifle",
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("quartz", 20),
                        new Ingredient("demonicbar", 5),
                        new Ingredient("Mechanical_Parts",3)
                }
        ).showBefore("Junk_Pistol").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "Normal_Revolver",
                1,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("ironbar", 20),
                        new Ingredient("tungstenbar", 10),
                        new Ingredient("Mechanical_Parts_Good",3)
                }
        ).showBefore("handgun").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "Light_Machinegun",
                1,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("glacialbar", 10),
                        new Ingredient("tungstenbar", 15),
                        new Ingredient("Mechanical_Parts_Good",5)
                }
        ).showBefore("handgun").showAfter("Normal_Revolver").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "Double_Barrel",
                1,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("amber", 10),
                        new Ingredient("demonicbar", 3),
                        new Ingredient("dryadlog", 10),
                        new Ingredient("Mechanical_Parts_Good",4)
                }
        ).showBefore("handgun").showAfter("Light_Machinegun").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "deathripper",
                1,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("shadowessence", 15),
                        new Ingredient("demonicbar", 25),
                        new Ingredient("Mechanical_Parts_Great",1)
                }
        ).showBefore("Normal_Revolver").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "cryoblaster",
                1,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("cryoessence", 15),
                        new Ingredient("demonicbar", 25),
                        new Ingredient("Mechanical_Parts_Great",1)
                }
        ).showBefore("Normal_Revolver").showAfter("deathripper").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "livingshotty",
                1,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("bioessence", 15),
                        new Ingredient("demonicbar", 25),
                        new Ingredient("Mechanical_Parts_Great",2)
                }
        ).showBefore("Normal_Revolver").showAfter("cryoblaster").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "antiquerifle",
                1,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("primordialessence", 15),
                        new Ingredient("demonicbar", 25),
                        new Ingredient("Mechanical_Parts_Great",1)
                }
        ).showBefore("Normal_Revolver").showAfter("livingshotty").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "AWP",
                1,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("nightsteelbar", 10),
                        new Ingredient("demonicbar", 15),
                        new Ingredient("Mechanical_Parts_Great",3)
                }
        ).showBefore("Normal_Revolver").showAfter("antiquerifle").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "sapphirerevolver",
                1,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("sapphire", 10),
                        new Ingredient("pearlescentdiamond", 10),
                        new Ingredient("omnicrystal",20)
                },
                false,
                (new GNDItemMap().setInt("upgradeLevel", 100))
        ).showBefore("Normal_Revolver").showAfter("AWP").setCraftingCategory("ballistic"));
        Recipes.registerModRecipe(new Recipe(
                "shardcannon",
                1,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("sapphire", 5),
                        new Ingredient("ruby", 5),
                        new Ingredient("pearlescentdiamond", 10),
                        new Ingredient("omnicrystal",10)
                },
                false,
                (new GNDItemMap().setInt("upgradeLevel", 100))
        ).showBefore("Normal_Revolver").showAfter("sapphirerevolver").setCraftingCategory("ballistic"));

        //MATERIALS
        Recipes.registerModRecipe(new Recipe(
                "Gunpowder",
                10,
                RecipeTechRegistry.ALCHEMY,
                new Ingredient[]{
                        new Ingredient("firemone", 1),
                        new Ingredient("sandtile", 2)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "firemone",
                1,
                RecipeTechRegistry.ALCHEMY,
                new Ingredient[]{
                        new Ingredient("Niter", 1)
                },
                true
        ));
        Recipes.registerModRecipe(new Recipe(
                "Bullet_Casing",
                50,
                RecipeTechRegistry.IRON_ANVIL,
                new Ingredient[]{
                        new Ingredient("simplebullet", 50)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "Bullet_Casing",
                100,
                RecipeTechRegistry.IRON_ANVIL,
                new Ingredient[]{
                        new Ingredient("copperbar", 2),
                        new Ingredient("ironbar", 1)
                }
        ));

        //GUN BULLETS
        Recipes.registerModRecipe(new Recipe(
                "simplebullet",
                100,
                RecipeTechRegistry.IRON_ANVIL,
                new Ingredient[]{
                        new Ingredient("ironbar", 1),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "Standard_Bullet",
                100,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("Gunpowder", 1),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("simplebullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "bouncingbullet",
                100,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("halffish", 2),
                        new Ingredient("Gunpowder", 2),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showAfter("Standard_Bullet").showBefore("simplebullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "bouncingbullet",
                200,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("slimematter", 1),
                        new Ingredient("Gunpowder", 2),
                        new Ingredient("Bullet_Casing", 200)
                }
        ).showAfter("Standard_Bullet").showAfter("bouncingbullet").showBefore("Frozen_Bullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "Frozen_Bullet",
                100,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("frostshard", 1),
                        new Ingredient("Gunpowder", 2),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showAfter("bouncingbullet").showBefore("simplebullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "Flame_Bullet",
                100,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("firemone", 1),
                        new Ingredient("Gunpowder", 2),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showAfter("Frozen_Bullet").showBefore("simplebullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "Splintering_Bullet",
                100,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("goldbar", 1),
                        new Ingredient("Gunpowder", 2),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showAfter("Flame_Bullet").showBefore("simplebullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "Blunt_Bullet",
                100,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("obsidian", 1),
                        new Ingredient("Gunpowder", 2),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("Standard_Bullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "voidbullet",
                100,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("voidshard", 2),
                        new Ingredient("Gunpowder", 2),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("Standard_Bullet").showAfter("Blunt_Bullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "crystalbullet",
                100,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("sapphire", 3),
                        new Ingredient("Gunpowder", 3),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("Standard_Bullet").showAfter("Blunt_Bullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "Lightning_Bullet",
                100,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("obsidian", 1),
                        new Ingredient("shadowessence", 3),
                        new Ingredient("Gunpowder", 3),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("Blunt_Bullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "Leach_Bullet",
                25,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("obsidian", 1),
                        new Ingredient("lifequartz", 5),
                        new Ingredient("alchemyshard", 15),
                        new Ingredient("Gunpowder", 3),
                        new Ingredient("Bullet_Casing", 25)
                }
        ).showBefore("Blunt_Bullet").showAfter("Lightning_Bullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "Ruby_Bullet",
                100,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("ruby", 3),
                        new Ingredient("obsidian", 2),
                        new Ingredient("Gunpowder", 3),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("Blunt_Bullet").showAfter("Lightning_Bullet").showAfter("Leach_Bullet").setCraftingCategory("bullets"));
        Recipes.registerModRecipe(new Recipe(
                "Amethyst_Bullet",
                100,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("amethyst", 3),
                        new Ingredient("obsidian", 2),
                        new Ingredient("Gunpowder", 3),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("Blunt_Bullet").showAfter("Ruby_Bullet").showAfter("Lightning_Bullet").showAfter("Leach_Bullet").setCraftingCategory("bullets"));

        //FUEL
        Recipes.registerModRecipe(new Recipe(
                "Gasoline",
                200,
                RecipeTechRegistry.VOID_ALCHEMY,
                new Ingredient[]{
                        new Ingredient("lavatile", 1),
                        new Ingredient("firemone", 5)
                        //new Ingredient("Canister", 1)
                }
        ).setCraftingCategory("fuel").onCrafted((event) -> {
            boolean found = false;
            for (int i = 0; i < event.itemsUsed.get(0).inventory.streamSlots().count(); i++) {
                if (event.itemsUsed.get(0).inventory.getItem(i).item.getStringID().equalsIgnoreCase("bucket")) {
                    event.itemsUsed.get(0).inventory.getItem(i).setAmount(event.itemsUsed.get(0).inventory.getItem(i).getAmount()+1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (int i = 0; i < event.itemsUsed.get(0).inventory.streamSlots().count(); i++) {
                    if (event.itemsUsed.get(0).inventory.isSlotClear(i)) {
                        event.itemsUsed.get(0).inventory.setItem(i,new InventoryItem("bucket"));
                        break;
                    }
                }
            }
        }));
        Recipes.registerModRecipe(new Recipe(
                "CryoFlame",
                200,
                RecipeTechRegistry.CAVEGLOW_ALCHEMY,
                new Ingredient[]{
                        new Ingredient("watertile", 1),
                        new Ingredient("glacialshard", 2),
                        new Ingredient("iceblossom", 2)
                }
        ).showBefore("Gasoline").setCraftingCategory("fuel").onCrafted((event) -> {
            boolean found = false;
            for (int i = 0; i < event.itemsUsed.get(0).inventory.streamSlots().count(); i++) {
                if (event.itemsUsed.get(0).inventory.getItem(i).item.getStringID().equalsIgnoreCase("bucket")) {
                    event.itemsUsed.get(0).inventory.getItem(i).setAmount(event.itemsUsed.get(0).inventory.getItem(i).getAmount()+1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (int i = 0; i < event.itemsUsed.get(0).inventory.streamSlots().count(); i++) {
                    if (event.itemsUsed.get(0).inventory.isSlotClear(i)) {
                        event.itemsUsed.get(0).inventory.setItem(i,new InventoryItem("bucket"));
                        break;
                    }
                }
            }
        }));
        Recipes.registerModRecipe(new Recipe(
                "Napalm",
                200,
                RecipeTechRegistry.FALLEN_ALCHEMY,
                new Ingredient[]{
                        new Ingredient("lavatile", 1),
                        new Ingredient("primordialessence", 1)
                }
        ).showBefore("CryoFlame").setCraftingCategory("fuel").onCrafted((event) -> {
            boolean found = false;
            for (int i = 0; i < event.itemsUsed.get(0).inventory.streamSlots().count(); i++) {
                if (event.itemsUsed.get(0).inventory.getItem(i).item.getStringID().equalsIgnoreCase("bucket")) {
                    event.itemsUsed.get(0).inventory.getItem(i).setAmount(event.itemsUsed.get(0).inventory.getItem(i).getAmount()+1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (int i = 0; i < event.itemsUsed.get(0).inventory.streamSlots().count(); i++) {
                    if (event.itemsUsed.get(0).inventory.isSlotClear(i)) {
                        event.itemsUsed.get(0).inventory.setItem(i,new InventoryItem("bucket"));
                        break;
                    }
                }
            }
        }));
        Recipes.registerModRecipe(new Recipe(
                "MoltenSlime",
                200,
                RecipeTechRegistry.FALLEN_ALCHEMY,
                new Ingredient[]{
                        new Ingredient("liquidslimetile", 1),
                        new Ingredient("lifequartz", 3)
                }
        ).showBefore("Napalm").setCraftingCategory("fuel").onCrafted((event) -> {
            boolean found = false;
            for (int i = 0; i < event.itemsUsed.get(0).inventory.streamSlots().count(); i++) {
                if (event.itemsUsed.get(0).inventory.getItem(i).item.getStringID().equalsIgnoreCase("bucket")) {
                    event.itemsUsed.get(0).inventory.getItem(i).setAmount(event.itemsUsed.get(0).inventory.getItem(i).getAmount()+1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (int i = 0; i < event.itemsUsed.get(0).inventory.streamSlots().count(); i++) {
                    if (event.itemsUsed.get(0).inventory.isSlotClear(i)) {
                        event.itemsUsed.get(0).inventory.setItem(i,new InventoryItem("bucket"));
                        break;
                    }
                }
            }
        }));

        //FLAMETHROWER
        Recipes.registerModRecipe(new Recipe(
                "Flamethrower",
                1,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        //new Ingredient("Canister", 1),
                        new Ingredient("demonicbar", 30),
                        new Ingredient("tungstenbar", 10),
                        new Ingredient("Mechanical_Parts_Good",4)
                }
        ).setCraftingCategory("flame"));

        //LAUNCHER
        Recipes.registerModRecipe(new Recipe(
                "Grenade_Launcher_Shell",
                25,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                        new Ingredient("tungstenbar", 1),
                        new Ingredient("Gunpowder", 10)
                }
        ).setCraftingCategory("shells"));
        Recipes.registerModRecipe(new Recipe(
                "Grenade_Launcher_Mine_Shell",
                5,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                        new Ingredient("tungstenbar", 1),
                        new Ingredient("ironbomb", 5)
                }
        ).showAfter("Grenade_Launcher_Shell").setCraftingCategory("shells"));
        Recipes.registerModRecipe(new Recipe(
                "Grenade_Launcher_Proxy_Shell",
                25,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                        new Ingredient("tungstenbar", 1),
                        new Ingredient("wire", 5),
                        new Ingredient("Gunpowder", 10)
                }
        ).showAfter("Grenade_Launcher_Mine_Shell").setCraftingCategory("shells"));
        Recipes.registerModRecipe(new Recipe(
                "Grenade_Launcher_Fire_Shell",
                25,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                        new Ingredient("tungstenbar", 1),
                        new Ingredient("firemone", 5),
                        new Ingredient("Gunpowder", 15)
                }
        ).showAfter("Grenade_Launcher_Proxy_Shell").setCraftingCategory("shells"));
        Recipes.registerModRecipe(new Recipe(
                "Grenade_Launcher",
                1,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("tungstenbar", 10),
                        new Ingredient("ancientfossilbar", 20),
                        new Ingredient("Mechanical_Parts_Good",6)
                }
        ).setCraftingCategory("explosive"));

        //FARM
        Recipes.registerModRecipe(new Recipe(
                "SeedGun",
                1,
                RecipeTechRegistry.IRON_ANVIL,
                new Ingredient[]{
                        new Ingredient("anylog", 20),
                        new Ingredient("Gunpowder", 10),
                        new Ingredient("ironbar",10)
                }
        ).setCraftingCategory("farm"));
        Recipes.registerModRecipe(new Recipe(
                "SeedGunShotgun",
                1,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("SeedGun", 1),
                        new Ingredient("myceliumbar", 15),
                        new Ingredient("Mechanical_Parts_Good", 3)
                }
        ).showBefore("SeedGun").setCraftingCategory("farm"));
        Recipes.registerModRecipe(new Recipe(
                "SeedGunMega",
                1,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("SeedGun", 1),
                        new Ingredient("bioessence", 15),
                        new Ingredient("Mechanical_Parts_Great", 2)
                }
        ).showBefore("SeedGunShotgun").setCraftingCategory("farm"));
        Recipes.registerModRecipe(new Recipe(
                "ProduceCannon",
                1,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("tungstenbar", 10),
                        new Ingredient("Gunpowder", 30),
                        new Ingredient("Mechanical_Parts_Good",2)
                }
        ).setCraftingCategory("farm"));

        //MATERIALS
        Recipes.registerModRecipe(new Recipe(
                "Mechanical_Parts",
                1,
                RecipeTechRegistry.DEMONIC_ANVIL,
                new Ingredient[]{
                        new Ingredient("copperbar", 5),
                        new Ingredient("ironbar", 5)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "Mechanical_Parts_Good",
                1,
                RecipeTechRegistry.TUNGSTEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("Mechanical_Parts", 2),
                        new Ingredient("tungstenbar", 5)
                }
        ).showBefore("Mechanical_Parts"));
        Recipes.registerModRecipe(new Recipe(
                "Mechanical_Parts_Great",
                1,
                RecipeTechRegistry.FALLEN_ANVIL,
                new Ingredient[]{
                        new Ingredient("Mechanical_Parts_Good", 2),
                        new Ingredient("shadowessence", 3),
                        new Ingredient("cryoessence", 3),
                        new Ingredient("bioessence", 3)
                }
        ).showBefore("Mechanical_Parts_Good"));

        //AMOBAGS
        Recipes.registerModRecipe(new Recipe(
                "AmmoPouchPlus",
                1,
                RecipeTechRegistry.DEMONIC_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("ammopouch", 1),
                        new Ingredient("Mechanical_Parts_Good", 1)
                }
        ).setCraftingCategory("equipment").onCrafted((event) -> {
            event.itemsUsed.stream().filter((item) -> {
                return item.invItem.item.getStringID().equals("ammopouch");
            }).findFirst().ifPresent((previousPouch) -> {
                event.resultItem.setGndData(previousPouch.invItem.getGndData());
                event.resultItem.setGndData(event.resultItem.getGndData().setBoolean("pickupDisabled",true));
            });
        }).showBefore("woodpickaxe").setCraftingCategory("equipment"));
        Recipes.registerModRecipe(new Recipe(
                "AmmoBagPlus",
                1,
                RecipeTechRegistry.FALLEN_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("AmmoPouchPlus", 1),
                        new Ingredient("Mechanical_Parts_Great", 2)
                }
        ).setCraftingCategory("equipment").showBefore("ammobag").onCrafted((event) -> {
            event.itemsUsed.stream().filter((item) -> {
                return item.invItem.item.getStringID().equals("AmmoPouchPlus");
            }).findFirst().ifPresent((previousPouch) -> {
                event.resultItem.setGndData(previousPouch.invItem.getGndData());
            });
        }).setCraftingCategory("equipment"));
    }
}
