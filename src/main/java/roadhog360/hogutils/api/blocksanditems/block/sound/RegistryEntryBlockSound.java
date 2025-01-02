package roadhog360.hogutils.api.blocksanditems.block.sound;

import net.minecraft.block.Block;

public class RegistryEntryBlockSound {

    protected final boolean isEnabled;
    protected final Block.SoundType blockSound;
    protected final Block.SoundType disabledSound;

    public RegistryEntryBlockSound(boolean isEnabled, Block.SoundType blockSound, Block.SoundType disabledSound) {
        this.isEnabled = isEnabled;
        this.blockSound = blockSound;
        this.disabledSound = disabledSound;
    }

    public RegistryEntryBlockSound(Block.SoundType blockSound) {
        this(true, blockSound, null);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Block.SoundType get() {
        return isEnabled() ? blockSound : disabledSound;
    }
}
