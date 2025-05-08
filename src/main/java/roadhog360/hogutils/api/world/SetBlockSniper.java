package roadhog360.hogutils.api.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.world.ChunkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// Allows you to precede SetBlock calls by changing the block when the SetBlock is called.
public final class SetBlockSniper {
    private SetBlockSniper() {}

    private static final List<Sniper> SNIPERS = new ObjectArrayList<>();

    public static void registerSniper(Sniper sniper) {
        SNIPERS.add(sniper);
    }

    public static void removeSniper(Sniper sniper) {
        SNIPERS.remove(sniper);
    }

    public static List<Sniper> getSnipers() {
        return SNIPERS;
    }

    @Nullable
    public static Sniper getSniperFor(Chunk chunk, int chunkX, int chunkY, int chunkZ, Block block, int meta) {
        for(SetBlockSniper.Sniper sniper : SetBlockSniper.getSnipers()) {
            if(sniper.replaceBlock(chunk, chunkX, chunkY, chunkZ, block, meta) != null) {
                return sniper;
            }
        }
        return null;
    }

    /// The container objects for {@link SetBlockSniper}. It runs in {@link Chunk#func_150807_a(int, int, int, Block, int)} (setBlock) so it won't
    /// replace some blocks generated as a chunk is created like biome-blocks and base chunk shape. But it will cover stuff like ores, unless
    /// they set the chunk data directly.
    ///
    /// To replace stuff like that, just use {@link ChunkProviderEvent}, or
    /// {@link ChunkEvent}.
    public abstract static class Sniper {
        /// Return null to not run replacement code.
        @Nullable
        public abstract Block replaceBlock(Chunk chunk, int chunkX, int chunkY, int chunkZ, Block inputBlock, int inputMeta);

        /// Only runs on setBlock calls, doesn't run if only setMetadata was called.
        /// Currently does not run if a mod was to directly set the block using the ExtendedBlockStorage.
        /// TODO: Maybe create a detection system for that? Might not be needed, I can't think of any mods that do this atm
        public abstract int replaceMeta(Chunk chunk, int chunkX, int chunkY, int chunkZ, Block inputBlock, int inputMeta);
    }
}
