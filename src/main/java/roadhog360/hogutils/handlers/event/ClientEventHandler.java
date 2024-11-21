package roadhog360.hogutils.handlers.event;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.hogtags.HogTags;
import roadhog360.hogutils.api.utils.GenericUtils;

import java.util.List;

public class ClientEventHandler {

    private ClientEventHandler() {}

    public static final ClientEventHandler INSTANCE = new ClientEventHandler();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void injectHogTagsDisplay(RenderGameOverlayEvent.Text event) {
        if (FMLClientHandler.instance().getClient().gameSettings.showDebugInfo) {
            MovingObjectPosition mop = GenericUtils.getMovingObjectPositionFromEntity(
                FMLClientHandler.instance().getWorldClient(), FMLClientHandler.instance().getClientPlayerEntity(), false);
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                Pair<Block, Integer> blockAndMeta = GenericUtils.getBlockAndMetaFromMOP(null, mop);
                Block lookingBlock = blockAndMeta.getLeft();
                int lookingMeta = blockAndMeta.getRight();
                List<String> tags = HogTags.BlockTags.getTags(lookingBlock, lookingMeta);

                if(!tags.isEmpty()) {
                    event.right.add(null);
                    event.right.add("HogTags info for block: " + Block.blockRegistry.getNameForObject(lookingBlock) + ":" + lookingMeta);
                    for (String tag : tags) {
                        event.right.add("#" + tag);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void injectHogTagsTooltip(ItemTooltipEvent event) {
        if (event.showAdvancedItemTooltips && event.itemStack != null) {
            List<String> tags = HogTags.ItemTags.getTags(event.itemStack.getItem(), event.itemStack.getItemDamage());
            if(!tags.isEmpty()) {
                if (GuiContainer.isCtrlKeyDown()) {
                    event.toolTip.add("\u00a78HogTags:");
                    for (String tag : tags) {
                        event.toolTip.add("#" + tag);
                    }
                } else {
                    event.toolTip.add("\u00a78HogTags: " + tags.size() + " Tag(s)");
                }
            }
        }
    }
}
