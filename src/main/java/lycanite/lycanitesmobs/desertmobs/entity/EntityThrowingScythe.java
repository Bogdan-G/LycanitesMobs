package lycanite.lycanitesmobs.desertmobs.entity;

import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.desertmobs.DesertMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityThrowingScythe extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	private float projectileWidth = 0.2f;
	private float projectileHeight = 0.2f;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityThrowingScythe(World par1World) {
        super(par1World);
        this.setSize(projectileWidth, projectileHeight);
    }

    public EntityThrowingScythe(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
        this.setSize(projectileWidth, projectileHeight);
    }

    public EntityThrowingScythe(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
        this.setSize(projectileWidth, projectileHeight);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "throwingscythe";
    	this.group = DesertMobs.group;
    	this.setBaseDamage(3);
    	this.setProjectileScale(1F);
        this.knockbackChance = 0.25D;
    	
    	this.waterProof = true;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void onUpdate() {
    	super.onUpdate();
    	if(this.posY > this.worldObj.getHeight() + 20)
    		this.setDead();
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0.01F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.worldObj.spawnParticle("smoke", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
}
