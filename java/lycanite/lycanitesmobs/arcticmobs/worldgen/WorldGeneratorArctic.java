package lycanite.lycanitesmobs.arcticmobs.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class WorldGeneratorArctic implements IWorldGenerator {
    protected final WorldGenerator oozeLakes;

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
         BiomeGenBase biome = world.getBiomeGenForCoords(chunkX, chunkZ);
         BiomeDictionary.Type[] biomeTypes = BiomeDictionary.getTypesForBiome(biome);
         boolean typeValid = false;
         for(BiomeDictionary.Type type : biomeTypes) {
             if((type == BiomeDictionary.Type.SNOWY) || (type == BiomeDictionary.Type.COLD)) {
                 typeValid = true;
                 break;
             }
         }
         if(!typeValid)
             return;

         if(random.nextInt(25) == 0) {
             int x = chunkX * 16 + random.nextInt(16);
             int z = chunkZ * 16 + random.nextInt(16);
             int y = world.getTopSolidOrLiquidBlock(x, z);
             this.oozeLakes.generate(world, random, x, y, z);
         }
    }
}
