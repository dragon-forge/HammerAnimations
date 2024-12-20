package shaded.util;

import java.util.Locale;
import java.util.Objects;

public class ResourceLocation implements Comparable<ResourceLocation>
{
	protected final String namespace;
	protected final String path;
	
	protected ResourceLocation(int ignored, String... resourceName)
	{
		this.namespace = resourceName[0].isBlank() ? "minecraft" : resourceName[0].toLowerCase(Locale.ROOT);
		this.path = resourceName[1].toLowerCase(Locale.ROOT);
		Objects.requireNonNull(this.path);
	}
	
	public ResourceLocation(String resourceName)
	{
		this(0, splitObjectName(resourceName));
	}
	
	public ResourceLocation(String namespaceIn, String pathIn)
	{
		this(0, namespaceIn, pathIn);
	}
	
	public static String[] splitObjectName(String toSplit)
	{
		String[] astring = new String[] {"minecraft", toSplit};
		int i = toSplit.indexOf(58);
		
		if (i >= 0)
		{
			astring[1] = toSplit.substring(i + 1, toSplit.length());
			
			if (i > 1)
			{
				astring[0] = toSplit.substring(0, i);
			}
		}
		
		return astring;
	}
	
	public String getPath()
	{
		return this.path;
	}
	
	public String getNamespace()
	{
		return this.namespace;
	}
	
	public String toString()
	{
		return this.namespace + ':' + this.path;
	}
	
	public boolean equals(Object p_equals_1_)
	{
		if (this == p_equals_1_)
		{
			return true;
		}
		else if (!(p_equals_1_ instanceof ResourceLocation))
		{
			return false;
		}
		else
		{
			ResourceLocation resourcelocation = (ResourceLocation)p_equals_1_;
			return this.namespace.equals(resourcelocation.namespace) && this.path.equals(resourcelocation.path);
		}
	}
	
	public int hashCode()
	{
		return 31 * this.namespace.hashCode() + this.path.hashCode();
	}
	
	public int compareTo(ResourceLocation p_compareTo_1_)
	{
		int i = this.namespace.compareTo(p_compareTo_1_.namespace);
		
		if (i == 0)
		{
			i = this.path.compareTo(p_compareTo_1_.path);
		}
		
		return i;
	}
}