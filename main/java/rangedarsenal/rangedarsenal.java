package rangedarsenal;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.*;
import necesse.engine.sound.GameMusic;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.ZombieMob;
import necesse.entity.mobs.hostile.ZombieArcherMob;
import necesse.entity.mobs.hostile.CrawlingZombieMob;
import necesse.entity.mobs.hostile.EnchantedCrawlingZombieMob;
import necesse.entity.mobs.hostile.EnchantedZombieMob;
import necesse.entity.mobs.hostile.EnchantedZombieArcherMob;
import necesse.entity.mobs.hostile.SwampZombieMob;
import necesse.entity.mobs.hostile.bosses.PestWardenHead;
import necesse.engine.sound.gameSound.GameSound;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
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
import rangedarsenal.items.bullets.seeds.*;
import rangedarsenal.items.bullets.shells.*;
import rangedarsenal.items.materials.*;
import rangedarsenal.items.misc.*;
import rangedarsenal.items.weapons.*;
import rangedarsenal.objects.*;
import rangedarsenal.projectiles.bullets.*;
import rangedarsenal.projectiles.food.*;
import rangedarsenal.projectiles.fuel.*;
import rangedarsenal.projectiles.seed.*;
import rangedarsenal.projectiles.shells.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

@ModEntry
public class rangedarsenal {

    //TO-DO:
    /*
    FIXES
    -ammobag in crafting menu shows ammo counter
    -clean food gun if stack
    -onion knockback

    CONTENT
    -draw sniper crit chance under mouse?
    -web gun? Forgot that was a thing.
    -seed bag?
    -other mod compatibility (what mods?)
    -laser weps
    -incursion drop pools
    -range flamethrower rework
    -better ammo bag?
     */

    public static String[] SEED_AMMO_TYPES= new String[]{"grassseed","swampgrassseed","iceblossomseed","firemoneseed","sunflowerseed","wheatseed","cornseed","tomatoseed","cabbageseed","chilipepperseed","sugarbeetseed","eggplantseed","potatoseed","riceseed","carrotseed","onionseed","pumpkinseed","strawberryseed","kew_copper_seed","kew_iron_seed","kew_gold_seed","kew_tier_1_seed","kew_tier_2_seed"};
    public static String[] FOOD_AMMO_TYPES= new String[]{"apple","banana","blackberry","blueberry","cabbage","carrot","chilipepper","coconut","corn","eggplant","lemon","onion","potato","pumpkin","rice","strawberry","sugarbeet","tomato","wheat","coffeebeans"};

    public static GameSound proxyarm;
    public rangedarsenal(){
    }

    public void preInit() {
        ArrayList vanilla = new ArrayList(Arrays.asList(GunProjectileToolItem.NORMAL_AMMO_TYPES));
        vanilla.addAll(Arrays.asList("Standard_Bullet","Frozen_Bullet","Flame_Bullet","Blunt_Bullet","Leach_Bullet","Lightning_Bullet","Splintering_Bullet","Ruby_Bullet","Amethyst_Bullet","Sapphire_Bullet"));
        GunProjectileToolItem.NORMAL_AMMO_TYPES = (String[])vanilla.toArray(new String[0]);
        float oldMusicVolumeModifier = 0.6F;
        GameMusic HUBMUSICVN = MusicRegistry.registerMusic("hubmusic", "music/hubmusic", (GameMessage) null, new StaticMessage("Hubmusic"), new Color(125, 164, 45), new Color(47, 105, 12)).setVolumeModifier(oldMusicVolumeModifier);
    }

