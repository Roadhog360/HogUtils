package roadhog360.hogutils.api.hogtags.handlers.impl;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.world.biome.BiomeGenBase;
import roadhog360.hogutils.Tags;
import roadhog360.hogutils.api.hogtags.handlers.AbstractTagHandler;
import roadhog360.hogutils.api.utils.CachedSupplier;

public class BiomeTags extends AbstractTagHandler<Integer, IntSet> {
    public BiomeTags() {
        super("modid:biome or \"Biome Name\" or Biome ID", Tags.MOD_ID + ":biome",
            new Int2ObjectOpenHashMap<>(), new Int2ObjectOpenHashMap<>(), new Object2ObjectOpenHashMap<>(), set -> set != null ? IntSets.unmodifiable(set) : new IntOpenHashSet());
    }

    @Override
    public String getNameFromObject(Integer member) {
        if(member == null || member < 0 || member >= BiomeGenBase.getBiomeGenArray().length) {
            return null;
        }
        return BiomeGenBase.getBiomeGenArray()[member].biomeName;
    }

    @Override
    public Integer getObjectFromName(String string) {
        Integer id = null;
        try {
            BiomeGenBase biome = BiomeGenBase.getBiome(Integer.valueOf(string));
            if(biome != null) {
                id = biome.biomeID;
            }
        } catch (Exception e) {
            for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
                if(biome.biomeName.equals(string)) {
                    id = biome.biomeID;
                    break;
                }
            }
        }
        return id;
    }

    public CachedSupplier<ObjectSet<String>> getTags(BiomeGenBase stack) {
        return super.getTags(stack.biomeID);
    }

    public void put(BiomeGenBase stack, String tag) {
        super.put(stack.biomeID, tag);
    }

    public void remove(BiomeGenBase stack, String tag) {
        super.remove(stack.biomeID, tag);
    }

    public boolean isIn(BiomeGenBase stack, String tag) {
        return super.isIn(stack.biomeID, tag);
    }
}
