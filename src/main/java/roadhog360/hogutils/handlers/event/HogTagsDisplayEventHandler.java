package roadhog360.hogutils.handlers.event;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.hogtags.HogTagsHelper;
import roadhog360.hogutils.api.utils.GenericUtils;

import java.util.List;

public class HogTagsDisplayEventHandler {

    private HogTagsDisplayEventHandler() {}

    public static final HogTagsDisplayEventHandler INSTANCE = new HogTagsDisplayEventHandler();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void injectHogTagsDisplay(RenderGameOverlayEvent.Text event) {
        if (FMLClientHandler.instance().getClient().gameSettings.showDebugInfo) {
            World world = FMLClientHandler.instance().getWorldClient();
            EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();

            // Left hand side (biome tags)
            BiomeGenBase biome = world.getBiomeGenForCoords(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ));
            if(biome != null) {
                List<String> tags = HogTagsHelper.BiomeTags.getTags(biome);
                if(!tags.isEmpty()) {
                    event.left.add(null);
                    event.left.add("HogTags for " + biome.biomeName
                        + ": (" + HogTagsHelper.BiomeTags.CONTAINER_ID + " tag pool)");
                    for (String tag : tags) {
                        event.left.add("#" + tag);
                    }
                }
            }

            // Right hand side (block tags)
            MovingObjectPosition mop = GenericUtils.getMovingObjectPositionFromEntity(world, player, false);
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                Pair<Block, Integer> blockAndMeta = GenericUtils.getBlockAndMetaFromMOP(null, mop);
                Block lookingBlock = blockAndMeta.getLeft();
                int lookingMeta = blockAndMeta.getRight();
                List<String> tags = HogTagsHelper.BlockTags.getTags(lookingBlock, lookingMeta);

                if(!tags.isEmpty()) {
                    event.right.add(null);
                    event.right.add("HogTags for " + Block.blockRegistry.getNameForObject(lookingBlock) + ":" + lookingMeta
                        + ": (" + HogTagsHelper.BlockTags.CONTAINER_ID + " tag pool)");
                    for (String tag : tags) {
                        event.right.add("#" + tag);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void injectHogTagsTooltip(ItemTooltipEvent event) {
        if (event.showAdvancedItemTooltips && event.itemStack != null && event.itemStack.getItem() != null) {
            List<String> tags = HogTagsHelper.ItemTags.getTags(event.itemStack.getItem(), event.itemStack.getItemDamage());
            if(!tags.isEmpty()) {
                if (GuiContainer.isCtrlKeyDown()) {
                    event.toolTip.add("\u00a78HogTags: (" + HogTagsHelper.ItemTags.CONTAINER_ID + " tag pool)");
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
