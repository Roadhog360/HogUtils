package roadhog360.hogutils.api.blocksanditems.block.sound;

import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBlockSound extends Block.SoundType {

    private final boolean uniquePlaceSound;

    public BaseBlockSound(String name, float volume, float pitch) {
        this(name, volume, pitch, false);
    }

    public BaseBlockSound(String name, float volume, float pitch, boolean uniquePlaceSound) {
        super(name, volume, pitch);
        this.uniquePlaceSound = uniquePlaceSound;
    }

    public String getBreakSound() {
        return addDomain() + "block." + this.soundName + ".break";
    }

    public String getStepResourcePath() {
        return addDomain() + "block." + this.soundName + ".step";
    }

    public String func_150496_b() {
        return uniquePlaceSound ? addDomain() + "block." + this.soundName + ".place" : getBreakSound();
    }

    public abstract @Nullable String getDomain();

    protected String addDomain() {
        if(getDomain() == null) return "";
        return getDomain() + ":";
    }
}
