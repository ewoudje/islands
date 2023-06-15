package org.mashed.islands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*
import net.minecraft.commands.arguments.ResourceKeyArgument
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceKey
import org.joml.Vector3i
import org.mashed.islands.generation.IslandGenerator
import org.mashed.islands.generation.island.GeneratingIsland
import kotlin.random.Random

object IslandCommands {
    private val generator = IslandGenerator(Random.nextLong())

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(literal("islands").then(
            literal("generate")
                .then(argument("type", ResourceKeyArgument.key(IslandTypes.ISLAND_TYPE_REGISTRY))
                    .then(argument("size", FloatArgumentType.floatArg(1f))
                        .executes(::generateIsland)))))
    }

    private fun generateIsland(ctx: CommandContext<CommandSourceStack>): Int {
        val source = ctx.source
        val level = source.level
        val player = source.playerOrException
        val pos = player.blockPosition()
        val type = ctx.getArgument("type", ResourceKey::class.java)
            .cast(IslandTypes.ISLAND_TYPE_REGISTRY).orElseThrow()

        val fetchedType = source.registryAccess().registryOrThrow(IslandTypes.ISLAND_TYPE_REGISTRY).getOrThrow(type)
        val size = FloatArgumentType.getFloat(ctx, "size")

        try {
            generator.makeIsland(level, Vector3i(pos.x, 0, pos.z), fetchedType, size)
        } catch (t: Throwable) {
            t.printStackTrace()
            source.sendFailure(TextComponent(t.message!!))
        }

        return 1
    }
}