package roadhog360.hogutils.api.hogtags;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import roadhog360.hogutils.api.blocksanditems.IReferenceBase;
import roadhog360.hogutils.api.blocksanditems.utils.ItemMetaPair;
import roadhog360.hogutils.api.hogtags.helpers.ItemTags;

import java.util.List;
import java.util.Set;

/// Used by recipes to reference a HogTag
public class TagRecipeReference {
    private final String tag;
    private Set<ItemMetaPair> prevLookup = null;
    private final List<ItemStack> stackList = new ObjectArrayList<>();

    public TagRecipeReference(String tag) {
        this.tag = tag;
    }

    public List<ItemStack> reference() {
        Set<ItemMetaPair> lookup = ItemTags.getInTag(tag); //TODO replace this with actual lookup reference.
        if(prevLookup != lookup) {
            stackList.clear();
            lookup.stream().map(IReferenceBase::newItemStack).forEach(stackList::add);

            prevLookup = lookup;
        }
        return stackList;
    }
}
