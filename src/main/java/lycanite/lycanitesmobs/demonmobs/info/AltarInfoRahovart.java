package lycanite.lycanitesmobs.demonmobs.info;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.AltarInfo;
import lycanite.lycanitesmobs.demonmobs.entity.EntityBehemoth;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class AltarInfoRahovart extends AltarInfo {

    // ==================================================
    //                    Constructor
    // ==================================================
    public AltarInfoRahovart(String name) {
        super(name);
    }


    // ==================================================
    //                     Checking
    // ==================================================
    /** Called first when checking for a valid altar, this should be fairly lightweight such as just checking if the first block checked is valid, a more in depth check if then done after. **/
    public boolean quickCheck(Entity entity, World world, int x, int y, int z) {
        if(world.getBlock(x, y, z) != ObjectManager.getBlock("soulcubedemonic"))
            return false;
        return true;
    }

    /** Called if the QuickCheck() is passed, this should check the entire altar structure and if true is returned, the altar will activate. **/
    public boolean fullCheck(Entity entity, World world, int x, int y, int z) {
        if(!this.quickCheck(entity, world, x, y, z))
            return false;

        Block bodyBlock = Blocks.obsidian;

        // Middle:
        if(world.getBlock(x, y - 2, z) != bodyBlock)
            return false;
        if(world.getBlock(x, y - 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x, y + 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x, y + 2, z) != bodyBlock)
            return false;

        // Corners:
        if(!this.checkPillar(bodyBlock, entity, world, x - 2, y, z - 2))
            return false;
        if(!this.checkPillar(bodyBlock, entity, world, x + 2, y, z - 2))
            return false;
        if(!this.checkPillar(bodyBlock, entity, world, x - 2, y, z + 2))
            return false;
        if(!this.checkPillar(bodyBlock, entity, world, x + 2, y, z + 2))
            return false;

        return true;
    }


    public boolean checkPillar(Block bodyBlock, Entity entity, World world, int x, int y, int z) {
        if(world.getBlock(x, y - 2, z) != bodyBlock)
            return false;
        if(world.getBlock(x, y - 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x, y, z) != bodyBlock)
            return false;
        if(world.getBlock(x, y + 1, z) != Blocks.diamond_block)
            return false;
        return true;
    }


    // ==================================================
    //                     Activate
    // ==================================================
    /** Called when this Altar should activate. This will typically destroy the Altar and summon a rare mob or activate an event such as a boss event. If false is returned then the activation did not work, this is the place to check for things like dimensions. **/
    public boolean activate(Entity entity, World world, int x, int y, int z) {
        if (world.isRemote)
            return true;

        ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
        if(worldExt == null)
            return false;

        int originX = x;
        int originY = Math.max(1, y - 3);
        int originZ = z;
        if(entity != null) {
            double[] coords = this.getFacingPosition(x, y, z, 10, entity.rotationYaw);
            originX = Math.round((float)coords[0]);
            originZ = Math.round((float)coords[2]);
        }

        worldExt.startMobEvent("rahovart", originX, originY, originZ);

        return true;
    }
}
