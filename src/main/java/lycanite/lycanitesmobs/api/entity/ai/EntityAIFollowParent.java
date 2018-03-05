package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import net.minecraft.entity.EntityLivingBase;

public class EntityAIFollowParent extends EntityAIFollow {
	public boolean followAsAdult = false;
	
	// Targets:
	EntityCreatureAgeable host;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIFollowParent(EntityCreatureAgeable setHost) {
    	super(setHost);
        this.setMutexBits(1);
        this.host = setHost;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIFollowParent setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIFollowParent setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAIFollowParent setStrayDistance(double setDist) {
    	this.strayDistance = setDist * setDist;
    	return this;
    }
    public EntityAIFollowParent setLostDistance(double setDist) {
    	this.lostDistance = setDist * setDist;
    	return this;
    }
    public EntityAIFollowParent setAdultFollowing(boolean setBool) {
    	this.followAsAdult = setBool;
    	return this;
    }
    public EntityAIFollowParent setFollowBehind(double setDist) {
    	this.behindDistance = setDist;
    	return this;
    }
    
	
	// ==================================================
 	//                    Get Target
 	// ==================================================
    @Override
    public EntityLivingBase getTarget() {
    	return this.host.getParentTarget();
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	if(!this.followAsAdult && this.host.getGrowingAge() >= 0)
    		return false;
    	return super.shouldExecute();
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
    @Override
    public boolean continueExecuting() {
    	if(!this.followAsAdult && this.host.getGrowingAge() >= 0)
    		return false;
    	return super.continueExecuting();
    }
}
