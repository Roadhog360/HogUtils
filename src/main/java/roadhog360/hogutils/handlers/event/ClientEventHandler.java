package roadhog360.hogutils.handlers.event;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import roadhog360.hogutils.api.GenericUtils;
import roadhog360.hogutils.api.hogtags.HogTags;

import java.util.Set;

public class ClientEventHandler {

    private ClientEventHandler() {}

    public static final ClientEventHandler INSTANCE = new ClientEventHandler();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void injectHogTagsDisplay(RenderGameOverlayEvent.Text event) {
        if (FMLClientHandler.instance().getClient().gameSettings.showDebugInfo) {
            MovingObjectPosition mop = GenericUtils.getMovingObjectPositionFromPlayer(
                FMLClientHandler.instance().getWorldClient(), FMLClientHandler.instance().getClientPlayerEntity(), false);
            if (mop != null) {
                Block lookingBlock = FMLClientHandler.instance().getWorldClient().getBlock(mop.blockX, mop.blockY, mop.blockZ);
                int lookingMeta = FMLClientHandler.instance().getWorldClient().getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
                Set<String> tags = HogTags.getTagsForBlockOrItem(lookingBlock, lookingMeta);

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
        if (event.showAdvancedItemTooltips) {
            Set<String> tags = HogTags.getTagsForBlockOrItem(event.itemStack.getItem(), event.itemStack.getItemDamage());
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
