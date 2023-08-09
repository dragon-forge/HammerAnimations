package org.zeith.hammeranims.core.contents.actions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animsys.actions.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammerlib.util.java.Cast;

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
		Level world = getAnimationWorld(layer);
		
		String msg = Cast.optionally(instance, HelloWorldActionInstance.class)
				.map(HelloWorldActionInstance::message)
				.orElse("");
		
		if(world instanceof ServerLevel sl)
			sl.getServer().getPlayerList().broadcastSystemMessage(InstanceHelpers.componentText(
					"Hello from server! Owner " + layer.system.owner + "@" + getAnimationPos(layer) +
							" has finished animation " +
							layer.getCurrentAnimation().getLocation() + " with message: " + msg), false);
		else
		{
			for(Player player : world.players())
			{
				player.sendSystemMessage(InstanceHelpers.componentText(
						"Hello from client! Owner " + layer.system.owner + "@" + getAnimationPos(layer) +
								" has finished animation " +
								layer.getCurrentAnimation().getLocation() + " with message: " + msg)
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
		public CompoundTag serializeNBT()
		{
			CompoundTag tag = super.serializeNBT();
			tag.putString("Msg", message);
			return tag;
		}
		
		@Override
		public void deserializeNBT(CompoundTag nbt)
		{
			message = nbt.getString("Msg");
			super.deserializeNBT(nbt);
		}
	}
}