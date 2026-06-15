package roadhog360.hogutils.mixins.early.event;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import roadhog360.hogutils.api.event.RecipeRegisterEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@Mixin(value = CraftingManager.class, priority = Integer.MAX_VALUE)
public class MixinCraftingManager {

    @Shadow
    private List recipes;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;<init>()V"))
    private void replaceRecipeList(CallbackInfo ci) {
        recipes = new RecipeListWrapper(recipes);
    }

    private static boolean fireEvent(IRecipe recipe) {
        return MinecraftForge.EVENT_BUS.post(new RecipeRegisterEvent(recipe));
    }

    private class RecipeListWrapper implements List<IRecipe> {
        private final List<IRecipe> wrapped;

        RecipeListWrapper(List<IRecipe> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public boolean add(IRecipe iRecipe) {
            if (!fireEvent(iRecipe)) return wrapped.add(iRecipe);;
            return false;
        }

        @Override
        public void add(int index, IRecipe element) {
            if (!fireEvent(element)) wrapped.add(index, element);
        }

        @Override
        public int size() {
            return wrapped.size();
        }

        @Override
        public boolean isEmpty() {
            return wrapped.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return wrapped.contains(o);
        }

        @Override
        public @NotNull Iterator<IRecipe> iterator() {
            return wrapped.iterator();
        }

        @Override
        public @NotNull Object[] toArray() {
            return wrapped.toArray();
        }

        @Override
        public @NotNull <T> T[] toArray(@NotNull T[] a) {
            return wrapped.toArray(a);
        }

        @Override
        public boolean remove(Object o) {
            return wrapped.remove(o);
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return wrapped.contains(c);
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends IRecipe> c) {
            return wrapped.addAll(c);
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends IRecipe> c) {
            return wrapped.addAll(index, c);
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return wrapped.removeAll(c);
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return wrapped.retainAll(c);
        }

        @Override
        public void clear() {
            wrapped.clear();
        }

        @Override
        public boolean equals(Object o) {
            return wrapped.equals(o);
        }

        @Override
        public int hashCode() {
            return wrapped.hashCode();
        }

        @Override
        public IRecipe get(int index) {
            return wrapped.get(index);
        }

        @Override
        public IRecipe set(int index, IRecipe element) {
            return wrapped.set(index, element);
        }

        @Override
        public IRecipe remove(int index) {
            return wrapped.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return wrapped.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return wrapped.lastIndexOf(o);
        }

        @Override
        public @NotNull ListIterator<IRecipe> listIterator() {
            return wrapped.listIterator();
        }

        @Override
        public @NotNull ListIterator<IRecipe> listIterator(int index) {
            return wrapped.listIterator(index);
        }

        @Override
        public @NotNull List<IRecipe> subList(int fromIndex, int toIndex) {
            return wrapped.subList(fromIndex, toIndex);
        }
    }
}
