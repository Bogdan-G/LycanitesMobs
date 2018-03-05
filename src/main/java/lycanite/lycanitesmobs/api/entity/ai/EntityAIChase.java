package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

public class EntityAIChase extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    private EntityLivingBase target;
    
    // Properties:
    private double speed = 1.0D;
    private float maxTargetDistance = 8.0F;
    
    private double movePosX;
    private double movePosY;
    private double movePosZ;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIChase(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIChase setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIChase setMaxDistance(float setDist) {
    	this.maxTargetDistance = setDist;
    	return this;
    }
	
    
	// ==================================================
 	//                   Should Execute
 	// ==================================================
    public boolean shouldExecute() {
        this.target = this.host.getAttackTarget();
        if(this.target == null)
            return false;
        else if(this.host.getDistanceSqToEntity(this.target) > (double)(this.maxTargetDistance * this.maxTargetDistance))
            return false;
        
        Vec3 vec3 = RandomPositionGenerator.findRandomTargetTowards(this.host, 16, 7, Vec3.createVectorHelper(this.target.posX, this.target.posY, this.target.posZ));
        if(vec3 == null)
            return false;
        
        this.movePosX = vec3.xCoord;
        this.movePosY = vec3.yCoord;
        this.movePosZ = vec3.zCoord;
        return true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
    	return !this.host.getNavigator().noPath() && this.host.isEntityAlive() && this.target.getDistanceSqToEntity(this.host) < (double)(this.maxTargetDistance * this.maxTargetDistance);
    }
	
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
    	if(!this.host.useFlightNavigator())
    		this.host.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
    	else
    		this.host.flightNavigator.setTargetPosition(new ChunkCoordinates((int)this.movePosX, (int)this.movePosY, (int)this.movePosZ), speed);
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.target = null;
        this.host.flightNavigator.clearTargetPosition(1.0D);
    }
}
