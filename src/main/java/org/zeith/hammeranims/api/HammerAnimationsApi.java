package org.zeith.hammeranims.api;

import org.zeith.hammeranims.api.utils.IResourceProvider;

import java.util.*;

public class HammerAnimationsApi
{
	public static final float APPROX_ZERO = 1.0E-30F;
	
	private static final List<IResourceProvider> AUXILIARY_RESOURCE_PROVIDERS = new ArrayList<>();
	
	public static boolean LOG_RELOADS = !Boolean.parseBoolean(System.getProperty("hammeranims.silence"));
	
	public static void addAuxiliaryResourceProvider(IResourceProvider provider)
	{
		AUXILIARY_RESOURCE_PROVIDERS.add(provider);
	}
	
	public static List<IResourceProvider> getAuxiliaryResourceProviders()
	{
		return Collections.unmodifiableList(AUXILIARY_RESOURCE_PROVIDERS);
	}
}