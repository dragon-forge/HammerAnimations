package org.zeith.hammeranims.standalone.contexts;

import org.zeith.hammeranims.standalone.mc.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public record ContextToken<T>(Class<T> type, ResourceLocation id, @Nullable Supplier<T> defaultValue)
{
	public ContextToken(Class<T> type, ResourceLocation id)
	{
		this(type, id, null);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		var that = (ContextToken) obj;
		return Objects.equals(this.type, that.type) &&
			   Objects.equals(this.id, that.id);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(type, id);
	}
	
	@Override
	public String toString()
	{
		return "ContextToken[" +
			   "type=" + type + ", " +
			   "id=" + id + ']';
	}
}