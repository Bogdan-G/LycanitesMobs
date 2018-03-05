package lycanite.lycanitesmobs.api.item;

import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSwordBase extends ItemSword {
	public static int descriptionWidth = 128;
	
	public String itemName = "Item";
	public GroupInfo group = LycanitesMobs.group;
	public String textureName = "item";
    public final Item.ToolMaterial toolMaterial;
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordBase(Item.ToolMaterial toolMaterial) {
        super(toolMaterial);
        this.toolMaterial = toolMaterial;
    }
    
    public void setup() {
        this.setCreativeTab(LycanitesMobs.itemsTab);
    	this.setUnlocalizedName(this.itemName);
        this.textureName = this.itemName.toLowerCase();
        int nameLength = this.textureName.length();
        if(nameLength > 6 && this.textureName.substring(nameLength - 6, nameLength).equalsIgnoreCase("charge")) {
        	this.textureName = this.textureName.substring(0, nameLength - 6);
        }
    }
    
    
	// ==================================================
	//                      Info
	// ==================================================
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
    	String description = this.getDescription(itemStack, entityPlayer, textList, par4);
    	if(!"".equalsIgnoreCase(description) && !("item." + this.itemName + ".description").equals(description)) {
    		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    		List formattedDescriptionList = fontRenderer.listFormattedStringToWidth(description, descriptionWidth);
    		for(Object formattedDescription : formattedDescriptionList) {
    			if(formattedDescription instanceof String)
    				textList.add("\u00a7a" + (String)formattedDescription);
    		}
    	}
    	super.addInformation(itemStack, entityPlayer, textList, par4);
    }
    
    public String getDescription(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
    	return StatCollector.translateToLocal("item." + this.itemName + ".description");
    }
	
    
	// ==================================================
	//                      Update
	// ==================================================
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
		super.onUpdate(itemStack, world, entity, par4, par5);
	}

    /** Called from the main EventListener this works the same as onUpdate but is called before the rest of the entity's logic. **/
    public void onEarlyUpdate(ItemStack itemStack, EntityLivingBase entityLiving) { }
    
    
	// ==================================================
	//                     Tool/Weapon
	// ==================================================
    // ========== Get Sword Damage ==========
    /**
     * Returns the additional damage provided by this weapon.
     * Seems to be added on to a base value of 4.
     * Most weapons return 0 which would be +4 damage.
     * Diamond returns 3 which is +7 damage.
     * @return
     */
    public float getDamage() {
        return this.toolMaterial.getDamageVsEntity();
    }
    @Override
    public float func_150931_i()
    {
        return this.getDamage();
    }

	// ========== Hit Entity ==========
    @Override
    public boolean hitEntity(ItemStack itemStack, EntityLivingBase entityHit, EntityLivingBase entityUser) {
    	return super.hitEntity(itemStack, entityHit, entityUser);
    }
    
	// ========== Block Destruction ==========
    @Override
    public boolean onBlockDestroyed(ItemStack itemStack, World world, Block block, int x, int y, int z, EntityLivingBase entityLiving) {
        return super.onBlockDestroyed(itemStack, world, block, x, y, z, entityLiving);
    }
    
    // ========== Block Effectiveness ==========
    @Override
    public float func_150893_a(ItemStack itemStack, Block block) {
        return super.func_150893_a(itemStack, block);
    }
    
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Use ==========
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
    	return super.onItemUse(itemStack, player, world, x, y, z, p_77648_7_, p_77648_8_, p_77648_9_, p_77648_10_);
    }
    
    // ========== Start ==========
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
    	return super.onItemRightClick(itemStack, world, player);
    }

    // ========== Using ==========
    @Override
    public void onUsingTick(ItemStack itemStack, EntityPlayer player, int useRemaining) {
    	super.onUsingTick(itemStack, player, useRemaining);
    }
    
    // ========== Stop ==========
    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer player, int useRemaining) {
    	super.onPlayerStoppedUsing(itemStack, world, player, useRemaining);
    }

    // ========== Animation ==========
    @Override
    public EnumAction getItemUseAction(ItemStack itemStack) {
        return super.getItemUseAction(itemStack);
    }
    
    // ========== Entity Interaction ==========
    public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
    	return false;
    }

	
	// ==================================================
	//                     Enchanting
	// ==================================================
    @Override
    public int getItemEnchantability() {
        return super.getItemEnchantability(); // Based on material.
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        return super.getIsRepairable(itemStack, repairStack);
    }


    // ==================================================
    //                  Entity Spawning
    // ==================================================
    public void onSpawnEntity(Entity entity) {
        return;
    }

    /** Should return a chance from 0.0 to 1.0 which is used for special weapon effects such as randomly spawning minions when hitting enemies. **/
    public float getSpecialEffectChance() { return 0.2F; }

	
	// ==================================================
	//                     Visuals
	// ==================================================
    // ========== Get Icon ==========
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
    	return AssetManager.getIcon(this.itemName);
    }
    
    // ========== Register Icons ==========
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
    	AssetManager.addIcon(this.itemName, this.group, this.textureName, iconRegister);
    }

    // ========== Holding Angle ==========
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
