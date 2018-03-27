package lycanite.lycanitesmobs.arcticmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityFrostweb extends EntityProjectileBase {
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityFrostweb(World par1World) {
        super(par1World);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityFrostweb(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityFrostweb(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(0.3125F, 0.3125F);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "frostweb";
    	this.group = ArcticMobs.group;
    	this.setBaseDamage(2);
        this.knockbackChance = 0D;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, this.getEffectDuration(2), 0));
    	return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(int x, int y, int z) {
    	Block block = this.worldObj.getBlock(x, y, z);
    	if(block == Blocks.snow || block == Blocks.fire || block == Blocks.tallgrass || ObjectManager.getBlock("PoisonCloud") != null && block == ObjectManager.getBlock("PoisonCloud") || ObjectManager.getBlock("FrostCloud") != null && block == ObjectManager.getBlock("FrostCloud") || ObjectManager.getBlock("Frostweb") != null && block == ObjectManager.getBlock("Frostweb") || ObjectManager.getBlock("QuickWeb") != null && block == ObjectManager.getBlock("QuickWeb") || ObjectManager.getBlock("Hellfire") != null && block == ObjectManager.getBlock("Hellfire") || ObjectManager.getBlock("Frostfire") != null && block == ObjectManager.getBlock("Frostfire") || ObjectManager.getBlock("Icefire") != null && block == ObjectManager.getBlock("Icefire") || ObjectManager.getBlock("Scorchfire") != null && block == ObjectManager.getBlock("Scorchfire"))
            return true;
   	 	return super.canDestroyBlock(x, y, z);
    }
    
    //========== Place Block ==========
    @Override
    public void placeBlock(World world, int x, int y, int z) {
	   	 world.setBlock(x, y, z, ObjectManager.getBlock("Frostweb"));
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.worldObj.spawnParticle("snowshovel", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
}
