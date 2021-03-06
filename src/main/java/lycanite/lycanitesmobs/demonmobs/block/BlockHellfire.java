package lycanite.lycanitesmobs.demonmobs.block;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Random;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ClientProxy;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.info.ItemInfo;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockHellfire extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockHellfire() {
		super(Material.fire);
		
		// Properties:
		this.group = DemonMobs.group;
		this.blockName = "hellfire";
		this.setup();
		
		// Stats:
		this.tickRate = ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Hellfire", true) ? 200 : 20;
		this.removeOnTick = false;
		this.loopTicks = true;
		this.canBeCrushed = true;
		
		this.noEntityCollision = true;
		this.noBreakCollision = false;
		this.isOpaque = false;
		
		this.setLightOpacity(1);
        this.setLightLevel(0.8F);
	}
    
    
	// ==================================================
	//                     Ticking
	// ==================================================
	// ========== Tick Rate ==========
    @Override
    public int tickRate(World par1World) {
        return 30;
    }
    
    // ========== Tick Update ==========
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
    	// ========== Main Fire Logic ==========
		// If Hellfire is disabled, use regular fire instead:
		if(!ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Hellfire"))
    		world.setBlock(x, y, z, Blocks.fire);
		
		// Remove if the doFireTick rule is false:
		if(!world.getGameRules().getGameRuleBooleanValue("doFireTick") && ItemInfo.removeOnNoFireTick) {
			world.setBlockToAir(x, y, z);
			return;
		}
        else if(!world.getGameRules().getGameRuleBooleanValue("doFireTick") && !ItemInfo.removeOnNoFireTick)
            return;

		
		Block base = world.getBlock(x, y - 1, z);
		
		// Take Over Replaceable Blocks:
		if(base == Blocks.snow_layer || base == Blocks.tallgrass) {
			world.setBlock(x, y - 1, z, this);
            world.setBlockToAir(x, y, z);
            return;
		}
		
        boolean onFireFuel = (base != null && base.isFireSource(world, x, y - 1, z, ForgeDirection.UP));

        if(!this.canPlaceBlockAt(world, x, y, z)) {
            world.setBlockToAir(x, y, z);
        }
        
        // Distinguish In Rain:
        if(!onFireFuel && world.isRaining() && (world.canLightningStrikeAt(x, y, z) || world.canLightningStrikeAt(x - 1, y, z) || world.canLightningStrikeAt(x + 1, y, z) || world.canLightningStrikeAt(x, y, z - 1) || world.canLightningStrikeAt(x, y, z + 1))) {
            world.setBlockToAir(x, y, z);
            return;
        }
        
        
        // ========== Base Logic ==========
		super.updateTick(world, x, y, z, random);
		
		
		// ========== Main Fire Logic Continued ==========
        int metadata = world.getBlockMetadata(x, y, z);
        if(metadata < 15) {
            world.setBlockMetadataWithNotify(x, y, z, metadata + random.nextInt(3) / 2, 4);
        }
        
		// Turn to air if no neighbor blocks can burn and this block is not on a solid block.
        if(!onFireFuel && !this.canNeighborBurn(world, x, y, z)) {
            if(!world.doesBlockHaveSolidTopSurface(world, x, y - 1, z) || metadata > 3) {
                world.setBlockToAir(x, y, z);
                return;
            }
        }
        
        // Random Chance of Fizzling Out (Uses metadata):
        if(!onFireFuel && !this.canBlockBurn(world, x, y - 1, z, ForgeDirection.UP) && metadata == 15 && random.nextInt(6) == 0) {
            world.setBlockToAir(x, y, z);
            return;
        }
        
        // Attempt To Spread:
        boolean humid = world.isBlockHighHumidity(x, y, z);
        byte humdity = 0;
        //if(humid)
        	//humdity = -50;
        int burnChance = 50;
        int burnChanceSide = burnChance + 50;
        this.tryCatchFire(world, x + 1, y, z, burnChanceSide + humdity, random, metadata, ForgeDirection.WEST );
        this.tryCatchFire(world, x - 1, y, z, burnChanceSide + humdity, random, metadata, ForgeDirection.EAST );
        this.tryCatchFire(world, x, y - 1, z, burnChance + humdity, random, metadata, ForgeDirection.UP   );
        this.tryCatchFire(world, x, y + 1, z, burnChance + humdity, random, metadata, ForgeDirection.DOWN );
        this.tryCatchFire(world, x, y, z - 1, burnChanceSide + humdity, random, metadata, ForgeDirection.SOUTH);
        this.tryCatchFire(world, x, y, z + 1, burnChanceSide + humdity, random, metadata, ForgeDirection.NORTH);
        
        for(int targetX = x - 1; targetX <= x + 1; ++targetX) {
            for(int targetZ = z - 1; targetZ <= z + 1; ++targetZ) {
                for(int targetY = y - 1; targetY <= y + 4; ++targetY) {
                    if(targetX != x || targetY != y || targetZ != z) {
                        int fireResistance = 100;

                        if(targetY > y + 1) {
                        	fireResistance += (targetY - (y + 1)) * 100;
                        }

                        int fireSpread = this.getChanceOfNeighborsEncouragingFire(world, targetX, targetY, targetZ);

                        if(fireSpread > 0) {
                            int fireChance = (fireSpread + 10 + world.difficultySetting.getDifficultyId() * 7) / (metadata + 30);
                            
                            if(humid)
                            	fireChance /= 2;
                            
                            if(fireChance > 0 && random.nextInt(fireResistance) <= fireChance && (!world.isRaining() || !world.canLightningStrikeAt(targetX, targetY, targetZ)) && !world.canLightningStrikeAt(targetX - 1, targetY, z) && !world.canLightningStrikeAt(targetX + 1, targetY, targetZ) && !world.canLightningStrikeAt(targetX, targetY, targetZ - 1) && !world.canLightningStrikeAt(targetX, targetY, targetZ + 1)) {
                                int k2 = metadata + random.nextInt(5) / 4;

                                if(k2 > 15) {
                                    k2 = 15;
                                }
                                
                                world.setBlock(targetX, targetY, targetZ, this, k2, 3);
                            }
                        }
                    }
                }
            }
        }
    }
    
    // ========== High Update Priority ==========
    // Setting this to true could cause lag if there's a lot of fire!
    @Override
    public boolean func_149698_L() {
    	return false;
    }


	// ==================================================
	//                       Fire
	// ==================================================
    // ========== Can Block Burn ==========
    public boolean canBlockBurn(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return world.getBlock(x, y, z).isFlammable(world, x, y, z, face);
    }
    
    // ========== Can Neighbor Burn ==========
    public boolean canNeighborBurn(World world, int x, int y, int z) {
        return this.canBlockBurn(world, x + 1, y, z, ForgeDirection.WEST ) ||
               this.canBlockBurn(world, x - 1, y, z, ForgeDirection.EAST ) ||
               this.canBlockBurn(world, x, y - 1, z, ForgeDirection.UP   ) ||
               this.canBlockBurn(world, x, y + 1, z, ForgeDirection.DOWN ) ||
               this.canBlockBurn(world, x, y, z - 1, ForgeDirection.SOUTH) ||
               this.canBlockBurn(world, x, y, z + 1, ForgeDirection.NORTH);
    }
    
    // ========== Try To Catch Block On Fire ===========
    private void tryCatchFire(World world, int x, int y, int z, int chance, Random random, int metadata, ForgeDirection face) {
        int flammability = 0;
        Block block = world.getBlock(x, y, z);
        if(block != null) {
        	flammability = block.getFlammability(world, x, y, z, face);
        }

        if(random.nextInt(chance) < flammability / 8) {
            boolean flag = world.getBlock(x, y, z) == Blocks.tnt;

            if (random.nextInt(metadata + 10) < 5 && !world.canLightningStrikeAt(x, y, z)) {
                int k1 = metadata + random.nextInt(5) / 4;

                if (k1 > 15) {
                    k1 = 15;
                }

                world.setBlock(x, y, z, this, k1, 3);
            }
            else {
            	world.setBlockToAir(x, y, z);
            }

            if(flag) {
                Blocks.tnt.onBlockDestroyedByPlayer(world, x, y, z, 1);
            }
        }
    }
    
    // ========== Get Chance of Neighbors Encouraging Fire ==========
    public int getChanceOfNeighborsEncouragingFire(World world, int x, int y, int z) {
        byte initialChance = 0;
        if(!world.isAirBlock(x, y, z))
            return 0;
        
        int fireChance = initialChance;
        fireChance = this.getChanceToEncourageFire(world, x + 1, y, z, fireChance, WEST );
        fireChance = this.getChanceToEncourageFire(world, x - 1, y, z, fireChance, EAST );
        fireChance = this.getChanceToEncourageFire(world, x, y - 1, z, fireChance, UP   );
        fireChance = this.getChanceToEncourageFire(world, x, y + 1, z, fireChance, DOWN );
        fireChance = this.getChanceToEncourageFire(world, x, y, z - 1, fireChance, SOUTH);
        fireChance = this.getChanceToEncourageFire(world, x, y, z + 1, fireChance, NORTH);
        return fireChance;
    }

    // ========== Get Chance To Encoure Fire ==========
    public int getChanceToEncourageFire(IBlockAccess world, int x, int y, int z, int oldChance, ForgeDirection face) {
        int newChance = world.getBlock(x, y, z).getFireSpreadSpeed(world, x, y, z, face);
        return (newChance > oldChance ? newChance : oldChance);
    }
    

	// ==================================================
	//                       Break
	// ==================================================
	@Override
	public Item getItemDropped(int metadata, Random random, int fortune) {
		return ObjectManager.getItem("HellfireCharge");
	}
	
	@Override
	public int damageDropped(int metadata) {
		return 0;
	}
    
	@Override
	public int quantityDropped(Random par1Random) {
        return 0;
    }
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
		if(entity instanceof EntityItem && ((EntityItem)entity).getEntityItem() != null)
    		if(((EntityItem)entity).getEntityItem().getItem() == ObjectManager.getItem("hellfirecharge"))
    			return;
		if(entity.isImmuneToFire())
			return;
    	entity.attackEntityFrom(DamageSource.lava, 2);
		entity.setFire(5);
	}
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
    	if(random.nextInt(24) == 0)
        	world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("hellfire"), 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
    	
        int l;
        float f;
        float f1;
        float f2;

        for(l = 0; l < 12; ++l) {
            f = (float)x + random.nextFloat();
            f1 = (float)y + random.nextFloat() * 0.5F;
            f2 = (float)z + random.nextFloat();
            //TODO EntityParticle particle = new EntityParticle(world, f, f1, f2, "hellfire", this.mod);
            world.spawnParticle("reddust", (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
        }
    }
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    	AssetManager.addIconGroup(blockName, this.group, new String[] {"hellfire_layer_0", "hellfire_layer_1"}, iconRegister);
    }
    
    // ========== Get Icon from Side and Meta ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int par1, int par2) {
        return AssetManager.getIconGroup(blockName)[0];
    }

    // ========== Get Render Type ==========
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return ClientProxy.RENDER_ID;
    }
    
    // ========== Render As Normal ==========
 	@Override
 	public boolean renderAsNormalBlock() {
 		return false;
 	}
}