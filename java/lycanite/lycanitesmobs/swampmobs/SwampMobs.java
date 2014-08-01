package lycanite.lycanitesmobs.swampmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.api.item.ItemTreat;
import lycanite.lycanitesmobs.swampmobs.block.BlockPoisonCloud;
import lycanite.lycanitesmobs.swampmobs.dispenser.DispenserBehaviorPoisonRay;
import lycanite.lycanitesmobs.swampmobs.dispenser.DispenserBehaviorVenomShot;
import lycanite.lycanitesmobs.swampmobs.entity.EntityAspid;
import lycanite.lycanitesmobs.swampmobs.entity.EntityDweller;
import lycanite.lycanitesmobs.swampmobs.entity.EntityEttin;
import lycanite.lycanitesmobs.swampmobs.entity.EntityEyewig;
import lycanite.lycanitesmobs.swampmobs.entity.EntityGhoulZombie;
import lycanite.lycanitesmobs.swampmobs.entity.EntityLurker;
import lycanite.lycanitesmobs.swampmobs.entity.EntityPoisonRay;
import lycanite.lycanitesmobs.swampmobs.entity.EntityPoisonRayEnd;
import lycanite.lycanitesmobs.swampmobs.entity.EntityRemobra;
import lycanite.lycanitesmobs.swampmobs.entity.EntityVenomShot;
import lycanite.lycanitesmobs.swampmobs.item.ItemPoisonGland;
import lycanite.lycanitesmobs.swampmobs.item.ItemScepterPoisonRay;
import lycanite.lycanitesmobs.swampmobs.item.ItemScepterVenomShot;
import lycanite.lycanitesmobs.swampmobs.item.ItemSwampEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = SwampMobs.modid, name = SwampMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class SwampMobs {
	
	public static final String modid = "swampmobs";
	public static final String name = "Lycanites Swamp Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static SwampMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.swampmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.swampmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Swamp Mobs")
                .setDimensions("0, 7").setBiomes("SWAMP, SPOOKY");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("swampegg", new ItemSwampEgg());
		
		ObjectManager.addItem("aspidmeatraw", new ItemCustomFood("aspidmeatraw", group, 2, 0.5F).setPotionEffect(Potion.poison.id, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("aspidmeatraw"));
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("aspidmeatraw"));
		
		ObjectManager.addItem("aspidmeatcooked", new ItemCustomFood("aspidmeatcooked", group, 6, 0.7F));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("aspidmeatcooked"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("aspidmeatcooked"));
		
		ObjectManager.addItem("mosspie", new ItemCustomFood("mosspie", group, 6, 0.7F).setPotionEffect(Potion.regeneration.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("mosspie"));

		ObjectManager.addItem("lurkertreat", new ItemTreat("lurkertreat", group));
		ObjectManager.addItem("eyewigtreat", new ItemTreat("eyewigtreat", group));
		
		ObjectManager.addItem("poisongland", new ItemPoisonGland());
		ObjectManager.addItem("poisonrayscepter", new ItemScepterPoisonRay());
		ObjectManager.addItem("venomshotscepter", new ItemScepterVenomShot());
		
		// ========== Create Blocks ==========
		AssetManager.addSound("poisoncloud", group, "block.poisoncloud");
		ObjectManager.addBlock("poisoncloud", new BlockPoisonCloud());
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		group.loadSpawningFromConfig();
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("swampegg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "ghoulzombie", EntityGhoulZombie.class, 0x009966, 0xAAFFDD)
		        .setPeaceful(false).setSummonable(false).setSummonCost(2);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "dweller", EntityDweller.class, 0x009922, 0x994499)
		        .setPeaceful(false).setSummonable(true).setSummonCost(1);
		newMob.spawnInfo.setSpawnTypes("WATERCREATURE")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "ettin", EntityEttin.class, 0x669900, 0xFF6600)
		        .setPeaceful(false).setSummonable(false).setSummonCost(6);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(3).setAreaLimit(3).setGroupLimits(1, 2);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "lurker", EntityLurker.class, 0x009900, 0x99FF00)
		        .setPeaceful(false).setSummonable(false).setSummonCost(4);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(6).setAreaLimit(5).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "eyewig", EntityEyewig.class, 0x000000, 0x009900)
		        .setPeaceful(false).setSummonable(false).setSummonCost(4);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(3).setAreaLimit(5).setGroupLimits(1, 1);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "aspid", EntityAspid.class, 0x009944, 0x446600)
		        .setPeaceful(true).setSummonable(false).setSummonCost(2);
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(12).setAreaLimit(10).setGroupLimits(1, 5).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "remobra", EntityRemobra.class, 0x440066, 0xDD00FF)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(4).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("poisonray", EntityPoisonRay.class, Items.fermented_spider_eye, new DispenserBehaviorPoisonRay());
		ObjectManager.addProjectile("poisonrayend", EntityPoisonRayEnd.class);
		ObjectManager.addProjectile("venomshot", EntityVenomShot.class, ObjectManager.getItem("poisongland"), new DispenserBehaviorVenomShot());
		
		// ========== Register Models ==========
		proxy.registerModels();
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		ConfigBase config = ConfigBase.getConfig(group, "spawning");
		
		// ========== Remove Vanilla Spawns ==========
		BiomeGenBase[] biomes = group.biomes;
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntityCow.class, EnumCreatureType.creature, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("poisonrayscepter"), 1, 0),
				new Object[] { "CPC", "CRC", "CRC",
				Character.valueOf('C'), Items.fermented_spider_eye,
				Character.valueOf('P'), ObjectManager.getItem("poisongland"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("venomshotscepter"), 1, 0),
				new Object[] { "CPC", "CRC", "CRC",
				Character.valueOf('C'), Items.rotten_flesh,
				Character.valueOf('P'), ObjectManager.getItem("poisongland"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("mosspie"), 1, 0),
				new Object[] {
					Blocks.vine,
					Blocks.red_mushroom,
					ObjectManager.getItem("aspidmeatcooked")
				}
			));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("mosspie"), 1, 0),
				new Object[] {
					Blocks.vine,
					Blocks.brown_mushroom,
					ObjectManager.getItem("aspidmeatcooked")
				}
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("lurkertreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("aspidmeatcooked"),
				Character.valueOf('B'), Items.bone
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("eyewigtreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("poisongland"),
				Character.valueOf('B'), Items.bone
			}));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("aspidmeatraw"), new ItemStack(ObjectManager.getItem("aspidmeatcooked"), 1), 0.5f);
	}
}
