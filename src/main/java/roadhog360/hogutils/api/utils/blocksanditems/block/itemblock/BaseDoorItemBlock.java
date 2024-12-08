package roadhog360.hogutils.api.utils.blocksanditems.block.itemblock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BaseDoorItemBlock extends ItemBlock {
    public BaseDoorItemBlock(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (side != 1)
            return false;
        y++;
        if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
            if (!field_150939_a/*blockInstance*/.canPlaceBlockAt(world, x, y, z))
                return false;
            ItemDoor.placeDoorBlock(world, x, y, z, MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3, field_150939_a); // blockInstance
            world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, this.field_150939_a/*blockInstance*/.stepSound.func_150496_b()/*getPlaceSound*/, (this.field_150939_a/*blockInstance*/.stepSound.getVolume() + 1.0F) / 2.0F, this.field_150939_a/*blockInstance*/.stepSound.getPitch() * 0.8F);
            stack.stackSize--;
            return true;
        }
        return false;
    }
}
