package roadhog360.hogutils.api.blocksanditems;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import javax.annotation.Nullable;

public final class BaseHelper {
    private BaseHelper() {}

    public static String getTextureName(String name, @Nullable String domain, @Nullable String subfolder) {
        String finalName = "";

        if(domain != null) {
            finalName = domain + ":";
        }

        if(subfolder != null) {
            finalName = finalName + subfolder + "/";
        }

        if(name.contains(":") && domain != null) {
            finalName += name.substring(0, name.indexOf(":"));
        } else {
            finalName += name;
        }

        return finalName;
    }

    public static String getUnlocalizedName(String name, @Nullable String domain) {
        return getTextureName(name, domain, null).replace(':', '.');
    }

    /// For now, contains hardcoded lists of commonly used materials for block names
    /// Pretty basic for now, may be expanded in the future
    /// Used by walls, fences, and stairs to change stuff like "tiles_wall" to "tile_wall"
    public static String depluralizeName(String name) {
        if(name.endsWith("tiles")) {
            return name.replace("tiles", "tile");
        }
        if(name.endsWith("bricks")) {
            return name.replace("bricks", "brick");
        }
        if(name.endsWith("planks")) {
            return name.replace("planks", "plank");
        }
        return name;
    }

    public static void setupStepSound(Block block) {
        Material material = block.getMaterial();
        if (material == Material.wood || material == Material.gourd) {
            block.stepSound = Block.soundTypeWood;
        } else if(material == Material.tnt || material == Material.vine || material == Material.coral || material == Material.grass
            || material == Material.plants || material == Material.sponge || material == Material.leaves) {
            block.stepSound = Block.soundTypeGrass;
        } else if (material == Material.ground || material == Material.clay) {
            block.stepSound = Block.soundTypeGravel;
        } else if (material == Material.anvil) {
            block.stepSound = Block.soundTypeAnvil;
        } else if (material == Material.cake || material == Material.cactus || material == Material.carpet || material == Material.fire
            || material == Material.cloth) {
            block.stepSound = Block.soundTypeCloth;
        } else if (material == Material.glass || material == Material.portal) {
            block.stepSound = Block.soundTypeGlass;
        }
    }

    //TODO: Auto harvest levels?
}
