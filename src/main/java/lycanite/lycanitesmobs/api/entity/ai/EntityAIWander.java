package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

public class EntityAIWander extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private double speed = 1.0D;
    private int pauseRate = 120;
    
    private double xPosition;
    private double yPosition;
    private double zPosition;
    
    // ==================================================
   	//                     Constructor
   	// ==================================================
    public EntityAIWander(EntityCreatureBase setHost) {
    	this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIWander setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIWander setPauseRate(int setPause) {
    	this.pauseRate = setPause;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
    public boolean shouldExecute() {
        if(this.host.getAge() >= 100)
            return false;
        else if(pauseRate != 0 && this.host.getRNG().nextInt(pauseRate) != 0)
            return false;
        else {
        	int flight = 0;
        	if(this.host.useFlightNavigator()) flight = 20;
            Vec3 newTarget = RandomPositionGenerator.findRandomTarget(this.host, 10, 7, flight);
            if(newTarget == null)
                return false;
            else {
                ChunkCoordinates wanderPosition = this.host.getWanderPosition(new ChunkCoordinates((int)newTarget.xCoord, (int)newTarget.yCoord, (int)newTarget.zCoord));
                this.xPosition = wanderPosition.posX;
                this.yPosition = wanderPosition.posY;
                this.zPosition = wanderPosition.posZ;
                return true;
            }
        }
    }
    
    
    // ==================================================
   	//                Continue Executing
   	// ==================================================
    public boolean continueExecuting() {
    	if(!host.useFlightNavigator())
    		return !this.host.getNavigator().noPath();
    	else {
            return !this.host.flightNavigator.atTargetPosition() && this.host.flightNavigator.isTargetPositionValid();
        }
        	//return this.host.getRNG().nextInt(100) != 0 && !this.host.flightNavigator.atTargetPosition() && this.host.flightNavigator.isTargetPositionValid();
    }
    
    
    // ==================================================
   	//                     Start
   	// ==================================================
    public void startExecuting() {
    	if(!host.useFlightNavigator())
    		this.host.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    	else
    		host.flightNavigator.setTargetPosition(new ChunkCoordinates((int)this.xPosition, (int)this.yPosition, (int)this.zPosition), this.speed);
    }
}
