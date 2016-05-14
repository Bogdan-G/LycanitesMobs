package lycanite.lycanitesmobs.api.entity.navigate;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class FlightMoveHelper extends EntityMoveHelper {
    private EntityCreatureBase parentEntity;
    private int courseChangeCooldown;

    public FlightMoveHelper(EntityCreatureBase entityCreatureBase) {
        super(entityCreatureBase);
        this.parentEntity = entityCreatureBase;
    }

    /**
     * Called on the update to apply movement.
     * field_188491_h = Current Move Action
     */
    public void onUpdateMoveHelper() {
        if (this.field_188491_h == EntityMoveHelper.Action.MOVE_TO) {
            double x = this.posX - this.parentEntity.posX;
            double y = this.posY - this.parentEntity.posY;
            double z = this.posZ - this.parentEntity.posZ;
            double distance = x * x + y * y + z * z;

            if (this.courseChangeCooldown-- <= 0) {
                this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
                distance = (double)MathHelper.sqrt_double(distance);
                if(distance >= 1D) {
                    //if (this.isNotColliding(this.posX, this.posY, this.posZ, distance)) {
                        this.parentEntity.motionX += x / distance * 0.1D;
                        this.parentEntity.motionY += y / distance * 0.1D;
                        this.parentEntity.motionZ += z / distance * 0.1D;
                    //}
                    //else {
                    //    this.field_188491_h = EntityMoveHelper.Action.WAIT;
                    //}
                }
                else {
                    this.field_188491_h = EntityMoveHelper.Action.WAIT;
                }
            }
        }

        // Look At Target or Movement Direction:
        if (this.parentEntity.getAttackTarget() != null) {
            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
            double distanceX = entitylivingbase.posX - this.parentEntity.posX;
            double distanceY = entitylivingbase.posZ - this.parentEntity.posZ;
            this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw = -((float)MathHelper.atan2(distanceX, distanceY)) * (180F / (float)Math.PI);
        }
        else if(this.field_188491_h == EntityMoveHelper.Action.MOVE_TO) {
            this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw = -((float)MathHelper.atan2(this.parentEntity.motionX, this.parentEntity.motionZ)) * (180F / (float)Math.PI);
        }
    }

    /**
     * Checks if entity bounding box is not colliding with terrain.
     */
    private boolean isNotColliding(double x, double y, double z, double p_179926_7_) {
        double d0 = (x - this.parentEntity.posX) / p_179926_7_;
        double d1 = (y - this.parentEntity.posY) / p_179926_7_;
        double d2 = (z - this.parentEntity.posZ) / p_179926_7_;
        AxisAlignedBB axisalignedbb = this.parentEntity.getEntityBoundingBox();

        for (int i = 1; (double)i < p_179926_7_; ++i) {
            axisalignedbb = axisalignedbb.offset(d0, d1, d2);

            if (!this.parentEntity.worldObj.getCubes(this.parentEntity, axisalignedbb).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
