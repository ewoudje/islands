package org.mashed.islands.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.UpgradeData;
import org.mashed.islands.generation.IslandProtoChunk;
import org.mashed.islands.generation.MyChunkStatuses;
import org.mashed.islands.generation.island.GeneratingIsland;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

@Mixin(ChunkMap.class)
public abstract class MixinChunkMap {

    @Shadow
    @Final
    private ServerLevel level;

    @Shadow public abstract CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> schedule(ChunkHolder holder, ChunkStatus status);

    @Shadow protected abstract CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> getChunkRangeFuture(ChunkPos chunkPos, int i, IntFunction<ChunkStatus> intFunction);

    @Inject(method = "method_17256",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ChunkMap;markPositionReplaceable(Lnet/minecraft/world/level/ChunkPos;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    void createProtoChunk(ChunkPos chunkPos, CallbackInfoReturnable<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> cir) {
        ServerShip ship = VSGameUtilsKt.getShipManagingPos(level, chunkPos);
        if (ship != null) {
            GeneratingIsland island = ship.getAttachment(GeneratingIsland.class);
            if (island != null) {
                cir.setReturnValue(Either.left(new IslandProtoChunk(
                        chunkPos,
                        UpgradeData.EMPTY,
                        level,
                        level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY),
                        null,
                        island
                )));
            }
        }
    }

    @Inject(method = "schedule", at = @At("HEAD"), cancellable = true)
    void scheduleHead(ChunkHolder holder, ChunkStatus status, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        ServerShip ship = VSGameUtilsKt.getShipManagingPos(level, holder.getPos());
        if (status == ChunkStatus.FULL && ship != null) {
            GeneratingIsland island = ship.getAttachment(GeneratingIsland.class);
            if (island != null) {
                cir.setReturnValue(this.schedule(holder, MyChunkStatuses.INSTANCE.getFULL_ISLAND()));
            }
        }
    }

    @Redirect(method = "prepareAccessibleChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;getChunkRangeFuture(Lnet/minecraft/world/level/ChunkPos;ILjava/util/function/IntFunction;)Ljava/util/concurrent/CompletableFuture;"))
    CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> redirectGetChunkRangeFuture(
            ChunkMap chunkMap,
            ChunkPos chunkPos,
            int i,
            IntFunction<ChunkStatus> intFunction) {
        ServerShip ship = VSGameUtilsKt.getShipManagingPos(level, chunkPos);
        if (ship != null) {
            GeneratingIsland island = ship.getAttachment(GeneratingIsland.class);
            if (island != null) {
                return getChunkRangeFuture(chunkPos, i, (int j) -> MyChunkStatuses.INSTANCE.getFULL_ISLAND());
            }
        }

        return getChunkRangeFuture(chunkPos, i, intFunction);
    }

}
