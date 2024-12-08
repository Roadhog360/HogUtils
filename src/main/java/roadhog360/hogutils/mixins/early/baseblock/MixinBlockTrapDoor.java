package roadhog360.hogutils.mixins.early.baseblock;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import roadhog360.hogutils.api.utils.blocksanditems.block.ICustomDoorSound;

@Mixin(BlockTrapDoor.class)
public class MixinBlockTrapDoor {
    @WrapOperation(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playAuxSFXAtEntity(Lnet/minecraft/entity/player/EntityPlayer;IIIII)V"))
    private void replaceDoorSoundInBlockActivate(World world, EntityPlayer player, int auxID, int x, int y, int z, int unk1, Operation<Void> original) {
        if(this instanceof ICustomDoorSound door) {
            door.doDoorSounds(world, x, y, z);
        } else {
            original.call(world, player, auxID, x, y, z, unk1);
        }
    }

    @WrapOperation(method = "func_150120_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playAuxSFXAtEntity(Lnet/minecraft/entity/player/EntityPlayer;IIIII)V"))
    private void replaceDoorSoundInBlockUpdate(World world, EntityPlayer player, int auxID, int x, int y, int z, int unk1, Operation<Void> original) {
        if(this instanceof ICustomDoorSound door) {
            door.doDoorSounds(world, x, y, z);
        } else {
            original.call(world, player, auxID, x, y, z, unk1);
        }
    }
}
