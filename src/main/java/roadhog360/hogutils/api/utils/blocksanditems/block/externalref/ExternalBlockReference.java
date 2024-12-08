package roadhog360.hogutils.api.utils.blocksanditems.block.externalref;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import roadhog360.hogutils.api.utils.blocksanditems.ExternalReference;

import java.util.function.Supplier;

public final class ExternalBlockReference extends ExternalReference<Block> {
    public ExternalBlockReference(String modID, String namespace) {
        super(modID, namespace);
    }

    public ExternalBlockReference(String blockID) {
        super(blockID);
    }

    @Override
    protected Supplier<Block> getSupplier() {
        return () -> GameRegistry.findBlock(modID, namespace);
    }

}
