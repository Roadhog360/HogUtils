package roadhog360.hogutils.handlers.event;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.world.BlockEvent;
import roadhog360.hogutils.api.Vec3i;
import roadhog360.hogutils.api.blocksanditems.block.ICustomActivateSound;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;
import roadhog360.hogutils.api.event.IUnfinalizedSoundEvent;

import java.util.Set;

public final class MultiBlockSoundEventHandler {

    public static final MultiBlockSoundEventHandler INSTANCE = new MultiBlockSoundEventHandler();
    private final Set<Vec3i> PLACED_BLOCKS = new ObjectOpenHashSet<>();

    private MultiBlockSoundEventHandler() {}

    @SubscribeEvent
    public void onOpenMenu(GuiOpenEvent event) {
        PLACED_BLOCKS.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlaceBlock(BlockEvent.PlaceEvent event) {
        if(!event.isCanceled() && event.block instanceof IMultiBlockSound) {
            PLACED_BLOCKS.add(new Vec3i(event.x, event.y, event.z));
        }
    }

    @SubscribeEvent
    public void onPlaySoundEvent(PlaySoundEvent17 event) {
        if (event.sound instanceof PositionedSound sound && FMLClientHandler.instance().getWorldClient() != null) {
            final World world = FMLClientHandler.instance().getWorldClient();
            final int x = MathHelper.floor_float(sound.getXPosF());
            final int y = MathHelper.floor_float(sound.getYPosF());
            final int z = MathHelper.floor_float(sound.getZPosF());
            final Block block = world.getBlock(x, y, z);

            if (block instanceof IMultiBlockSound mbs) {
                boolean isPlaceSound = PLACED_BLOCKS.remove(new Vec3i(x, y, z));
                IMultiBlockSound.SoundMode type;
                if (block.stepSound.getStepResourcePath().endsWith(event.name) && !isPlaceSound) {
                    type = IMultiBlockSound.SoundMode.HIT;
                } else if (block.stepSound.func_150496_b/*getPlaceSound*/().endsWith(event.name) || isPlaceSound) {
                    type = IMultiBlockSound.SoundMode.PLACE;
                } else {
                    type = IMultiBlockSound.SoundMode.BREAK;
                }

                Block.SoundType newSound = mbs.getSoundType(world, x, y, z, type);
                if(newSound != block.stepSound) {

                    sound.field_147664_a = new ResourceLocation(switch (type) {
                        case BREAK -> newSound.getBreakSound();
                        case PLACE -> newSound.func_150496_b();
                        default -> newSound.getStepResourcePath();
                    });
                    float volume = newSound.getVolume();
                    float pitch = newSound.getPitch();

                    float volumeVariation = sound.getVolume() - block.stepSound.getVolume();
                    float pitchVariation = sound.getPitch() - block.stepSound.getPitch();

                    sound.volume = volume + volumeVariation;
                    sound.field_147663_c = pitch + pitchVariation;
                }
            } else if (block instanceof ICustomActivateSound cas) {
                if (event.name.contains("random.chest") || event.name.contains("random.door") ||
                    ((block instanceof BlockButton || block instanceof BlockBasePressurePlate || block instanceof BlockDispenser)
                        && event.name.equals("random.click"))) {

                    String baseSoundName = cas.getSound(world, x, y, z, event.name);
                    if(baseSoundName != null) {
                        String extension = cas.getSuffix(world, x, y, z, event.name);

                        sound.field_147664_a = new ResourceLocation(baseSoundName + extension);
                        sound.volume = cas.getVolume(world, x, y, z, event.name, sound.getVolume());
                        sound.field_147663_c = cas.getPitch(world, x, y, z, event.name, sound.getPitch());
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlaySoundAtEntityEvent(PlaySoundAtEntityEvent event) {
        //Some mods fire null sounds, blech
        if (event.name == null || FMLClientHandler.instance().getWorldClient() == null) return;

        //TODO: Custom eat/drink sounds for food?
//        if (entity instanceof EntityPlayer player && event.name.equals("random.drink")) {
//            if (player.isUsingItem() && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemHoneyBottle) {
//                entity.playSound(Tags.MC_ASSET_VER + ":item.honey_bottle.drink" + ignore_suffix, 1, 1);
//                event.setCanceled(true);
//                return;
//            }
//        }

        Entity entity = event.entity;
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.posY - 0.20000000298023224D - entity.yOffset);
        int z = MathHelper.floor_double(entity.posZ);
        World world = FMLClientHandler.instance().getWorldClient();
        Block block = world.getBlock(x, y, z);

        if (block instanceof IMultiBlockSound mbs && block.stepSound.getStepResourcePath().equals(event.name)) {
            Block.SoundType newSound = mbs.getSoundType(world, x, y, z, IMultiBlockSound.SoundMode.WALK);
            if(newSound != block.stepSound) {
                float volume = newSound.getVolume();
                float pitch = newSound.getPitch();

                float volumeVariation = event.volume - block.stepSound.getVolume();
                float pitchVariation = event.pitch - block.stepSound.getPitch();

                event.name = newSound.getStepResourcePath();
                if (event instanceof IUnfinalizedSoundEvent unfinalized) {
                    unfinalized.setVolume(volume + volumeVariation);
                    unfinalized.setPitch(pitch + pitchVariation);
                }
            }
        }
    }
}
