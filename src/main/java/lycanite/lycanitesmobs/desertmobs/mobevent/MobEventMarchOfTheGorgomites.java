package lycanite.lycanitesmobs.desertmobs.mobevent;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventMarchOfTheGorgomites extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventMarchOfTheGorgomites(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world) {
        super.onStart(world);
		world.getWorldInfo().setRaining(false);
		world.getWorldInfo().setThundering(false);
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);

        if(entity instanceof EntityCreatureBase && entity.getRNG().nextFloat() >= 0.85D) {
        	((EntityCreatureBase)entity).setSizeScale(3.0D + (0.35D * (0.5D - entity.getRNG().nextFloat())));
        }
	}
}
