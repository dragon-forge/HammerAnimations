package org.zeith.hammeranims.api.utils;

public interface IHammerReloadable
{
	/**
	 * Reloads this container using the provided resource provider.
	 * This is an internal function and should never be called externally.
	 * Implementing it is fine.
	 *
	 * @param provider
	 * 		The resource provider used to reload the container.
	 */
	void reload(IResourceProvider provider);
}