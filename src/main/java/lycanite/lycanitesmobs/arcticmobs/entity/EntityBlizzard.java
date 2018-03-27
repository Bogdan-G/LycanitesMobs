package lycanite.lycanitesmobs.arcticmobs.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityBlizzard extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityBlizzard(World world) {
        super(world);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityBlizzard(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityBlizzard(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.setSize(0.3125F, 0.3125F);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "blizzard";
    	this.group = ArcticMobs.group;
    	this.setBaseDamage(1);
    	this.setProjectileScale(0.5F);
        this.knockbackChance = 0D;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
        entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, this.getEffectDuration(5), 0));
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
        String blockName = "icefire";
        if(this.getThrower() != null && this.getThrower() instanceof EntitySerpix) {
            EntitySerpix entitySerpix = (EntitySerpix)this.getThrower();
            if(!entitySerpix.isTamed())
                blockName = "frostfire";
        }
        world.setBlock(x, y, z, ObjectManager.getBlock(blockName));
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.worldObj.spawnParticle("snowshovel", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    	}
    }
}
