package org.zeith.hammeranims.mixins;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.hammeranims.core.contents.commands.CommandParticle;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin
{
	@Shadow
	private static ArgumentBuilder<CommandSourceStack, ?> addConditional(CommandNode<CommandSourceStack> pContext, ArgumentBuilder<CommandSourceStack, ?> pBuilder, boolean pValue, ExecuteCommand.CommandPredicate pTest)
	{
		throw new UnsupportedOperationException();
	}
	
	@Inject(
			method = "addConditionals",
			at = @At(value = "RETURN")
	)
	private static void HammerAnimations_addConditionals(CommandNode<CommandSourceStack> pParent, LiteralArgumentBuilder<CommandSourceStack> pLiteral, boolean pIsIf, CommandBuildContext pContext, CallbackInfoReturnable<ArgumentBuilder<CommandSourceStack, ?>> cir)
	{
		pLiteral.then(Commands.literal("hasbedrockparticle")
				.then(addConditional(pParent, Commands.argument("bedrock_particle_type", ResourceLocationArgument.id()).suggests(CommandParticle.effectSuggestor()), pIsIf,
						filter ->
						{
							var entity = filter.getSource().getEntity();
							if(entity instanceof ServerPlayer sp)
							{
								var f = CommandParticle.getOrRequest(sp);
								return f.isDone() && f.join().contains(ResourceLocationArgument.getId(filter, "bedrock_particle_type"));
							}
							return false;
						}
				))
		);
	}
}