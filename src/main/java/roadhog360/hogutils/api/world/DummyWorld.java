package roadhog360.hogutils.api.world;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceIntImmutablePair;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import roadhog360.hogutils.api.BlockPos;
import roadhog360.hogutils.api.utils.FastRandom;

import java.io.File;
import java.util.Map;

/// Used for fake worlds where setblock/getblock is needed
/// Tile entity data and entities are not supported
public class DummyWorld extends World {
    private final Map<BlockPos, ReferenceIntImmutablePair<Block>> FAKE_WORLD_DATA = new Object2ReferenceOpenHashMap<>(); //Stores setblock data for getblock
    private static final ReferenceIntImmutablePair<Block> AIR = ReferenceIntImmutablePair.of(Blocks.air, 0);

    DummyWorld(ISaveHandler par1iSaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler) {
        super(par1iSaveHandler, par2Str, par4WorldSettings, par3WorldProvider, par5Profiler);
        rand = new FastRandom();
    }

    public DummyWorld() {
        this(
            new ISaveHandler() {
                @Override
                public void saveWorldInfoWithPlayer(WorldInfo var1, NBTTagCompound var2) {/*Do nothing*/}

                @Override
                public void saveWorldInfo(WorldInfo var1) {/*Do nothing*/}

                @Override
                public WorldInfo loadWorldInfo() {
                    return null;
                }

                @Override
                public IPlayerFileData getSaveHandler() {
                    return null;
                }

                @Override
                public File getMapFileFromName(String var1) {
                    return null;
                }

                @Override
                public IChunkLoader getChunkLoader(WorldProvider var1) {
                    return null;
                }

                @Override
                public void flush() {/*Do nothing*/}

                @Override
                public void checkSessionLock() {/*Do nothing*/}

                @Override
                public String getWorldDirectoryName() {
                    return "jss2a98aj";
                }

                @Override
                public File getWorldDirectory() {
                    return null;
                }
            },
            "DUMMY_DIMENSION",
            new WorldProvider() {
                @Override
                public String getDimensionName() {
                    return "DUMMY_DIMENSION";
                }
            },
            new WorldSettings(new WorldInfo(new NBTTagCompound())),
            new Profiler()
        );
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
    }

    @Override
    public Entity getEntityByID(int aEntityID) {
        return null;
    }

    @Override
    public boolean setBlockMetadataWithNotify(int aX, int aY, int aZ, int aMeta, int flags) {
        ReferenceIntImmutablePair<Block> block = FAKE_WORLD_DATA.get(new BlockPos(aX, aY, aZ));
        if (block != null && block.rightInt() != aMeta) {
            setBlock(aX, aY, aZ, block.left(), aMeta, 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean setBlockToAir(int aX, int aY, int aZ) {
        val block = FAKE_WORLD_DATA.remove(new BlockPos(aX, aY, aZ));
        return block != null && block.left() == Blocks.air;
    }

    @Override
    public boolean setBlock(int x, int y, int z, Block block) {
        return this.setBlock(x, y, z, block, 0, 0);
    }

    @Override
    public boolean setBlock(int aX, int aY, int aZ, Block aBlock, int aMeta, int aFlags) {
        BlockPos pos = new BlockPos(aX, aY, aZ);
        if (aBlock == Blocks.air) {
            return setBlockToAir(aX, aY, aZ);
        } else {
            ReferenceIntImmutablePair<Block> result = FAKE_WORLD_DATA.put(pos, ReferenceIntImmutablePair.of(aBlock, aMeta));
            return result == null || result.left() != aBlock || result.rightInt() != aMeta;
        }
    }

    @Override
    public float getSunBrightnessFactor(float p_72967_1_) {
        return 1.0F;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int aX, int aZ) {
        return BiomeGenBase.ocean;
    }

    @Override
    public int getFullBlockLightValue(int aX, int aY, int aZ) {
        return 10;
    }

    @Override
    public Block getBlock(int aX, int aY, int aZ) {
        return FAKE_WORLD_DATA.getOrDefault(new BlockPos(aX, aY, aZ), AIR).left();
    }

    @Override
    public int getBlockMetadata(int aX, int aY, int aZ) {
        return FAKE_WORLD_DATA.getOrDefault(new BlockPos(aX, aY, aZ), AIR).rightInt();
    }

    @Override
    public boolean canBlockSeeTheSky(int aX, int aY, int aZ) {
        if (aX >= 16 && aZ >= 16 && aX < 32 && aZ < 32) return aY > 64;
        return true;
    }

    /**
     * MCP name: {@code getRenderDistanceChunks}
     */
    @Override
    protected int func_152379_p() {
        return 0;
    }

    public void clearFakeData() {
        FAKE_WORLD_DATA.clear();
    }
}
