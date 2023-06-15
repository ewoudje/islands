package org.mashed.islands.generation.island

import com.fasterxml.jackson.annotation.JsonIgnore
import org.mashed.islands.generation.IslandBuilder
import java.util.concurrent.CompletableFuture

interface GeneratingIsland {
    val size: Float
    val seed: Int
    val type: IslandType
    val status: IslandStatus
    @get:JsonIgnore
    val next: CompletableFuture<GeneratingIsland>?
}

data class EmptyGeneratedIsland(
    override val size: Float,
    override val seed: Int,
    override val type: IslandType,
    @JsonIgnore
    val builder: IslandBuilder,
    @JsonIgnore
    val owner: WrappingIsland
) : GeneratingIsland {
    override val status: IslandStatus
        get() = IslandStatus.NONE

    override val next: CompletableFuture<GeneratingIsland>? = CompletableFuture.supplyAsync<GeneratingIsland> {
        ShapedGeneratedIsland(size, seed, type, type.generateShape(this), builder, owner)
    }.whenCompleteAsync { r, t ->
        if (t != null) {
            throw t
        } else owner.island = r
    }
}

data class ShapedGeneratedIsland(
    override val size: Float,
    override val seed: Int,
    override val type: IslandType,
    val shape: IslandShape,
    @JsonIgnore
    val builder: IslandBuilder,
    @JsonIgnore
    val owner: WrappingIsland
) : GeneratingIsland {
    override val status: IslandStatus
        get() = IslandStatus.SHAPED

    override val next: CompletableFuture<GeneratingIsland>? = CompletableFuture.supplyAsync<GeneratingIsland> {
        type.iterateShape(this, shape)
        IteratedGeneratedIsland(size, seed, type, shape, builder, owner)
    }.whenCompleteAsync { r, t ->
        if (t != null) {
            throw t
        } else owner.island = r
    }
}

data class IteratedGeneratedIsland(
    override val size: Float,
    override val seed: Int,
    override val type: IslandType,
    val shape: IslandShape,
    @JsonIgnore
    val builder: IslandBuilder,
    @JsonIgnore
    val owner: WrappingIsland
) : GeneratingIsland {
    override val status: IslandStatus
        get() = IslandStatus.ITERATED

    override val next: CompletableFuture<GeneratingIsland>? = CompletableFuture.supplyAsync<GeneratingIsland> {
        type.applyShape(this, shape, builder)
        builder.finish()

        AppliedGeneratedIsland(size, seed, type, builder)
    }.whenCompleteAsync { r, t ->
        if (t != null) {
            throw t
        } else owner.island = r
    }
}

data class AppliedGeneratedIsland(
    override val size: Float,
    override val seed: Int,
    override val type: IslandType,
    @JsonIgnore
    val builder: IslandBuilder
) : GeneratingIsland {
    override val status: IslandStatus
        get() = IslandStatus.DONE

    override val next: CompletableFuture<GeneratingIsland>? = null
}

class WrappingIsland(
    var island: GeneratingIsland?
) : GeneratingIsland {
    override val size: Float
        get() = island!!.size
    override val seed: Int
        get() = island!!.seed
    override val type: IslandType
        get() = island!!.type
    override val status: IslandStatus
        get() = island!!.status
    override val next: CompletableFuture<GeneratingIsland>?
        get() = island!!.next

    override fun toString(): String = "GeneratingIsland(status=$status, type=$type, size=$size, seed=$seed)"
}