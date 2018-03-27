package lycanite.lycanitesmobs.freshwatermobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityAquaPulse extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityAquaPulse(World par1World) {
        super(par1World);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityAquaPulse(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityAquaPulse(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(0.3125F, 0.3125F);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "aquapulse";
    	this.group = FreshwaterMobs.group;
    	this.setBaseDamage(2);
    	this.setProjectileScale(4F);
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(ObjectManager.getPotionEffect("Penetration") != null && ObjectManager.getPotionEffect("Penetration").id < Potion.potionTypes.length)
            entityLiving.addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("Penetration").id, this.getEffectDuration(8), 2));
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
            world.setBlock(x, y, z, Blocks.flowing_water, 12, 3);
        if(this.canDestroyBlockSub(x + 1, y, z))
            world.setBlock(x + 1, y, z, Blocks.flowing_water, 11, 3);
        if(this.canDestroyBlockSub(x - 1, y, z))
            world.setBlock(x - 1, y, z, Blocks.flowing_water, 11, 3);
        if(this.canDestroyBlockSub(x, y, z + 1))
            world.setBlock(x, y, z + 1, Blocks.flowing_water, 11, 3);
        if(this.canDestroyBlockSub(x, y, z - 1))
            world.setBlock(x, y, z - 1, Blocks.flowing_water, 11, 3);
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.worldObj.spawnParticle("splash", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    		this.worldObj.spawnParticle("splash", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    	}
    }
}
