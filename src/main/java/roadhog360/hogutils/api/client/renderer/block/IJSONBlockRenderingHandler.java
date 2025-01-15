package roadhog360.hogutils.api.client.renderer.block;

import com.gtnewhorizon.gtnhlib.client.renderer.quad.QuadProvider;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public interface IJSONBlockRenderingHandler extends ISimpleBlockRenderingHandler {
    QuadProvider getInventoryModel(Block block, int meta);

    QuadProvider getWorldModel(IBlockAccess world, int x, int y, int z, int meta);
}
