package roadhog360.hogutils.mixinplugin;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import roadhog360.hogutils.Tags;
import roadhog360.hogutils.config.HogUtilsConfigs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HogUtilsEarlyMixins implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static final MixinEnvironment.Side SIDE = MixinEnvironment.getCurrentEnvironment().getSide();

    @Override
    public String getMixinConfig() {
        return "mixins." + Tags.MOD_ID + ".early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        HogUtilsConfigs.init();

        List<String> mixins = new ArrayList<>();
        // Tags
        mixins.add("hogtags.MixinOreDictionary");
        mixins.add("hogtags.MixinBlock");
        mixins.add("hogtags.MixinItem");
        mixins.add("hogtags.MixinBiomeGenBase");

        // Custom events
        // IUnfinalizedSoundEventHandler
        mixins.add("event.MixinWorld");
        mixins.add("event.MixinPlaySoundAtEntityEvent");
        if (SIDE == MixinEnvironment.Side.CLIENT) {
            mixins.add("event.MixinEntityPlayerSP");
        }

        // Check if a populator is currently running
        mixins.add("geninfo.MixinChunkProviderServer");

        // Base block fixes/tools
        mixins.add("baseblock.MixinBlockLeaves");
        mixins.add("baseblock.MixinBlockFlowerPot");
        if (SIDE == MixinEnvironment.Side.CLIENT) {
            mixins.add("baseblock.MixinRenderBlocks");
        }
        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
