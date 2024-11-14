package roadhog360.hogutils.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public final class GenericUtils {

    private GenericUtils() {}

    public static MovingObjectPosition getMovingObjectPositionFromPlayer(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
        float f = 1.0F;
        float f1 = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * f;
        float f2 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * f;
        Vec3 vec3 = getVec3(worldIn, playerIn, (double) f);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        if (playerIn instanceof EntityPlayerMP) {
            d3 = ((EntityPlayerMP) playerIn).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return worldIn.func_147447_a/*rayTraceBlocks*/(vec3, vec31, useLiquids, !useLiquids, false);
    }

    public static Vec3 getVec3(World worldIn, EntityPlayer playerIn, double f) {
        double d0 = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * f;
        double d1 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * f + (double) (worldIn.isRemote ? playerIn.getEyeHeight() - playerIn.getDefaultEyeHeight() : playerIn.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        return vec3;
    }

    /// Returns the capital letter closest to the end of the string.
    /// Returns -1 if none exist.
    public static int indexOfFirstCapitalLetter(String str) {
        for(int i = 0; i < str.length(); i++) {
            if(Character.isUpperCase(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /// Returns the capital letter closest to the beginning of the string.
    /// Returns -1 if none exist.
    public static int indexOfLastCapitalLetter(String str) {
        return str.length() - 1 - indexOfFirstCapitalLetter(new StringBuilder(str).reverse().toString());
    }
}
