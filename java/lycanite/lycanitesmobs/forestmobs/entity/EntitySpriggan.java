package lycanite.lycanitesmobs.forestmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupFire;
import lycanite.lycanitesmobs.api.IGroupPlant;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackRanged;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowOwner;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySpriggan extends EntityCreatureTameable implements IMob, IGroupPlant {
	EntityAIAttackRanged rangedAttackAI;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySpriggan(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 10;
        this.spawnsInDarkness = true;
        this.hasAttackSound = false;
        
        this.eggName = "ForestEgg";
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();
        this.stepHeight = 1.0F;
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.rangedAttackAI = new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(10).setStaminaTime(100).setRange(14.0F).setMinChaseDistance(4.0F).setChaseTime(-1);
        this.tasks.addTask(5, rangedAttackAI);
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
    	this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupFire.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
        HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
        baseAttributes.put("maxHealth", 20D);
        baseAttributes.put("movementSpeed", 0.24D);
        baseAttributes.put("knockbackResistance", 0.0D);
        baseAttributes.put("followRange", 16D);
        baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.stick), 0.5F).setMaxAmount(6).setBurningDrop(new ItemStack(Items.coal)));
        this.drops.add(new DropRate(new ItemStack(Blocks.vine), 0.1F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.wheat_seeds), 0.1F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.pumpkin_seeds), 0.05F).setMaxAmount(1));
        this.drops.add(new DropRate(new ItemStack(Items.melon_seeds), 0.05F).setMaxAmount(1));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("lifedraincharge")), 0.25F).setMaxAmount(1));
	}


    // ==================================================
    //                      Updates
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Water Healing:
        if(this.isInWater())
            this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 3 * 20, 2));
        else if(this.worldObj.isRaining() && this.worldObj.canBlockSeeTheSky((int)this.posX, (int)this.posY, (int)this.posZ))
            this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 3 * 20, 1));

        // Particles:
        if(this.worldObj.isRemote)
            for(int i = 0; i < 2; ++i) {
                this.worldObj.spawnParticle("blockcrack_18_0", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
            }
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    EntityLifeDrain projectile = null;
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Update Laser:
    	if(this.projectile != null && this.projectile.isEntityAlive()) {
    		this.projectile.setTime(20);
    	}
    	else {
    		this.projectile = null;
    	}
    	
    	// Create New Laser:
    	if(this.projectile == null) {
	    	// Type:
	    	this.projectile = new EntityLifeDrain(this.worldObj, this, 25, 20);
	    	
	    	// Launch:
	        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.worldObj.spawnEntityInWorld(projectile);
    	}
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 4.0F;
    	if(damageSrc.getEntity() != null) {
    		Item heldItem = null;
    		if(damageSrc.getEntity() instanceof EntityPlayer) {
    			EntityPlayer entityPlayer = (EntityPlayer)damageSrc.getEntity();
	    		if(entityPlayer.getHeldItem() != null) {
	    			heldItem = entityPlayer.getHeldItem().getItem();
	    		}
    		}
    		else if(damageSrc.getEntity() instanceof EntityLiving) {
	    		EntityLiving entityLiving = (EntityLiving)damageSrc.getEntity();
	    		if(entityLiving.getHeldItem() != null) {
	    			heldItem = entityLiving.getHeldItem().getItem();
	    		}
    		}
    		if(ObjectLists.isAxe(heldItem))
				return 4.0F;
    	}
    	return 1.0F;
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    @Override
    public boolean canFly() { return true; }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }


    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotionID() == Potion.moveSlowdown.id) return false;
        if(ObjectManager.getPotionEffect("Paralysis") != null)
            if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Paralysis").id) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
}
