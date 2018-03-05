package lycanite.lycanitesmobs.arcticmobs.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import lycanite.lycanitesmobs.api.IWorldGenBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class WorldGeneratorArctic implements IWorldGenerator {
    protected final IWorldGenBase oozeLakes;

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGeneratorArctic() {
        this.oozeLakes = new WorldGenOozeLakes();
    }


    // ==================================================
    //                      Generate
    // ==================================================
     @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
         this.oozeLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }
}
