package lycanite.lycanitesmobs.junglemobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityPoop extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityPoop(World par1World) {
        super(par1World);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityPoop(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityPoop(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(0.3125F, 0.3125F);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "poop";
    	this.group = JungleMobs.group;
    	this.setBaseDamage(2);
    	this.setProjectileScale(1.0F);
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, this.getEffectDuration(3), 0));
    	entityLiving.addPotionEffect(new PotionEffect(Potion.confusion.id, this.getEffectDuration(2), 0));
    	return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(int x, int y, int z) {
    	Block block = this.worldObj.getBlock(x, y, z);
    	if(block == Blocks.snow || block == Blocks.tallgrass || ObjectManager.getBlock("PoisonCloud") != null && block == ObjectManager.getBlock("PoisonCloud") || ObjectManager.getBlock("PoopCloud") != null && block == ObjectManager.getBlock("PoopCloud") || ObjectManager.getBlock("FrostCloud") != null && block == ObjectManager.getBlock("FrostCloud") || ObjectManager.getBlock("Frostweb") != null && block == ObjectManager.getBlock("Frostweb") || ObjectManager.getBlock("QuickWeb") != null && block == ObjectManager.getBlock("QuickWeb") || ObjectManager.getBlock("Hellfire") != null && block == ObjectManager.getBlock("Hellfire") || ObjectManager.getBlock("Frostfire") != null && block == ObjectManager.getBlock("Frostfire") || ObjectManager.getBlock("Icefire") != null && block == ObjectManager.getBlock("Icefire") || ObjectManager.getBlock("Scorchfire") != null && block == ObjectManager.getBlock("Scorchfire"))
            return true;
   	 	return super.canDestroyBlock(x, y, z);
    }
    
    //========== Place Block ==========
    @Override
    public void placeBlock(World world, int x, int y, int z) {
	   	 world.setBlock(x, y, z, ObjectManager.getBlock("poopcloud"));
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.worldObj.spawnParticle("blockcrack_3_2", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public String getLaunchSound() {
    	return AssetManager.getSound("poop");
    }
}
