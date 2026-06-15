package roadhog360.hogutils.api.hogtags.handlers.impl;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import roadhog360.hogutils.api.blocksanditems.utils.MetaPackingUtils;
import roadhog360.hogutils.api.hogtags.handlers.AbstractTagHandler;
import roadhog360.hogutils.api.utils.CachedSupplier;

public abstract class TagHandlerMeta extends AbstractTagHandler<Long, LongSet> {
    private static final LongHash.Strategy strategy =
        new LongHash.Strategy() {
            @Override
            public int hashCode(long e) {
                int firstBits = (int) e;
                return Long.hashCode(firstBits);
            }

            @Override
            public boolean equals(long a, long b) {
                return a == b;
            }
        };

    private static final Object2ObjectFunction<LongSet, LongSet> longSetFactory = (o) -> o instanceof LongSet set ? LongSets.unmodifiable(set) :
        new LongOpenCustomHashSet(strategy) {
        @Override
        public boolean contains(long k) {
            if(super.contains(k)) {
                return true;
            }
            return MetaPackingUtils.getMetaFromLong(k) != OreDictionary.WILDCARD_VALUE && super.contains(MetaPackingUtils.pack(MetaPackingUtils.getIDFromLong(k), OreDictionary.WILDCARD_VALUE));
        }
    };

    protected TagHandlerMeta(String helpText, String handlerID) {
        super(helpText, handlerID, new Long2ObjectOpenHashMap<>(), new Long2ObjectOpenHashMap<>(), new Object2ObjectOpenHashMap<>(), longSetFactory);

        extraMembers = (Object2ObjectFunction<Long, ObjectSet<String>>) key -> {
            if (key instanceof Long owner) {
                if (MetaPackingUtils.getMetaFromLong(owner) != OreDictionary.WILDCARD_VALUE) {
                    return getTags(MetaPackingUtils.pack(MetaPackingUtils.getIDFromLong(owner), OreDictionary.WILDCARD_VALUE)).get();
                }
            }
            return ObjectSets.emptySet();
        };
    }

    @Override
    public void dump(boolean flat) {

    }

    public static final class ItemTags extends TagHandlerMeta {
        public ItemTags() {
            super("modid:item or modid:item:meta", "minecraft:item");
        }

        public CachedSupplier<ObjectSet<String>> getTags(Item item, int meta) {
            return super.getTags(MetaPackingUtils.pack(item, meta));
        }

        public CachedSupplier<ObjectSet<String>> getTags(ItemStack stack) {
            return super.getTags(MetaPackingUtils.pack(stack));
        }

        public void put(Item item, int meta, String tag) {
            super.put(MetaPackingUtils.pack(item, meta), tag);
        }

        public void put(ItemStack stack, String tag) {
            super.put(MetaPackingUtils.pack(stack), tag);
        }

        public void remove(Item item, int meta, String tag) {
            super.remove(MetaPackingUtils.pack(item, meta), tag);
        }

        public void remove(ItemStack stack, String tag) {
            super.remove(MetaPackingUtils.pack(stack), tag);
        }

        public boolean isIn(Item item, int meta, String tag) {
            return super.isIn(MetaPackingUtils.pack(item, meta), tag);
        }

        public boolean isIn(ItemStack stack, String tag) {
            return super.isIn(MetaPackingUtils.pack(stack), tag);
        }

        @Override
        public String getNameFromObject(Long member) {
            return "";
        }

        @Override
        public Long getObjectFromName(String string) {
            return null;
        }
    }

    public static final class BlockTags extends TagHandlerMeta {
        public BlockTags() {
            super("modid:block or modid:block:meta", "minecraft:block");
        }

        public CachedSupplier<ObjectSet<String>> getTags(Block block, int meta) {
            return super.getTags(MetaPackingUtils.pack(block, meta));
        }

        public CachedSupplier<ObjectSet<String>> getTags(Pair<Block, Integer> stack) {
            return super.getTags(MetaPackingUtils.pack(stack.first(), stack.second()));
        }

        public CachedSupplier<ObjectSet<String>> getTags(org.apache.commons.lang3.tuple.Pair<Block, Integer> stack) {
            return super.getTags(MetaPackingUtils.pack(stack.getLeft(), stack.getRight()));
        }

        public void put(Block block, int meta, String tag) {
            super.put(MetaPackingUtils.pack(block, meta), tag);
        }

        public void put(Pair<Block, Integer> stack, String tag) {
            super.put(MetaPackingUtils.pack(stack.first(), stack.second()), tag);
        }

        public void put(org.apache.commons.lang3.tuple.Pair<Block, Integer> stack, String tag) {
            super.put(MetaPackingUtils.pack(stack.getLeft(), stack.getRight()), tag);
        }

        public void remove(Block block, int meta, String tag) {
            super.remove(MetaPackingUtils.pack(block, meta), tag);
        }

        public void remove(Pair<Block, Integer> stack, String tag) {
            super.remove(MetaPackingUtils.pack(stack.first(), stack.second()), tag);
        }

        public void remove(org.apache.commons.lang3.tuple.Pair<Block, Integer> stack, String tag) {
            super.remove(MetaPackingUtils.pack(stack.getLeft(), stack.getRight()), tag);
        }

        public boolean isIn(Block block, int meta, String tag) {
            return super.isIn(MetaPackingUtils.pack(block, meta), tag);
        }

        public boolean isIn(Pair<Block, Integer> stack, String tag) {
            return super.isIn(MetaPackingUtils.pack(stack.first(), stack.second()), tag);
        }

        public boolean isIn(org.apache.commons.lang3.tuple.Pair<Block, Integer> stack, String tag) {
            return super.isIn(MetaPackingUtils.pack(stack.getLeft(), stack.getRight()), tag);
        }

        @Override
        public String getNameFromObject(Long member) {
            Block block = MetaPackingUtils.getBlockFromLong(member);
            String meta = String.valueOf(MetaPackingUtils.getMetaFromLong(member));
            if(meta.equals("-1")) meta = "*";
            return block != null && block.delegate != null ? block.delegate.name() + ":" + meta : "<ERROR>";
        }

        @Override
        public Long getObjectFromName(String string) {
            return null;
        }
    }
}
