package roadhog360.hogutils.mixinplugin;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import roadhog360.hogutils.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public class HogUtilsLateMixins implements ILateMixinLoader {

    public static final MixinEnvironment.Side SIDE = MixinEnvironment.getCurrentEnvironment().getSide();

    @Override
    public String getMixinConfig() {
        return "mixins." + Tags.MOD_ID + ".late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
//        mixins.add("sample1");
        if (SIDE == MixinEnvironment.Side.CLIENT) {
//            mixins.add("sample2");
        }
        return mixins;
    }
}
