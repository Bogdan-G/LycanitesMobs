package lycanite.lycanitesmobs.swampmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupHunter;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAvoid;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIBeg;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowOwner;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIMate;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIStealth;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerThreats;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITempt;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.desertmobs.entity.EntityJoust;
import lycanite.lycanitesmobs.desertmobs.entity.EntityJoustAlpha;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityLurker extends EntityCreatureTameable implements IGroupHunter {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityLurker(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = true;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.5F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIStealth(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(2, new EntityAIAvoid(this).setNearSpeed(2.0D).setFarSpeed(1.5D).setNearDistance(5.0D).setFarDistance(10.0D));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(4, this.aiSit);
        this.tasks.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(6, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("lurkertreat"))).setTemptDistanceMin(2.0D));
        this.tasks.addTask(7, new EntityAIMate(this));
        this.tasks.addTask(8, new EntityAIFollowParent(this));
        this.tasks.addTask(9, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIBeg(this));
        this.tasks.addTask(11, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(12, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(MobInfo.predatorsAttackAnimals) {
        	this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityChicken.class));
	        if(ObjectManager.getMob("Joust") != null)
	        	this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityJoust.class));
	        if(ObjectManager.getMob("JoustAlpha") != null)
	        	this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityJoustAlpha.class));
        }
        this.targetTasks.addTask(0, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 15D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.string), 0.5F).setMinAmount(2).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Items.leather), 0.5F).setMaxAmount(2));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Lurker Blind Stalking:
        if(this.getAttackTarget() != null) {
        	if(this.getAttackTarget().isPotionActive(Potion.blindness.id))
        		this.setAvoidTarget(this.getAttackTarget());
        	else
        		this.setAvoidTarget(null);
        }
        else
        	this.setAvoidTarget(null);
        
        // Leap:
        if(this.onGround && !this.worldObj.isRemote && this.rand.nextInt(10) == 0) {
        	if(this.hasAttackTarget())
        		this.leap(6.0F, 0.4D, this.getAttackTarget());
        	else if(this.hasAvoidTarget())
        		this.leap(4.0F, 0.4D);
        }
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Effect:
        if(target instanceof EntityLivingBase) {
            ((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.poison.id, this.getEffectDuration(8), 1));
            ((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.blindness.id, this.getEffectDuration(8), 0));
        }
        
        return true;
    }
    
    
    // ==================================================
   	//                     Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
    	if(this.worldObj.isRemote) return false;
    	else {
	    	if(this.hasAttackTarget()) {
	    		if(this.getAttackTarget() instanceof EntityPlayer) {
	    			EntityPlayer playerTarget = (EntityPlayer)this.getAttackTarget();
	    			ItemStack itemstack = playerTarget.inventory.getCurrentItem();
	    			if(itemstack != null && isTamingItem(itemstack))
	    				return false;
	    		}
	    		if(!this.getAttackTarget().isPotionActive(Potion.blindness.id))
	    			return false;
	    		if(this.getDistanceSqToEntity(this.getAttackTarget()) < (5.0D * 5.0D))
	    			return false;
	    	}
	    	else {
	    		if(this.isMoving())
	    			return false;
	    	}
	        return true;
        }
    }
    
    @Override
    public void startStealth() {
    	if(this.worldObj.isRemote) {
            String particle = "smoke";
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
            	this.worldObj.spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    	super.startStealth();
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean canClimb() { return true; }
    
    
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
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.poison.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.blindness.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    
    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemstack) {
        return itemstack.getItem() == ObjectManager.getItem("lurkertreat");
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }
    
    
    // ==================================================
    //                       Visuals
    // ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
    	if(this.isTamed() && this.getOwner() == player)
    		return false;
        return this.isInvisible();
    }
}
