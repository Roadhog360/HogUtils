package roadhog360.hogutils.mixinplugin;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import roadhog360.hogutils.Tags;
import roadhog360.hogutils.config.HogUtilsConfigs;
import roadhog360.hogutils.repackaged.fplib.config.ConfigException;
import roadhog360.hogutils.repackaged.fplib.config.ConfigurationManager;

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
        try {
            if(SIDE == MixinEnvironment.Side.CLIENT) {
                ConfigurationManager.registerConfig(HogUtilsConfigs.Utils.F3AndTooltips.class);
            }
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }

        List<String> mixins = new ArrayList<>();
        mixins.add("hogtags.MixinOreDictionary");
        mixins.add("baseblock.MixinBlockDoor");
        mixins.add("baseblock.MixinBlockTrapDoor");
        if (SIDE == MixinEnvironment.Side.CLIENT) {
//            mixins.add("sample2");
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
