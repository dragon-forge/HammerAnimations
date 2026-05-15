package org.zeith.hammeranims.core.client;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.api.texture.ITextureAccess;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class McTextureAccess
{
	public static ITextureAccess getPixels(IResourceManager pManager, ResourceLocation pLocation)
			throws IOException
	{
		try(IResource input = pManager.getResource(pLocation))
		{
			BufferedImage image = TextureUtil.readBufferedImage(input.getInputStream());
			int[] pixels = makePixelArray(image);
			int w = image.getWidth();
			int h = image.getHeight();
			return ITextureAccess.from(w, h, (x, y) -> pixels[x + y * w]);
		}
	}
	
	public static int[] makePixelArray(BufferedImage img)
	{
		int[] aint = new int[img.getWidth() * img.getHeight()];
		for(int y = 0; y < img.getHeight(); ++y)
			for(int x = 0; x < img.getWidth(); ++x)
				aint[x + y * img.getWidth()] = img.getRGB(x, y);
		return aint;
	}
}
