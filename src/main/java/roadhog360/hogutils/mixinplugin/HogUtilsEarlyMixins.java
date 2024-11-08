package roadhog360.hogutils.mixinplugin;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.include.com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

@LateMixin
public class HogUtilsEarlyMixins implements ILateMixinLoader {
    public static final MixinEnvironment.Side SIDE = MixinEnvironment.getCurrentEnvironment().getSide();

    @Override
    public String getMixinConfig() {
        return "";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = Lists.newArrayList();
        return mixins;
    }
}
