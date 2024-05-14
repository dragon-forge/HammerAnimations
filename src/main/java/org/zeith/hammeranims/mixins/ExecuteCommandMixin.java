package org.zeith.hammeranims.mixins;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.impl.ExecuteCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.core.contents.commands.CommandParticle;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mixin(ExecuteCommand.class)
public abstract class ExecuteCommandMixin
{
	@Shadow
	private static ArgumentBuilder<CommandSource, ?> addConditional(CommandNode<CommandSource> pContext, ArgumentBuilder<CommandSource, ?> pBuilder, boolean pValue, ExecuteCommand.IBooleanTest pTest)
	{
		throw new UnsupportedOperationException();
	}
	
	@Inject(
			method = "addConditionals",
			at = @At(value = "RETURN")
	)
	private static void HammerAnimations_addConditionals(CommandNode<CommandSource> pParent, LiteralArgumentBuilder<CommandSource> pLiteral, boolean pIsIf, CallbackInfoReturnable<ArgumentBuilder<CommandSource, ?>> cir)
	{
		pLiteral.then(Commands.literal("hasbedrockparticle")
				.then(addConditional(pParent, Commands.argument("bedrock_particle_type", ResourceLocationArgument.id()).suggests(CommandParticle.effectSuggestor()), pIsIf,
						filter ->
						{
							Entity entity = filter.getSource().getEntity();
							if(entity instanceof ServerPlayerEntity)
							{
								ResourceLocation id = ResourceLocationArgument.getId(filter, "bedrock_particle_type");
								if(HammerAnimationsApi.particleContainers().containsKey(id))
									return true;
								CompletableFuture<Set<ResourceLocation>> f = CommandParticle.getOrRequest(filter.getSource().getPlayerOrException());
								return f.isDone() && f.join().contains(id);
							}
							return false;
						}
				))
		);
	}
}