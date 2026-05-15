package org.zeith.hammeranims.core.utils;

import com.zeitheron.hammercore.api.io.NBTSerializationHelper;
import com.zeitheron.hammercore.utils.ReflectionUtil;
import lombok.var;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SerializableMethodHandle
		implements ICompoundSerializable
{
	protected Method method;
	protected Object instance;
	protected Object[] args;
	protected boolean resolved;
	
	public SerializableMethodHandle(Method method, Object instance, Object... args)
	{
		this.method = method;
		this.instance = method == null || Modifier.isStatic(method.getModifiers()) ? null : instance;
		this.args = args;
		updateResolution();
	}
	
	public SerializableMethodHandle(NBTTagCompound nbt)
	{
		deserializeNBT(nbt);
	}
	
	public Method getMethod()
	{
		return method;
	}
	
	public Object getInstance()
	{
		return instance;
	}
	
	public Object[] getArgs()
	{
		return args;
	}
	
	public void setMethod(Method method)
	{
		this.method = method;
		updateResolution();
	}
	
	public void setInstance(Object instance)
	{
		this.instance = instance;
		updateResolution();
	}
	
	public void setArgs(Object[] args)
	{
		this.args = args;
		updateResolution();
	}
	
	public static SerializableMethodHandle create(Class<?> owner, String methodName, Object instance, Object... args)
	{
		int al = args.length;
		var res = Arrays.stream(owner.getMethods())
		                .filter(m -> m.getName().equals(methodName) && m.getParameterCount() == al)
		                .collect(Collectors.toList());
		Method resolved = null;
		
		search:
		for(var candidate : res)
		{
			var pars = candidate.getParameterTypes();
			for(int i = 0; i < al; i++)
			{
				// if the argument is non-primitive and is null, or if it's an instance of the requested parameter type
				if((args[i] == null && !pars[i].isPrimitive()) || pars[i].isInstance(args[i]))
					continue; // Argument matched, go on...
				continue search;
			}
			resolved = candidate;
			break;
		}
		
		return new SerializableMethodHandle(resolved, instance, args);
	}
	
	public void updateResolution()
	{
		boolean resolved = method != null
				&& (instance != null || Modifier.isStatic(method.getModifiers()))
				&& args != null
				&& args.length == method.getParameterCount();
		
		if(!resolved)
		{
			this.resolved = false;
			return;
		}
		
		var args = method.getParameterTypes();
		for(int i = 0; i < args.length; i++)
		{
			if(!args[i].isInstance(this.args[i]))
			{
				this.resolved = false;
				return;
			}
		}
		
		this.resolved = true;
	}
	
	public boolean isResolved()
	{
		return resolved;
	}
	
	public Object call()
			throws MethodHandleInvocationException
	{
		try
		{
			return method.invoke(instance, args);
		} catch(Exception e)
		{
			throw new MethodHandleInvocationException(e);
		}
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("Class", method.getDeclaringClass().getCanonicalName());
		nbt.setString("Method", method.getName());
		
		if(!isUniqueMethod(method))
		{
			NBTTagList pars = new NBTTagList();
			for(Class<?> type : method.getParameterTypes())
				pars.appendTag(new NBTTagString(type.getCanonicalName()));
			nbt.setTag("Types", pars);
		}
		
		if(instance != null)
			NBTSerializationHelper.serializeField(instance.getClass(), instance, nbt, "Instance");
		
		nbt.setInteger("Args", args.length);
		for(int i = 0; i < args.length; i++)
		{
			Object arg = args[i];
			NBTSerializationHelper.serializeField(arg.getClass(), arg, nbt, "Arg" + i);
		}
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		method = null;
		instance = null;
		args = new Object[0];
		resolved = false;
		
		var cls = ReflectionUtil.fetchClass(nbt.getString("Class"));
		if(cls == null) return;
		
		String mName = nbt.getString("Method");
		var res = Arrays.stream(cls.getMethods())
		                .filter(m -> m.getName().equals(mName))
		                .collect(Collectors.toList());
		if(res.isEmpty()) return;
		
		Method resolved = null;
		
		methodResolver:
		{
			if(nbt.hasKey("Types", Constants.NBT.TAG_LIST))
			{
				var typesNbt = nbt.getTagList("Types", Constants.NBT.TAG_STRING);
				Class<?>[] args = new Class[typesNbt.tagCount()];
				for(int i = 0; i < args.length; i++)
				{
					var a = ReflectionUtil.fetchClass(typesNbt.getStringTagAt(i));
					if(a == null) break methodResolver;
					args[i] = a;
				}
				
				search:
				for(var t : res)
				{
					var types = t.getParameterTypes();
					if(types.length != args.length) continue;
					for(int i = 0; i < types.length; i++)
						if(!args[i].equals(types[i]))
							continue search;
					resolved = t;
					break methodResolver;
				}
			} else
			{
				if(res.size() > 1) break methodResolver;
				resolved = res.get(0);
			}
		}
		
		if(resolved == null)
		{
			return;
		}
		
		this.method = resolved;
		if(nbt.hasKey("Instance", Constants.NBT.TAG_COMPOUND))
			this.instance = NBTSerializationHelper.deserializeField(cls, nbt, "Instance");
		this.args = new Object[nbt.getInteger("Args")];
		var pars = method.getParameterTypes();
		int c = Math.min(this.args.length, pars.length);
		for(int i = 0; i < c; i++)
			this.args[i] = NBTSerializationHelper.deserializeField(pars[i], nbt, "Arg" + i);
		
		updateResolution();
	}
	
	@Override
	public String toString()
	{
		return "SerializableMethodHandle{" +
				"resolved=" + resolved +
				", method=" + method +
				", instance=" + instance +
				", args=" + Arrays.toString(args) +
				'}';
	}
	
	public static boolean isUniqueMethod(Method method)
	{
		return Arrays.stream(method.getDeclaringClass().getMethods())
		             .map(Method::getName)
		             .filter(method.getName()::equals)
		             .count() == 1L;
	}
	
	public static class MethodHandleInvocationException
			extends ReflectiveOperationException
	{
		public MethodHandleInvocationException(Throwable cause)
		{
			super(cause);
		}
	}
}