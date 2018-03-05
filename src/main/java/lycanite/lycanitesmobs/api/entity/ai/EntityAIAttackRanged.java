package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

public class EntityAIAttackRanged extends EntityAIBase {
    // Targets:
	private final EntityCreatureBase host;
    private EntityLivingBase attackTarget;

    // Properties
    private int attackTime;
    private int attackTimeClose = 20;
    private int attackTimeFar = 20;
    
    private int attackStamina = 0;
    private int attackStaminaMax = 0;
    private boolean attackOnCooldown = false;
    private int staminaRecoverRate = 1;
    private int staminaDrainRate = 1;
    
    private double speed = 1.0D;
    private int chaseTime;
    private int chaseTimeMax = -1; // Average of 20
    private float range = 5.0F;
    private float attackDistance = 5.0F * 5.0F;
    private float minChaseDistance = 2.0F;
    private float flyingHeight = 2.0F;
    private boolean longMemory = true;
    private boolean checkSight = true;
    private boolean mountedAttacking = true;
    public boolean enabled = true;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAIAttackRanged(EntityCreatureBase setHost) {
    	this.host = setHost;
        this.setMutexBits(3);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIAttackRanged setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIAttackRanged setLongMemory(boolean setLongMemory) {
    	this.longMemory = setLongMemory;
    	return this;
    }
    public EntityAIAttackRanged setCheckSight(boolean setCheckSight) {
    	this.checkSight = setCheckSight;
    	return this;
    }
    public EntityAIAttackRanged setRateClose(int rate) {
    	this.attackTimeClose = rate;
    	return this;
    }
    public EntityAIAttackRanged setRateFar(int rate) {
    	this.attackTimeFar = rate;
    	return this;
    }
    public EntityAIAttackRanged setRate(int rate) {
    	return this.setRateClose(rate).setRateFar(rate);
    }
    
    // ========== Stamina ==========
    public EntityAIAttackRanged setStaminaTime(int setInt) {
    	this.attackStaminaMax = setInt;
    	this.attackStamina = this.attackStaminaMax;
    	return this;
    }
    public EntityAIAttackRanged setStaminaRecoverRate(int rate) {
    	this.staminaRecoverRate = rate;
    	return this;
    }
    public EntityAIAttackRanged setStaminaDrainRate(int rate) {
    	this.staminaDrainRate = rate;
    	return this;
    }
    
    public EntityAIAttackRanged setRange(float setRange) {
    	this.range = setRange;
    	this.attackDistance = setRange * setRange;
    	return this;
    }
    public EntityAIAttackRanged setMinChaseDistance(float setMinDist) {
    	this.minChaseDistance = setMinDist * setMinDist;
    	return this;
    }
    public EntityAIAttackRanged setChaseTime(int setChaseTime) {
    	this.chaseTimeMax = setChaseTime;
    	return this;
    }
    public EntityAIAttackRanged setFlyingHeight(float setFlyingHeight) {
    	this.flyingHeight = setFlyingHeight;
    	return this;
    }
    public EntityAIAttackRanged setMountedAttacking(boolean bool) {
        this.mountedAttacking = bool;
        return this;
    }
    public EntityAIAttackRanged setEnabled(boolean setEnabled) {
    	this.enabled = setEnabled;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	// Attack Stamina/Cooldown Recovery:
        if(this.attackStaminaMax > 0) {
        	if(this.attackOnCooldown) {
        		this.attackStamina += this.staminaRecoverRate;
        		if(this.attackStamina >= this.attackStaminaMax)
        			this.attackOnCooldown = false;
        	}
        }
        
        // Should Execute:
    	if(!this.enabled)
    		return false;
        if(!this.mountedAttacking && this.host instanceof EntityCreatureRideable) {
            EntityCreatureRideable rideableHost = (EntityCreatureRideable)this.host;
            if(rideableHost.getRiderTarget() instanceof EntityPlayer)
                return false;
        }
        EntityLivingBase possibleAttackTarget = this.host.getAttackTarget();
        if(possibleAttackTarget == null)
            return false;
        if(!possibleAttackTarget.isEntityAlive())
            return false;
        this.attackTarget = possibleAttackTarget;
        return true;
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
    public boolean continueExecuting() {
    	if(!this.longMemory)
	    	if(!this.host.useFlightNavigator() && !this.host.getNavigator().noPath()) return this.shouldExecute();
	    	else if(this.host.useFlightNavigator() && this.host.flightNavigator.targetPosition == null) return this.shouldExecute();

    	// Should Execute:
    	if(!this.enabled)
    		return false;
        EntityLivingBase possibleAttackTarget = this.host.getAttackTarget();
        if(possibleAttackTarget == null)
            return false;
        if(!possibleAttackTarget.isEntityAlive())
            return false;
        this.attackTarget = possibleAttackTarget;
        return true;
    }
    
    
    // ==================================================
  	//                      Reset
  	// ==================================================
    public void resetTask() {
        this.attackTarget = null;
        this.chaseTime = 0;
        this.attackTime = -1;
    }
    
    
    // ==================================================
  	//                   Update Task
  	// ==================================================
    public void updateTask() {
        double distance = this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        boolean hasSight = this.host.getEntitySenses().canSee(this.attackTarget);
        float flyingHeightOffset = this.flyingHeight;
        
        if(hasSight && this.chaseTimeMax >= 0)
            ++this.chaseTime;
        else
            this.chaseTime = 0;
        
        if(!hasSight)
        	flyingHeightOffset = 0;

        if(distance <= this.minChaseDistance || (this.chaseTimeMax >= 0 && distance <= (double)this.attackDistance && this.chaseTime >= this.chaseTimeMax))
        	if(!this.host.useFlightNavigator())
        		this.host.getNavigator().clearPathEntity();
        	else
        		this.host.flightNavigator.clearTargetPosition(1.0D);
        else
        	if(!this.host.useFlightNavigator())
        		this.host.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.speed);
        	else
        		this.host.flightNavigator.setTargetPosition(new ChunkCoordinates((int)this.attackTarget.posX, (int)(this.attackTarget.posY + flyingHeightOffset), (int)this.attackTarget.posZ), speed);

        this.host.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
        float rangeFactor;
        
        // Attack Stamina/Cooldown:
        if(this.attackStaminaMax > 0) {
        	if(!this.attackOnCooldown) {
        		this.attackStamina -= this.staminaDrainRate;
        		if(this.attackStamina <= 0)
        			this.attackOnCooldown = true;
        	}
        	else {
        		this.attackStamina += this.staminaRecoverRate;
        		if(this.attackStamina >= this.attackStaminaMax)
        			this.attackOnCooldown = false;
        	}
        }
        else if(this.attackOnCooldown)
        	this.attackOnCooldown = false;
        
        // Fire Projectile:
        if(!this.attackOnCooldown) {
	        if(--this.attackTime == 0) {
	            if(distance > (double)this.attackDistance || (this.checkSight && !hasSight))
	                return;
	
	            rangeFactor = MathHelper.sqrt_double(distance) / this.range;
	            float outerRangeFactor = rangeFactor; // Passed to the attack, clamps targets within 10% closeness.
	            if(rangeFactor < 0.1F)
	            	outerRangeFactor = 0.1F;
	            if(outerRangeFactor > 1.0F)
	            	outerRangeFactor = 1.0F;
	
	            this.host.rangedAttack(this.attackTarget, outerRangeFactor);
	            if(rangeFactor > 0.5F)
	            	this.attackTime = this.host.getHaste(this.attackTimeFar);
	            else
	            	this.attackTime = this.host.getHaste(this.attackTimeClose);
	        }
	        else if(this.attackTime < 0) {
	        	rangeFactor = MathHelper.sqrt_double(distance) / this.range;
	            if(rangeFactor > 0.5F)
	            	this.attackTime = this.host.getHaste(this.attackTimeFar);
	            else
	            	this.attackTime = this.host.getHaste(this.attackTimeClose);
	        }
        }
    }
}
