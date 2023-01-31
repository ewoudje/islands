package org.mashed.islands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.entity.player.Player
import org.joml.Vector3i
import org.mashed.islands.generation.IslandGenerator
import org.mashed.islands.generation.island.IslandState
import org.mashed.islands.generation.island.PlainIsland
import org.valkyrienskies.mod.common.util.toJOML
import kotlin.random.Random

object IslandCommands {
    private val generator = IslandGenerator(Random.nextLong())

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(literal("islands").then(
            literal("generate")
                .then(argument("size", FloatArgumentType.floatArg(1f))
                    .executes(::generateIsland))))
    }

    private fun generateIsland(ctx: CommandContext<CommandSourceStack>): Int {
        val source = ctx.source
        val level = source.level
        val player = source.playerOrException
        val pos = player.blockPosition()

        val island = IslandState(FloatArgumentType.getFloat(ctx, "size"), Random.nextInt(), PlainIsland)

        try {
            val builder = generator.startIsland(level, Vector3i(pos.x, 0, pos.z))
            generator.generateIsland(builder, island)
            builder.clean()
        } catch (t: Throwable) {
            t.printStackTrace()
            source.sendFailure(TextComponent(t.message!!))
        }

        return 1
    }
}