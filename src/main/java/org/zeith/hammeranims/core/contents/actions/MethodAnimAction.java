package org.zeith.hammeranims.core.contents.actions;

import com.zeitheron.hammercore.utils.base.Cast;
import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animsys.*;
import org.zeith.hammeranims.api.animsys.actions.*;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.annotation.ExposedToAnimAction;
import org.zeith.hammeranims.core.init.ContainersHA;
import org.zeith.hammeranims.core.utils.SerializableMethodHandle;

import java.lang.reflect.Modifier;

public class MethodAnimAction
		extends AnimationAction
{
	@Nullable
	public static AnimationActionInstance create(@NotNull Class<?> owner, @NotNull String methodName, @Nullable Object instance, Object... args)
	{
		return createWith(SerializableMethodHandle.create(owner, methodName, instance, args));
	}
	
	public static AnimationActionInstance createWith(SerializableMethodHandle handle)
	{
		if(handle == null || !handle.isResolved()) return null; // Unresolved handles, don't care
		if(!handle.getMethod().isAnnotationPresent(ExposedToAnimAction.class)) return null; // If we don't have @ExposedToAnimAction, don't even bother.
		Object[] args = handle.getArgs();
		for(int i = 0; i < args.length; i++)
		{
			if(isSwappableObject(args[i]))
				args[i] = false;
		}
		if(isSwappableObject(handle.getInstance()))
			handle.setInstance(false);
		return new MathodAnimActionInstance(ContainersHA.METHOD_CALL).withHandle(handle);
	}
	
	@Override
	protected @NotNull AnimationActionInstance createInstance()
	{
		return new MathodAnimActionInstance(this);
	}
	
	public static boolean isSwappableObject(Object a)
	{
		return a instanceof AnimationLayer || a instanceof AnimationSystem || a instanceof IAnimatedObject || a instanceof World;
	}
	
	public static Object findSwappableObject(Object current, Class<?> type, AnimationLayer layer)
	{
		if(type.isInstance(layer)) return layer;
		
		val sys = layer.system;
		if(type.isInstance(sys)) return sys;
		
		val owner = sys.owner;
		if(type.isInstance(owner)) return owner;
		
		val world = owner.getAnimatedObjectWorld();
		if(type.isInstance(world)) return world;
		
		return current;
	}
	
	@Override
	public void execute(AnimationActionInstance instance, AnimationLayer layer)
	{
		val handle = Cast
				.optionally(instance, MathodAnimActionInstance.class)
				.map(MathodAnimActionInstance::getHandle)
				.orElse(null);
		
		if(handle == null)
			return;
		
		try
		{
			// Populate dynamic arguments relating to animation system
			val args = handle.getArgs().clone();
			val argTypes = handle.getMethod().getParameterTypes();
			Object inst = handle.getInstance();
			
			// Maybe the IAnimatedObject is the instance which contains the method
			if(!Modifier.isStatic(handle.getMethod().getModifiers()))
				inst = findSwappableObject(inst, handle.getMethod().getDeclaringClass(), layer);
			
			for(int i = 0, len = argTypes.length; i < len; i++)
				args[i] = findSwappableObject(args[i], argTypes[i], layer);
			
			val updatedHandle = new SerializableMethodHandle(
					handle.getMethod(),
					inst,
					args
			);
			
			// Call only exposed static functions to avoid any RCE.
			if(!updatedHandle.isResolved() || !updatedHandle.getMethod().isAnnotationPresent(ExposedToAnimAction.class))
				return;
			
			updatedHandle.call();
		} catch(SerializableMethodHandle.MethodHandleInvocationException e)
		{
			HammerAnimations.LOG.error("Failed to invoke method {}", handle.getMethod(), e);
		}
	}
	
	public static class MathodAnimActionInstance
			extends AnimationActionInstance
	{
		@Getter
		public SerializableMethodHandle handle;
		
		public MathodAnimActionInstance(AnimationAction action)
		{
			super(action);
		}
		
		public MathodAnimActionInstance withHandle(SerializableMethodHandle handle)
		{
			this.handle = handle;
			return this;
		}
		
		@Override
		public NBTTagCompound serializeNBT()
		{
			var tag = super.serializeNBT();
			tag.setTag("Call", handle.serializeNBT());
			return tag;
		}
		
		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			super.deserializeNBT(nbt);
			handle = new SerializableMethodHandle(nbt.getCompoundTag("Call"));
		}
	}
}
