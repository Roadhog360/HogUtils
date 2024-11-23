package roadhog360.hogutils.mixins.early.hogtags;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roadhog360.hogutils.api.hogtags.HogTagsHelper;

@Mixin(OreDictionary.class)
public class MixinOreDictionary {

    @Inject(method = "registerOreImpl", remap = false,
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/oredict/OreDictionary;getOreID(Ljava/lang/String;)I"))
    private static void registerTags(String name, ItemStack ore, CallbackInfo ci) {
        String[] tags = HogTagsHelper.Utils.convertOreDictToTags(name, false).toArray(new String[]{});
        if(tags.length > 0) {
            HogTagsHelper.ItemTags.addTags(ore.getItem(), ore.getItemDamage(), tags);
            Block block = Block.getBlockFromItem(ore.getItem());
            if (block != null) {
                HogTagsHelper.BlockTags.addTags(block, ore.getHasSubtypes() ? ore.getItemDamage() : OreDictionary.WILDCARD_VALUE, tags);
            }
        }
    }
}
