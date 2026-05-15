package org.zeith.hammeranims.api.texture;

@FunctionalInterface
public interface IPixelGetter
{
	int getARGB(int x, int y);
}