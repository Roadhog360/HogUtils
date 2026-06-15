package roadhog360.hogutils.handlers.event;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.event.OreDictionaryToHogTagsEvent;
import roadhog360.hogutils.api.hogtags.HogTags;
import roadhog360.hogutils.api.hogtags.HogTagsOreDictionaryHelper;

import java.util.List;

@EventBusSubscriber(phase = Phase.CONSTRUCT)
public class OreRegisterEventHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void oreRegisterEventHandler(OreDictionary.OreRegisterEvent event) {
        ItemStack ore = event.Ore;
        String name = event.Name;
        if(ore.getItem() != null) {
            List<String> tags = HogTagsOreDictionaryHelper.getHogTagsForOreDictionary(name, ore, false);
            if (!tags.isEmpty() && !MinecraftForge.EVENT_BUS.post(new OreDictionaryToHogTagsEvent(name, ore, tags))) {
                String[] tagArray = tags.toArray(new String[]{});
                Block block = Block.getBlockFromItem(ore.getItem());
                for(String tag : tagArray) {
                    HogTags.ITEMS.put(ore, tag);
                    if (block != null) {
                        HogTags.BLOCKS.put(block, ore.getHasSubtypes() ? ore.getItemDamage() : OreDictionary.WILDCARD_VALUE, tag);
                    }
                }
            }
        }
    }
}
