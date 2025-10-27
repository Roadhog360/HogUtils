package roadhog360.hogutils.api.world;

public interface IGeneratingCheck {
    /// Checks if this world is currently running generation code. If false, worldgen code is not running.
    boolean hu$isGenerating();
}
