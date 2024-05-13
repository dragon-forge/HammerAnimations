package org.zeith.hammeranims.core.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("HammerAnimationsCore")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1)
public class HACoremod
		implements IFMLLoadingPlugin
{
	protected final String[] transformers = new String[] {
			HAClassTransformer.class.getCanonicalName()
	};
	
	@Override
	public String[] getASMTransformerClass()
	{
		return transformers;
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Nullable
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data)
	{
	
	}
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}