    public void init() {
        System.out.println("Ranged Arsenal loaded!");

        //OBJECTS
        RecipeTechRegistry.registerTech("GUNCRAFTING", "Loading Bench");
        ObjectRegistry.registerObject("LoadingBench", new LoadingBench(), 10f, true);

        //ITEMS
        ItemRegistry.registerItem("AmmoPouchPlus", new AmmoPouchPlus(), 100f, true);
        ItemRegistry.registerItem("AmmoBagPlus", new AmmoBagPlus(), 100f, true);
        ItemRegistry.replaceItem("ammopouch", new AmmoPouchFix(), 100f, true);
        ItemRegistry.replaceItem("potionpouch", new PotionPouchFix(), 100f, true);
        ItemRegistry.replaceItem("ammobag", new AmmoBagFix(), 100f, true);
        ItemRegistry.replaceItem("potionbag", new PotionBagFix(), 100f, true);

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
        ItemRegistry.replaceItem("frostbullet", new FrozenBullet(),0.1f,true,true);
        ItemRegistry.replaceItem("voidbullet", new NewVoidBullet(),0.1f,true,true);
        ItemRegistry.registerItem("Flame_Bullet", new FlameBullet(), 0.1f, true);
        ItemRegistry.registerItem("Blunt_Bullet", new BluntBullet(), 0.2f, true);
        ItemRegistry.registerItem("Lightning_Bullet", new LightningBullet(), 0.3f, true);
        ItemRegistry.registerItem("Splintering_Bullet", new SplinteringBullet(), 0.1f, true);
        ItemRegistry.replaceItem("crystalbullet", new SapphireBullet(), 0.2f, true);
        ItemRegistry.registerItem("Ruby_Bullet", new RubyBullet(), 0.2f, true);
        //ItemRegistry.registerItem("Amethyst_Bullet", new AmethystBullet(), 0.2f, true);

        //Seed Gun
        ItemRegistry.registerItem("Seed_Bullet", new SeedBullet(), 0f, false,false);
        ItemRegistry.registerItem("Grass_Seed_Bullet", new GrassSeedBullet(), 0f, false,false);
        ItemRegistry.registerItem("Fire_Seed_Bullet", new FireSeedBullet(), 0f, false,false);
        ItemRegistry.registerItem("Cold_Seed_Bullet", new ColdSeedBullet(), 0f, false,false);
        ItemRegistry.registerItem("Light_Seed_Bullet", new LightSeedBullet(), 0f, false,false);
        ItemRegistry.registerItem("Pierce_Seed_Bullet", new PierceSeedBullet(), 0f, false,false);
        ItemRegistry.registerItem("Metal_Seed_Bullet", new MetalSeedBullet(), 0f, false,false);
        ItemRegistry.registerItem("Essence_Seed_Bullet", new EssenceSeedBullet(), 0f, false,false);

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

        //Flamethrower
        ItemRegistry.registerItem("Gasoline", new GasolineBullet(), 0.2f, true);
        ItemRegistry.registerItem("CryoFlame", new CryoFlameBullet(), 0.2f, true);
        ItemRegistry.registerItem("Napalm", new NapalmBullet(), 0.2f, true);
        ItemRegistry.registerItem("MoltenSlime_Bullet", new MoltenSlimeBullet(), 0.2f, true);

        //launcher
        ItemRegistry.registerItem("Grenade_Launcher_Shell", new GrenadeLauncherShell(), 0.3f, true,true);
        ItemRegistry.registerItem("Grenade_Launcher_Mine_Shell", new GrenadeLauncherMineShell(), 0.3f, true,true);
        ItemRegistry.registerItem("Grenade_Launcher_Proxy_Shell", new GrenadeLauncherProxyShell(), 0.3f, true,true);
        //ItemRegistry.registerItem("Grenade_Launcher_Fire_Shell", new GrenadeLauncherFireBullet(), 0.5f, false,false);

        //WEAPONS
        ItemRegistry.registerItem("Junk_Pistol", new JunkPistol(), 90f, true);
        ItemRegistry.registerItem("Lever_Action_Rifle", new LeverActionRifle(), 100f, true);
        ItemRegistry.registerItem("Double_Barrel", new DoubleBarrel(), 200f, true);
        ItemRegistry.registerItem("Light_Machinegun", new LightMachinegun(), 300f, true);
        ItemRegistry.registerItem("Normal_Revolver", new NormalRevolver(), 300f, true);
        ItemRegistry.registerItem("Flamethrower", new Flamethrower(), 300f, true);
        ItemRegistry.registerItem("Range_Flamethrower", new RangeFlamethrower(), 300f, false,false);
        ItemRegistry.registerItem("Grenade_Launcher", new GrenadeLauncher(), 400f, true,true);
        ItemRegistry.replaceItem("sniperrifle", new SniperRework(),100f,true,true);
        ItemRegistry.replaceItem("shotgun", new ShotgunRework(),100f,true,true);
        ItemRegistry.replaceItem("cryoblaster", new CryoBlasterRework(),200f,true,true);
        ItemRegistry.registerItem("AWP", new AWP(),300f,true,true);
        ItemRegistry.registerItem("SeedGun", new SeedGun(),60f,true,true);
        ItemRegistry.registerItem("SeedGunShotgun", new SeedGunShotgun(),100f,true,true);
        ItemRegistry.registerItem("SeedGunMega", new SeedGunMega(),300f,true,true);
        ItemRegistry.registerItem("ProduceCannon", new ProduceCannon(),200f,true,true);
        ItemRegistry.registerItem("ProduceCannonMega", new ProduceCannonMega(),300f,true,true);
        ItemRegistry.registerItem("LightningRifle", new LightningRifle(),1f,false,false);
        ItemRegistry.registerItem("BeamRifle", new BeamRifle(),1f,false,false);
        ItemRegistry.replaceItem("shardcannon", new ShardCannonRework(),1500f,true,true);
        ItemRegistry.replaceItem("sapphirerevolver", new SapphireRevolverRework(),1500f,true,true);
        ItemRegistry.registerItem("shardcannonRED", new ShardCannonRED(),1f,false,false);
        ItemRegistry.replaceItem("machinegun", new MachinegunRework(),65f,true,true);


        //PROJECTILES
        ProjectileRegistry.registerProjectile("Frozen_Bullet_Projectile", FrozenBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Flame_Bullet_Projectile", FlameBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Blunt_Bullet_Projectile", BluntBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Leach_Bullet_Projectile", LeachBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Lightning_Bullet_Projectile", LightningBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Splintering_Bullet_Projectile", SplinteringBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("New_Void_Bullet_Projectile", NewVoidBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("LightningRifle_Bullet_Projectile", LightningRifleBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Sapphire_Bullet_Projectile", SapphireBulletProjectile.class,"crystalbullet","crystalbullet_shadow");
        ProjectileRegistry.registerProjectile("Ruby_Bullet_Projectile", RubyBulletProjectile.class,"rubybullet","crystalbullet_shadow");
        //ProjectileRegistry.registerProjectile("Amethyst_Bullet_Projectile", AmethystBulletProjectile.class,"amethystbullet","crystalbullet_shadow");
        ProjectileRegistry.registerProjectile("SapphireSplosion_Bullet_Projectile", SapphireSplosionBulletProjectile.class,"crystalbullet","crystalbullet_shadow");


        //seeds
        ProjectileRegistry.registerProjectile("Seed_Bullet_Projectile", SeedBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Grass_Seed_Bullet_Projectile", GrassSeedBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Cold_Seed_Bullet_Projectile", ColdSeedBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Fire_Seed_Bullet_Projectile", FireSeedBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Light_Seed_Bullet_Projectile", LightSeedBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Pierce_Seed_Bullet_Projectile", PierceSeedBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Metal_Seed_Bullet_Projectile", MetalSeedBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Essence_Seed_Bullet_Projectile", EssenceSeedBulletProjectile.class,"","");

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

        ProjectileRegistry.registerProjectile("Gasoline_Bullet_Projectile", GasolineBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("CryoFlame_Bullet_Projectile", CryoFlameBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("Napalm_Bullet_Projectile", NapalmBulletProjectile.class,"","");
        ProjectileRegistry.registerProjectile("MoltenSlime_Bullet_Projectile", MoltenSlimeBulletProjectile.class,"","");

        ProjectileRegistry.registerProjectile("Grenade_Launcher_Projectile", GrenadeLauncherProjectile.class,"Grenade_Launcher_Projectile","Grenade_Launcher_Projectile_shadow");
        ProjectileRegistry.registerProjectile("Grenade_Launcher_Mine_Projectile", GrenadeLauncherMineProjectile.class,"Grenade_Launcher_Mine_Projectile","Grenade_Launcher_Projectile_shadow");
        //ProjectileRegistry.registerProjectile("Grenade_Launcher_Fire_Projectile", GrenadeLauncherFireProjectile.class,"Grenade_Launcher_Projectile","Grenade_Launcher_Projectile_shadow");
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
        BuffRegistry.registerBuff("ShardCannonCooldownDebuff", new ShardCannonCooldownDebuff());

        //EVENTS
        LevelEventRegistry.registerEvent("SlimeSplosionEvent", SlimeSplosionEvent.class);
        LevelEventRegistry.registerEvent("GrenadeLauncherExplosionEvent", GrenadeLauncherExplosionEvent.class);
        LevelEventRegistry.registerEvent("GrenadeLauncherMineExplosionEvent", GrenadeLauncherMineExplosionEvent.class);
        //LevelEventRegistry.registerEvent("GrenadeLauncherFireExplosionEvent", GrenadeLauncherFireExplosionEvent.class);
        LevelEventRegistry.registerEvent("LightningJumperEvent", LightningJumperEvent.class);
        LevelEventRegistry.registerEvent("FruitBoomEvent", FruitBoomEvent.class);
        LevelEventRegistry.registerEvent("ShrapnelEvent", ShrapnelEvent.class);
        LevelEventRegistry.registerEvent("GrenadeLauncherProxyExplosionEvent", GrenadeLauncherProxyExplosionEvent.class);
        LevelEventRegistry.registerEvent("LightningRifleEvent", LightningRifleEvent.class);
        LevelEventRegistry.registerEvent("BeamRifleEvent", BeamRifleEvent.class);

    }
    public void initResources() {
        GameTexture LoadingBench = GameTexture.fromFile("objects/LoadingBench");
        proxyarm = GameSound.fromFile("activate_single_mono");
    }
    public void postInit() {

        //Add Niter
        ZombieMob.lootTable = new LootTable(ZombieMob.lootTable, new ChanceLootItem(0.35f, "Niter"));
        ZombieArcherMob.lootTable = new LootTable(ZombieMob.lootTable, new ChanceLootItem(0.5f, "Niter"));
        CrawlingZombieMob.lootTable = new LootTable(CrawlingZombieMob.lootTable, new ChanceLootItem(0.35f, "Niter"));
        EnchantedZombieMob.lootTable = new LootTable(EnchantedZombieMob.lootTable, new ChanceLootItem(0.35f, "Niter"));
        EnchantedZombieArcherMob.lootTable = new LootTable(EnchantedZombieArcherMob.lootTable, new ChanceLootItem(0.35f, "Niter"));
        EnchantedCrawlingZombieMob.lootTable = new LootTable(EnchantedCrawlingZombieMob.lootTable, new ChanceLootItem(0.35f, "Niter"));
        SwampZombieMob.lootTable = new LootTable(SwampZombieMob.lootTable, new ChanceLootItem(0.45f, "Niter"));

        //Replace livingshotty
        PestWardenHead.uniqueDrops.items.remove(2);
        PestWardenHead.uniqueDrops.items.add(2,(new LootItem("ProduceCannonMega")));

        //boss gun parts
        /*GameEvents.addListener(MobLootTableDropsEvent.class, new GameEventListener<MobLootTableDropsEvent>() {
            @Override
            public void onEvent(MobLootTableDropsEvent event) {
                if (event.mob.isBoss()) {
                    //Gamerandomchance isn't working for some reason
                    if (GameRandom.globalRandom.getIntBetween(0,100) >= 50) {
                        if (event.mob.getArmorFlat() < 39) {
                            event.drops.add(new InventoryItem("Gun_Parts", 5));
                        } else if (event.mob.getArmorFlat() > 20 && event.mob.getArmorFlat() < 39) {
                            event.drops.add(new InventoryItem("Gun_Parts", 3));
                        } else {
                            event.drops.add(new InventoryItem("Gun_Parts", 1));
                        }
                    }
                }
            }
        });*/

        CrateLootTable.basicCrate.items.add(new ChanceLootItem(0.20f, "simplebullet", GameRandom.globalRandom.getIntBetween(5, 8)));
        
        //Add recipes

        //STARTER
        Recipes.registerModRecipe(new Recipe(
                "SeedGun",
                1,
                RecipeTechRegistry.WORKSTATION,
                new Ingredient[]{
                        new Ingredient("anylog", 20),
                        new Ingredient("Gunpowder", 10),
                        new Ingredient("ironbar",10)
                }
        ).showAfter("woodstaff"));
        Recipes.registerModRecipe(new Recipe(
                "Junk_Pistol",
                1,
                RecipeTechRegistry.IRON_ANVIL,
                new Ingredient[]{
                        new Ingredient("copperbar", 14),
                        new Ingredient("ironbar",6)
                }
        ).showAfter("copperbow"));

        //EXTERNAL
        Recipes.registerModRecipe(new Recipe(
                "LoadingBench",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("Mechanical_Parts", 1),
                        new Ingredient("ironbar", 10),
                        new Ingredient("anylog", 10)
                }
        ).showAfter("advancedworkstation"));
        Recipes.registerModRecipe(new Recipe(
                "Gunpowder",
                10,
                RecipeTechRegistry.ALCHEMY,
                new Ingredient[]{
                        new Ingredient("Niter", 1),
                        new Ingredient("sandtile", 2)
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
        ).showAfter("bucket").showBefore("simplebullet").showBefore("cannonball"));
        Recipes.registerModRecipe(new Recipe(
                "Bullet_Casing",
                50,
                RecipeTechRegistry.IRON_ANVIL,
                new Ingredient[]{
                        new Ingredient("simplebullet", 50)
                }
        ).showAfter("bucket").showBefore("simplebullet").showBefore("cannonball"));
        Recipes.registerModRecipe(new Recipe(
                "simplebullet",
                100,
                RecipeTechRegistry.IRON_ANVIL,
                new Ingredient[]{
                        //new Ingredient("copperbar", 2),
                        new Ingredient("ironbar", 1),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showAfter("Bullet_Casing").showBefore("cannonball"));

        //GUNS
        Recipes.registerModRecipe(new Recipe(
                "handgun",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("ironbar", 10),
                        new Ingredient("Mechanical_Parts",2)
                }
        ));
        Recipes.registerModRecipe(new Recipe(
                "Lever_Action_Rifle",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("goldbar", 10),
                        new Ingredient("anylog", 5),
                        new Ingredient("Mechanical_Parts",2)
                }
        ).showAfter("handgun"));
        Recipes.registerModRecipe(new Recipe(
                "machinegun",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("ivybar", 15),
                        new Ingredient("demonicbar", 5),
                        new Ingredient("Mechanical_Parts",3)
                }
        ).showAfter("Lever_Action_Rifle"));
        Recipes.registerModRecipe(new Recipe(
                "sniperrifle",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("quartz", 20),
                        new Ingredient("demonicbar", 5),
                        new Ingredient("Mechanical_Parts",3)
                }
        ).showAfter("machinegun"));
        Recipes.registerModRecipe(new Recipe(
                "shotgun",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("voidshard", 15),
                        new Ingredient("demonicbar", 15),
                        new Ingredient("Mechanical_Parts",4)
                }
        ).showAfter("sniperrifle"));
        Recipes.registerModRecipe(new Recipe(
                "Normal_Revolver",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("ironbar", 20),
                        new Ingredient("tungstenbar", 10),
                        new Ingredient("Mechanical_Parts_Good",3)
                }
        ).showAfter("shotgun").showBefore("Light_Machinegun"));
        Recipes.registerModRecipe(new Recipe(
                "Light_Machinegun",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("glacialbar", 10),
                        new Ingredient("tungstenbar", 15),
                        new Ingredient("Mechanical_Parts_Good",5)
                }
        ).showAfter("shotgun").showBefore("Double_Barrel"));
        Recipes.registerModRecipe(new Recipe(
                "Double_Barrel",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("myceliumbar", 10),
                        new Ingredient("demonicbar", 3),
                        new Ingredient("anylog", 10),
                        new Ingredient("Mechanical_Parts_Good",4)
                }
        ).showAfter("shotgun").showBefore("AWP"));
        Recipes.registerModRecipe(new Recipe(
                "AWP",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("nightsteelbar", 10),
                        new Ingredient("demonicbar", 15),
                        new Ingredient("Mechanical_Parts_Great",3)
                }
        ).showAfter("shotgun").showBefore("Gasoline"));

        Recipes.registerModRecipe(new Recipe(
                "sapphirerevolver",
                1,
                RecipeTechRegistry.FALLEN_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("sapphire", 10),
                        new Ingredient("pearlescentdiamond", 10),
                        new Ingredient("omnicrystal",20)
                },
                false,
                (new GNDItemMap().setInt("upgradeLevel", 100))
        ).showAfter("gemstonelongsword").showBefore("shardcannon"));

        //GUN BULLETS
        Recipes.registerModRecipe(new Recipe(
                "simplebullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        //new Ingredient("copperbar", 2),
                        new Ingredient("ironbar", 1),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun").showBefore("Standard_Bullet"));
        Recipes.registerModRecipe(new Recipe(
                "Standard_Bullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("Gunpowder", 1),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun").showAfter("simplebullet"));
        Recipes.registerModRecipe(new Recipe(
                "bouncingbullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("halffish", 1),
                        new Ingredient("Gunpowder", 1),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun").showAfter("Standard_Bullet"));
        Recipes.registerModRecipe(new Recipe(
                "Frozen_Bullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("frostshard", 1),
                        new Ingredient("Gunpowder", 2),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun"));
        Recipes.registerModRecipe(new Recipe(
                "Flame_Bullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("firemone", 1),
                        new Ingredient("Gunpowder", 2),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun"));
        Recipes.registerModRecipe(new Recipe(
                "Splintering_Bullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("copperbar", 1),
                        new Ingredient("goldbar", 1),
                        new Ingredient("Gunpowder", 3),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun"));
        Recipes.registerModRecipe(new Recipe(
                "Blunt_Bullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("obsidian", 1),
                        new Ingredient("Gunpowder", 2),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun"));
        Recipes.registerModRecipe(new Recipe(
                "voidbullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("obsidian", 1),
                        new Ingredient("voidshard", 1),
                        new Ingredient("Gunpowder", 3),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun"));
        Recipes.registerModRecipe(new Recipe(
                "Lightning_Bullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("obsidian", 1),
                        new Ingredient("shadowessence", 3),
                        new Ingredient("Gunpowder", 3),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun"));
        Recipes.registerModRecipe(new Recipe(
                "Leach_Bullet",
                25,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("obsidian", 1),
                        new Ingredient("lifequartz", 10),
                        new Ingredient("alchemyshard", 25),
                        new Ingredient("Gunpowder", 5),
                        new Ingredient("Bullet_Casing", 25)
                }
        ).showBefore("handgun"));
        Recipes.registerModRecipe(new Recipe(
                "crystalbullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("sapphire", 3),
                        new Ingredient("alchemyshard", 15),
                        new Ingredient("Gunpowder", 5),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun"));
        Recipes.registerModRecipe(new Recipe(
                "Ruby_Bullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("ruby", 3),
                        new Ingredient("obsidian", 5),
                        new Ingredient("Gunpowder", 5),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun"));
       /* Recipes.registerModRecipe(new Recipe(
                "Amethyst_Bullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("amethyst", 3),
                        new Ingredient("voidshard", 5),
                        new Ingredient("Gunpowder", 5),
                        new Ingredient("Bullet_Casing", 100)
                }
        ).showBefore("handgun"));*/

        //FUEL
        Recipes.registerModRecipe(new Recipe(
                "Gasoline",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("lavatile", 1),
                        new Ingredient("firemone", 5)
                        //new Ingredient("Canister", 1)
                }
        ).showAfter("AWP"));
        Recipes.registerModRecipe(new Recipe(
                "CryoFlame",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("glacialshard", 2),
                        new Ingredient("iceblossom", 3),
                        new Ingredient("bucket", 1)
                }
        ).showAfter("Gasoline"));
        Recipes.registerModRecipe(new Recipe(
                "Napalm",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("lavatile", 1),
                        new Ingredient("primordialessence", 1)
                        //new Ingredient("Canister", 1)
                }
        ).showAfter("CryoFlame"));
        Recipes.registerModRecipe(new Recipe(
                "MoltenSlime_Bullet",
                100,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("liquidslimetile", 1),
                        new Ingredient("lifequartz", 3)
                        //new Ingredient("Canister", 1)
                }
        ).showAfter("Napalm"));

        //FLAMETHROWER
        Recipes.registerModRecipe(new Recipe(
                "Flamethrower",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        //new Ingredient("Canister", 1),
                        new Ingredient("demonicbar", 30),
                        new Ingredient("tungstenbar", 10),
                        new Ingredient("Mechanical_Parts_Good",4)
                }
        ).showAfter("MoltenSlime_Bullet"));

        //LAUNCHER
        Recipes.registerModRecipe(new Recipe(
                "Grenade_Launcher_Shell",
                10,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                        new Ingredient("tungstenbar", 1),
                        new Ingredient("Gunpowder", 10)
                }
        ).showAfter("Flamethrower"));
        Recipes.registerModRecipe(new Recipe(
                "Grenade_Launcher_Mine_Shell",
                10,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                        new Ingredient("tungstenbar", 1),
                        new Ingredient("ironbomb", 10)
                }
        ).showAfter("Grenade_Launcher_Shell"));
        Recipes.registerModRecipe(new Recipe(
                "Grenade_Launcher_Proxy_Shell",
                10,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("ironbar", 2),
                        new Ingredient("tungstenbar", 1),
                        new Ingredient("wire", 5),
                        new Ingredient("Gunpowder", 10)
                }
        ).showAfter("Grenade_Launcher_Mine_Shell"));
        Recipes.registerModRecipe(new Recipe(
                "Grenade_Launcher",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("tungstenbar", 10),
                        new Ingredient("ancientfossilbar", 20),
                        new Ingredient("Mechanical_Parts_Good",6)
                }
        ).showAfter("Grenade_Launcher_Proxy_Shell"));

        //FARM
        Recipes.registerModRecipe(new Recipe(
                "SeedGun",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("anylog", 20),
                        new Ingredient("Gunpowder", 10),
                        new Ingredient("ironbar",10)
                }
        ).showAfter("Grenade_Launcher").showBefore("SeedGunShotgun"));
        Recipes.registerModRecipe(new Recipe(
                "SeedGunShotgun",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("SeedGun", 3),
                        new Ingredient("myceliumbar", 15),
                        new Ingredient("Mechanical_Parts_Good", 3)
                }
        ).showAfter("Grenade_Launcher").showBefore("SeedGunMega"));
        Recipes.registerModRecipe(new Recipe(
                "SeedGunMega",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("SeedGun", 1),
                        new Ingredient("bioessence", 15),
                        new Ingredient("Mechanical_Parts_Great", 2)
                }
        ).showAfter("Grenade_Launcher"));
        Recipes.registerModRecipe(new Recipe(
                "ProduceCannon",
                1,
                RecipeTechRegistry.getTech("GUNCRAFTING"),
                new Ingredient[]{
                        new Ingredient("tungstenbar", 10),
                        new Ingredient("Gunpowder", 30),
                        new Ingredient("Mechanical_Parts_Good",2)
                }
        ).showAfter("Grenade_Launcher"));

        //MATERIALS
        Recipes.registerModRecipe(new Recipe(
                "Mechanical_Parts",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("copperbar", 5),
                        new Ingredient("ironbar", 5)
                }
        ).showAfter("LoadingBench"));
        Recipes.registerModRecipe(new Recipe(
                "Mechanical_Parts_Good",
                1,
                RecipeTechRegistry.ADVANCED_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("Mechanical_Parts", 2),
                        new Ingredient("tungstenbar", 5)
                }
        ).showAfter("Mechanical_Parts"));
        Recipes.registerModRecipe(new Recipe(
                "Mechanical_Parts_Great",
                1,
                RecipeTechRegistry.FALLEN_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("Mechanical_Parts_Good", 2),
                        new Ingredient("shadowessence", 3),
                        new Ingredient("cryoessence", 3),
                        new Ingredient("bioessence", 3)
                }
        ).showAfter("Mechanical_Parts_Good"));

        //AMOBAGS
        Recipes.registerModRecipe(new Recipe(
                "AmmoPouchPlus",
                1,
                RecipeTechRegistry.DEMONIC,
                new Ingredient[]{
                        new Ingredient("ammopouch", 1),
                        new Ingredient("Mechanical_Parts_Good", 1)
                }
        ).onCrafted((event) -> {
            event.itemsUsed.stream().filter((item) -> {
                return item.invItem.item.getStringID().equals("ammopouch");
            }).findFirst().ifPresent((previousPouch) -> {
                event.resultItem.setGndData(previousPouch.invItem.getGndData());
            });
        }).showAfter("chainshirt"));
        Recipes.registerModRecipe(new Recipe(
                "AmmoBagPlus",
                1,
                RecipeTechRegistry.FALLEN_WORKSTATION,
                new Ingredient[]{
                        new Ingredient("AmmoPouchPlus", 1),
                        new Ingredient("Mechanical_Parts_Great", 2)
                }
        ).showAfter("ammobag").onCrafted((event) -> {
            event.itemsUsed.stream().filter((item) -> {
                return item.invItem.item.getStringID().equals("AmmoPouchPlus");
            }).findFirst().ifPresent((previousPouch) -> {
                event.resultItem.setGndData(previousPouch.invItem.getGndData());
            });
        }));
    }
}
