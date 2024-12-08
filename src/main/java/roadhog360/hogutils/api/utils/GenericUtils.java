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
import roadhog360.hogutils.core.ModsList;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeMap;

public final class GenericUtils {

    private GenericUtils() {}

    public static MovingObjectPosition getMovingObjectPositionFromEntity(World worldIn, Entity entity, boolean useNonSelectable, boolean ignoreNonCollidable) {
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
        return worldIn.func_147447_a/*rayTraceBlocks*/(vec3, vec31, useNonSelectable, ignoreNonCollidable, false);
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
            if (ModsList.NOT_ENOUGH_IDS.isLoaded() && ModsList.NOT_ENOUGH_IDS.isVersionNewerOrEqual("2.0.0")) {
                maxMeta = (int) Short.MAX_VALUE;
            } else if (ModsList.ENDLESS_IDS_BLOCKITEM.isLoaded()) {
                maxMeta = 65536;
            } else {
                maxMeta = 15;
            }
        }
        return maxMeta;
    }

    public static int getMinBlockMetadata() {
        if (minMeta == null) {
            if (ModsList.NOT_ENOUGH_IDS.isLoaded() && ModsList.NOT_ENOUGH_IDS.isVersionNewerOrEqual("2.0.0")) {
                minMeta = (int) Short.MIN_VALUE;
            } else { //EIDs has min meta 0 too, so we don't need to check for it
                minMeta = 0;
            }
        }
        return minMeta;
    }

    public static boolean isBlockMetaInBounds(int meta) {
        return meta <= getMaxBlockMetadata() && meta >= getMinBlockMetadata();
    }

    public static boolean isBlockMetaInBoundsIgnoreWildcard(int meta) {
        return meta == OreDictionary.WILDCARD_VALUE || isBlockMetaInBounds(meta);
    }

    /// Code by Ben-Hur Langoni Junior on Stack Overflow, [original answer](https://stackoverflow.com/a/19759564)
    public static final class RomanNumbers {
        private final static TreeMap<Integer, String> map = new TreeMap<>();
        static {
            map.put(1000, "M");
            map.put(900, "CM");
            map.put(500, "D");
            map.put(400, "CD");
            map.put(100, "C");
            map.put(90, "XC");
            map.put(50, "L");
            map.put(40, "XL");
            map.put(10, "X");
            map.put(9, "IX");
            map.put(5, "V");
            map.put(4, "IV");
            map.put(1, "I");
        }

        private static String getRomanNumeral(int number) {
            String roman = toRoman(Math.abs(number));
            if(number < 0) {
                roman = "-" + roman;
            }
            return roman;
        }

        private static String toRoman(int number) {
            if(number == 0) {
                return "NONE";
            }
            int l =  map.floorKey(number);
            if (number == l) {
                return map.get(number);
            }
            return map.get(l) + toRoman(number-l);
        }
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
