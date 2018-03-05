package lycanite.lycanitesmobs.api.block;

import java.util.List;
import java.util.Random;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBase extends Block {
	
	// Properties:
	public GroupInfo group;
	public String blockName = "BlockBase";
	
	// Stats:
	/** If set to a value above 0, the block will update on the specified number of ticks. **/
	public int tickRate = 0;
	/** If true, this block will be set to air on it's first tick, useful for blocks that despawn over time like fire. **/
	public boolean removeOnTick = false;
	/** If true after performing a tick update another tick update will be scheduled thus creating a loop. **/
	public boolean loopTicks = true;
	/** Will falling blocks such as sand or gravel destroy this block if they land on it? */
	public boolean canBeCrushed = false;
	
	/** If true, this block can be walked through. **/
	public boolean noEntityCollision = false;
	/** If true, this block cannot be broken or even hit like a solid block. **/
	public boolean noBreakCollision = false;
	/** Whether or not light can pass through this block, useful for blocks such as glass. Setting this to false will also stop blocks behind it from rendering. **/
	public boolean isOpaque = true;
	
	// Rendering:
	public static enum RENDER_TYPE {
		NONE(-1), NORMAL(0), CROSS(1), TORCH(2), FIRE(3), FLUID(4); // More found on RenderBlock, or use Client Proxies for custom renderers.
		public final int id;
	    private RENDER_TYPE(int value) { this.id = value; }
	    public int getValue() { return id; }
	}
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public BlockBase(Material material, GroupInfo group, String name) {
        super(material);
        this.group = group;
        this.blockName = name;
        this.setup();
    }

	public BlockBase(Material material) {
		super(material);
	}
	
	/** Should be called by a child class once the blockName and other important variables are set, kind of a late construct. **/
	public void setup() {
		this.setBlockName(this.blockName);
        this.setBlockTextureName(this.blockName.toLowerCase());
	}


    // ==================================================
    //                      Info
    // ==================================================
    public String getDescription(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
        return StatCollector.translateToLocal("tile." + this.blockName + ".description");
    }
	
	
	// ==================================================
	//                      Place
	// ==================================================
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		// Initial Block Ticking:
		if(this.tickRate > 0)
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	}
	
	
	// ==================================================
	//                      Break
	// ==================================================
	//========== Drops ==========
	@Override
	public Item getItemDropped(int breakID, Random random, int zero) {
        return super.getItemDropped(breakID, random, zero);
	}
	
	@Override
	public int damageDropped(int breakMetadata) {
		return 0;
	}
    
	@Override
	public int quantityDropped(Random random) {
        return 1;
    }
	

	// ==================================================
	//                   Block Updates
	// ==================================================
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
        // Crushable:
		Block block = world.getBlock(x, y, z);
		if(this.canBeCrushed)
			if(block == Blocks.sand || block == Blocks.gravel)
	        	world.setBlockToAir(x, y, z);
    }
    
    
	// ==================================================
	//                     Ticking
	// ==================================================
    // ========== Tick Rate ==========
    @Override
    public int tickRate(World world) {
    	return this.tickRate;
    }

    // ========== Tick Update ==========
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
		if(world.isRemote)
			return;
		
		// Remove On Tick:
		if(this.removeOnTick)
			world.setBlockToAir(x, y, z);
		
		// Looping Tick:
		else if(this.tickRate > 0 && this.loopTicks)
			world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
    }
    
    // ========== High Update Priority ==========
    @Override
    public boolean func_149698_L() {
    	return super.func_149698_L();
    }
    
    // ========== Should Tick ==========
    @Override
    public boolean getTickRandomly() {
        return this.tickRate > 0;
    }
    
    
	// ==================================================
	//                    Collision
	// ==================================================
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
    	if(this.noEntityCollision)
    		return null;
        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
    }
    
    // ========== Punch Collision ==========
    @Override
    public boolean isCollidable() {
        return !this.noBreakCollision;
    }
    
    // ========== Is Opaque ==========
    @Override
    public boolean isOpaqueCube() {
        return this.isOpaque;
    }
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    	AssetManager.addIcon(this.blockName, this.group, this.getTextureName(), iconRegister);
    }
    
    // ========== Get Icon from Side and Metadata ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int metadata) {
        return AssetManager.getIcon(blockName);
    }
    
    // ========== Get Sub Icon ==========
    @SideOnly(Side.CLIENT)
    public IIcon getSubIcon(int subID) {
        return AssetManager.getIconGroup(blockName)[subID];
    }

    // ========== Get Render Type ==========
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() {
        return super.getRenderType();
    }
    
    // ========== Render As Normal ==========
 	@Override
 	public boolean renderAsNormalBlock() {
 		return true;
 	}
}
