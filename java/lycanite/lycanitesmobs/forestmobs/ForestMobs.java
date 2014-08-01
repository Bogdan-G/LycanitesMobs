package lycanite.lycanitesmobs.forestmobs;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.api.item.ItemTreat;
import lycanite.lycanitesmobs.forestmobs.entity.EntityArisaur;
import lycanite.lycanitesmobs.forestmobs.entity.EntityEnt;
import lycanite.lycanitesmobs.forestmobs.entity.EntityShambler;
import lycanite.lycanitesmobs.forestmobs.entity.EntityTrent;
import lycanite.lycanitesmobs.forestmobs.item.ItemForestEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
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

@Mod(modid = ForestMobs.modid, name = ForestMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class ForestMobs {
	
	public static final String modid = "forestmobs";
	public static final String name = "Lycanites Forest Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static ForestMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.forestmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.forestmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Forest Mobs");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("forestegg", new ItemForestEgg());
		
		ItemCustomFood rawMeat =  new ItemCustomFood("arisaurmeatraw", group, 2, 0.5F);
		if(ObjectManager.getPotionEffect("paralysis") != null)
			rawMeat.setPotionEffect(ObjectManager.getPotionEffect("paralysis").id, 10, 2, 0.8F);
		ObjectManager.addItem("arisaurmeatraw", rawMeat);
		ObjectLists.addItem("vegetables", ObjectManager.getItem("arisaurmeatraw"));
		
		ObjectManager.addItem("arisaurmeatcooked", new ItemCustomFood("arisaurmeatcooked", group, 6, 0.7F));
		ObjectLists.addItem("vegetables", ObjectManager.getItem("arisaurmeatcooked"));
		
		ObjectManager.addItem("paleosalad", new ItemCustomFood("paleosalad", group, 6, 0.7F).setPotionEffect(Potion.field_76434_w.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16)); // Health Boost
		ObjectLists.addItem("vegetables", ObjectManager.getItem("paleosalad"));

		ObjectManager.addItem("shamblertreat", new ItemTreat("shamblertreat", group));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("forestegg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "ent", EntityEnt.class, 0x997700, 0x00FF22)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "trent", EntityTrent.class, 0x663300, 0x00AA11)
		        .setPeaceful(false).setSummonable(false).setSummonCost(6);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(1).setAreaLimit(2).setGroupLimits(1, 1);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "shambler", EntityShambler.class, 0xDDFF22, 0x005511)
		        .setPeaceful(false).setSummonable(false).setSummonCost(4);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(4).setAreaLimit(6).setGroupLimits(1, 2);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "arisaur", EntityArisaur.class, 0x008800, 0x00FF00)
		        .setPeaceful(true).setSummonable(false).setSummonCost(2);
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(10).setAreaLimit(12).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		//ObjectManager.addProjectile("Template", EntityTemplate.class, Item.templateCharge, new DispenserBehaviorPoisonRay());
		
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
		if(config.getBool("Vanilla Spawning", "Edit Vanilla Spawning", true, "If true, some vanilla spawns in this biome will be removed, note that vanilla mobs should still be easy to find, only they will be more biome specific.")) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("paleosalad"), 1, 0),
				new Object[] {
					Blocks.leaves,
					Items.carrot,
					ObjectManager.getItem("arisaurmeatcooked")
				}
			));
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("paleosalad"), 1, 0),
				new Object[] {
					Blocks.leaves2,
					Items.carrot,
					ObjectManager.getItem("arisaurmeatcooked")
				}
			));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("shamblertreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("arisaurmeatcooked"),
				Character.valueOf('B'), Items.reeds
			}));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("arisaurmeatraw"), new ItemStack(ObjectManager.getItem("arisaurmeatcooked"), 1), 0.5f);
	}
}
