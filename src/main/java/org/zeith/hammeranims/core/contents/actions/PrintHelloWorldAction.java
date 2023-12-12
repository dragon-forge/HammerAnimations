package org.zeith.hammeranims.core.contents.actions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ChatType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animsys.actions.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammerlib.util.java.Cast;

import java.util.UUID;

public class PrintHelloWorldAction
		extends AnimationAction
{
	@Override
	public HelloWorldActionInstance defaultInstance()
	{
		return createInstance();
	}
	
	@NotNull
	@Override
	protected HelloWorldActionInstance createInstance()
	{
		return new HelloWorldActionInstance(this);
	}
	
	@Override
	public void execute(AnimationActionInstance instance, AnimationLayer layer)
	{
		World world = getAnimationWorld(layer);
		
		String msg = Cast.optionally(instance, HelloWorldActionInstance.class)
				.map(HelloWorldActionInstance::message)
				.orElse("");
		
		if(world instanceof ServerWorld)
			world.getServer().getPlayerList().broadcastMessage(InstanceHelpers.componentText(
					"Hello from server! Owner " + layer.system.owner + "@" + getAnimationPos(layer) +
					" has finished animation " +
					layer.getCurrentAnimation().getLocation() + " with message: " + msg), ChatType.CHAT, UUID.randomUUID());
		else
		{
			for(PlayerEntity player : world.players())
			{
				player.sendMessage(InstanceHelpers.componentText(
								"Hello from client! Owner " + layer.system.owner + "@" + getAnimationPos(layer) +
								" has finished animation " +
								layer.getCurrentAnimation().getLocation() + " with message: " + msg),
						UUID.randomUUID()
				);
			}
		}
	}
	
	public static class HelloWorldActionInstance
			extends AnimationActionInstance
	{
		public String message = "";
		
		public HelloWorldActionInstance(AnimationAction action)
		{
			super(action);
		}
		
		public String message()
		{
			return message;
		}
		
		public HelloWorldActionInstance withMessage(String message)
		{
			this.message = message;
			return this;
		}
		
		@Override
		public CompoundNBT serializeNBT()
		{
			CompoundNBT tag = super.serializeNBT();
			tag.putString("Msg", message);
			return tag;
		}
		
		@Override
		public void deserializeNBT(CompoundNBT nbt)
		{
			message = nbt.getString("Msg");
			super.deserializeNBT(nbt);
		}
	}
}