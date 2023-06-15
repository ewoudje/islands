package org.mashed.islands.mixin.accessors;

import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.EnumSet;

@Mixin(ChunkStatus.class)
public interface ChunkStatusAccessor {

    @Invoker(value = "register")
    static ChunkStatus register(String key, @Nullable ChunkStatus parent, int taskRange, EnumSet<Heightmap.Types> heightmaps, ChunkStatus.ChunkType type, ChunkStatus.GenerationTask generationTask) {
        throw new AssertionError();
    }
}
