package roadhog360.hogutils.api.utils;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemDye;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import roadhog360.hogutils.api.RegistryMapping;

import java.util.Arrays;
import java.util.Collection;

public final class GenericUtils {

    private GenericUtils() {}

    public static MovingObjectPosition getMovingObjectPositionFromEntity(World worldIn, Entity entity, boolean useLiquids) {
        float f = 1.0F;
        float f1 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * f;
        float f2 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * f;
        Vec3 vec3 = getVec3(worldIn, entity, f);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        if (entity instanceof EntityPlayerMP) {
            d3 = ((EntityPlayerMP) entity).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return worldIn.func_147447_a/*rayTraceBlocks*/(vec3, vec31, useLiquids, !useLiquids, false);
    }

    public static Vec3 getVec3(World worldIn, Entity entity, double f) {
        double eyeHeight = (worldIn.isRemote && entity instanceof EntityPlayer
            ? entity.getEyeHeight() - ((EntityPlayer) entity).getDefaultEyeHeight()
            : entity.getEyeHeight());
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * f;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * f + eyeHeight; // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * f;
        return Vec3.createVectorHelper(d0, d1, d2);
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

    /// If {@link World} is null, uses the client world. This will crash on a server, obviously, so be careful doing that.
    public static Pair<Block, Integer> getBlockAndMetaFromMOP(World world, MovingObjectPosition mop) {
        if(world == null) {
            world = FMLClientHandler.instance().getWorldClient();
        }
        return Pair.of(world.getBlock(mop.blockX, mop.blockY, mop.blockZ), world.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ));
    }

    /// If {@link World} is null, uses the client world. This will crash on a server, obviously, so be careful doing that.
    /// Returns a {@link RegistryMapping} for ease in getting a key from a {@link MovingObjectPosition}.
    public static RegistryMapping<Block> getRegistryMappingFromMOP(World world, MovingObjectPosition mop) {
        Pair<Block, Integer> pair = getBlockAndMetaFromMOP(world, mop);
        return RegistryMapping.of(pair.getLeft(), pair.getRight());
    }

    /// Returns the capital letter closest to the beginning of the string.
    /// Returns -1 if none exist.
    public static int indexOfLastCapitalLetter(String str) {
        return str.length() - 1 - indexOfFirstCapitalLetter(new StringBuilder(str).reverse().toString());
    }

    public static boolean anyStartsWith(String match, Collection<String> collection) {
        return collection.stream().anyMatch(s -> s.startsWith(match));
    }

    public static boolean anyEndsWith(String match, Collection<String> collection) {
        return collection.stream().anyMatch(s -> s.endsWith(match));
    }

    public static boolean anyContains(String match, Collection<String> collection) {
        return collection.stream().anyMatch(s -> s.contains(match));
    }
    private static Integer maxMeta;
    private static Integer minMeta;

    public static int getMaxBlockMetadata() {
        if (maxMeta == null) {
//            if (ModsList.NOT_ENOUGH_IDS.isLoaded() && ModsList.NOT_ENOUGH_IDS.isVersionNewerOrEqual("2.0.0")) {
//                maxMeta = (int) Short.MAX_VALUE;
//            } else if (ModsList.ENDLESS_IDS_BLOCKITEM.isLoaded()) {
//                maxMeta = 65536;
//            } else {
                maxMeta = 15;
//            }
        }
        return maxMeta;
    }

    public static int getMinBlockMetadata() {
        if (minMeta == null) {
//            if (ModsList.NOT_ENOUGH_IDS.isLoaded() && ModsList.NOT_ENOUGH_IDS.isVersionNewerOrEqual("2.0.0")) {
//                minMeta = (int) Short.MIN_VALUE;
//            } else { //EIDs has min meta 0 too, so we don't need to check for it
                minMeta = 0;
//            }
        }
        return minMeta;
    }

    public static boolean isBlockMetaInBounds(int meta) {
        return meta <= getMaxBlockMetadata() && meta >= getMinBlockMetadata();
    }

    public static boolean isBlockMetaInBoundsIgnoreWildcard(int meta) {
        return meta == OreDictionary.WILDCARD_VALUE || isBlockMetaInBounds(meta);
    }

    public static class Constants {
        public static final float[][] COLORS_RGB = EntitySheep.fleeceColorTable;

        public static final String[] COLORS_SNAKE_CASE = ItemDye.field_150921_b;
        /// Used by the OreDictionary
        public static final String[] COLORS_CAMEL_CASE = ItemDye.field_150923_a;
        // Difference is just "lightGray" instead of "silver"
        public static final String[] MODERN_COLORS_SNAKE_CASE;
        // Difference is just "light_gray" instead of "silver"
        public static final String[] MODERN_COLORS_CAMEL_CASE;
        static {
            MODERN_COLORS_SNAKE_CASE = Arrays.copyOf(COLORS_SNAKE_CASE, 16);
            MODERN_COLORS_SNAKE_CASE[7] = "light_gray";

            MODERN_COLORS_CAMEL_CASE = Arrays.copyOf(COLORS_CAMEL_CASE, 16);
            MODERN_COLORS_CAMEL_CASE[7] = "lightGray";
        }
    }
}
