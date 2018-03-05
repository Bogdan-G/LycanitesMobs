package lycanite.lycanitesmobs.forestmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupFire;
import lycanite.lycanitesmobs.api.IGroupPlant;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.*;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
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
import net.minecraftforge.common.IPlantable;

public class EntitySpriggan extends EntityCreatureTameable implements IMob, IGroupPlant {
	
	EntityAIAttackRanged rangedAttackAI;
	public int farmingRate = 20;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySpriggan(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 10;
        this.hasAttackSound = false;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.2F;
        this.setupMob();

        this.stepHeight = 1.0F;
        this.farmingRate = ConfigBase.getConfig(this.group, "general").getInt("Features", "Spriggan Minion Crop Boosting", this.farmingRate, "Sets the rate in ticks (20 ticks = 1 second) that a Spriggan will boost nearby crops. Each boost will usually cause the crop to grow one stage.");
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, this.aiSit);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.rangedAttackAI = new EntityAIAttackRanged(this).setSpeed(0.75D).setRate(10).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F).setChaseTime(-1);
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
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
        HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
        baseAttributes.put("maxHealth", 15D);
        baseAttributes.put("movementSpeed", 0.24D);
        baseAttributes.put("knockbackResistance", 0.0D);
        baseAttributes.put("followRange", 16D);
        baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.stick), 0.5F).setMaxAmount(6).setBurningDrop(new ItemStack(Items.coal, 1, 1)));
        this.drops.add(new DropRate(new ItemStack(Blocks.vine), 0.1F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.wheat_seeds), 0.1F).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.pumpkin_seeds), 0.05F).setMaxAmount(1));
        this.drops.add(new DropRate(new ItemStack(Items.melon_seeds), 0.05F).setMaxAmount(1));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("lifedraincharge")), 0.25F).setMaxAmount(1));
	}


    // ==================================================
    //                      Updates
    // ==================================================
	private int farmingTick = 0;
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Water Healing:
        if(!this.worldObj.isRemote) {
	        if(this.isInWater())
	            this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 3 * 20, 2));
	        else if(this.worldObj.isRaining() && this.worldObj.canBlockSeeTheSky((int)this.posX, (int)this.posY, (int)this.posZ))
	            this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 3 * 20, 1));
        }

        // Farming:
        int currentFarmingRate = this.farmingRate;
        if(this.isTamed() && currentFarmingRate > 0) {
            if(this.isPetType("familiar"))
                currentFarmingRate = this.farmingRate * 10;
            if(this.subspecies != null)
                currentFarmingRate = Math.max(1, Math.round((float)this.farmingRate / 3));
        	this.farmingTick++;
	        int farmingRange = 16;
	        int farmingHeight = 4;
	        for(int x = (int)this.posX - farmingRange; x <= (int)this.posX + farmingRange; x++) {
	        	for(int y = (int)this.posY - farmingHeight; y <= (int)this.posY + farmingHeight; y++) {
	        		for(int z = (int)this.posZ - farmingRange; z <= (int)this.posZ + farmingRange; z++) {
	        			Block farmingBlock = this.worldObj.getBlock(x, y, z);
	        			if(farmingBlock != null && farmingBlock instanceof IPlantable && farmingBlock instanceof IGrowable && farmingBlock != Blocks.tallgrass && farmingBlock != Blocks.double_plant) {
	        				
		        			// Boost Crops Every X Seconds:
		        			if(!this.worldObj.isRemote && this.farmingTick % (currentFarmingRate) == 0) {
                                if(farmingBlock.getTickRandomly()) {
                                    this.worldObj.scheduleBlockUpdate(x, y, z, farmingBlock, currentFarmingRate);
                                }
		    	        		/*IGrowable growableBlock = (IGrowable)farmingBlock;
		    	        		if(growableBlock.func_149851_a(this.worldObj, x, y, z, this.worldObj.isRemote)) {
	    	                        if(growableBlock.func_149852_a(this.worldObj, this.getRNG(), x, y, z)) {
	    	                        	growableBlock.func_149853_b(this.worldObj, this.getRNG(), x, y, z);
	    	                        }
		    	                }*/
		        			}
		        			
		        			// Crop Growth Effect:
		        			if(this.worldObj.isRemote && this.farmingTick % 40 == 0) {
		        				double d0 = this.getRNG().nextGaussian() * 0.02D;
		                        double d1 = this.getRNG().nextGaussian() * 0.02D;
		                        double d2 = this.getRNG().nextGaussian() * 0.02D;
		        				this.worldObj.spawnParticle("happyVillager", (double)((float)x + this.getRNG().nextFloat()), (double)y + (double)this.getRNG().nextFloat() * farmingBlock.getBlockBoundsMaxY(), (double)((float)z + this.getRNG().nextFloat()), d0, d1, d2);
		        			}
	        			}
	    	        }
		        }
	        }
        }

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
            this.projectile.setBaseDamage(1);
	    	
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
