package lycanite.lycanitesmobs.infernomobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityScorchfireball extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityScorchfireball(World par1World) {
        super(par1World);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityScorchfireball(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityScorchfireball(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(0.3125F, 0.3125F);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "scorchfire";
    	this.group = InfernoMobs.group;
    	this.setBaseDamage(1);
    	this.setProjectileScale(2F);
        this.knockbackChance = 0.5D;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(10) / 20);
    	return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(int x, int y, int z) {
    	return true;
    }
    
    public boolean canDestroyBlockSub(int x, int y, int z) {
    	Block block = this.worldObj.getBlock(x, y, z);
    	if(block == Blocks.snow_layer || block == Blocks.tallgrass || block == Blocks.fire || block == Blocks.web || ObjectManager.getBlock("PoisonCloud") != null && block == ObjectManager.getBlock("PoisonCloud") || ObjectManager.getBlock("FrostCloud") != null && block == ObjectManager.getBlock("FrostCloud") || ObjectManager.getBlock("Frostweb") != null && block == ObjectManager.getBlock("Frostweb") || ObjectManager.getBlock("QuickWeb") != null && block == ObjectManager.getBlock("QuickWeb") || ObjectManager.getBlock("Hellfire") != null && block == ObjectManager.getBlock("Hellfire") || ObjectManager.getBlock("Frostfire") != null && block == ObjectManager.getBlock("Frostfire") || ObjectManager.getBlock("Icefire") != null && block == ObjectManager.getBlock("Icefire") || ObjectManager.getBlock("Scorchfire") != null && block == ObjectManager.getBlock("Scorchfire"))
            return true;
   	 	return super.canDestroyBlock(x, y, z);
    }
    
    //========== Place Block ==========
    @Override
    public void placeBlock(World world, int x, int y, int z) {
    	if(this.canDestroyBlockSub(x, y, z))
	   		 world.setBlock(x, y, z, ObjectManager.getBlock("Scorchfire"), 12, 3);
	   	if(this.canDestroyBlockSub(x + 1, y, z))
	   		 world.setBlock(x + 1, y, z, ObjectManager.getBlock("Scorchfire"), 11, 3);
	   	if(this.canDestroyBlockSub(x - 1, y, z))
		   	 world.setBlock(x - 1, y, z, ObjectManager.getBlock("Scorchfire"), 11, 3);
	   	if(this.canDestroyBlockSub(x, y, z + 1))
		   	 world.setBlock(x, y, z + 1, ObjectManager.getBlock("Scorchfire"), 11, 3);
	   	if(this.canDestroyBlockSub(x, y, z - 1))
		   	 world.setBlock(x, y, z - 1, ObjectManager.getBlock("Scorchfire"), 11, 3);
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.worldObj.spawnParticle("flame", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            this.worldObj.spawnParticle("largesmoke", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public String getLaunchSound() {
    	return AssetManager.getSound("scorchfireball");
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness(float par1) {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }
}
