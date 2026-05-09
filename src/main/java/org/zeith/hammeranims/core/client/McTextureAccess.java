package org.zeith.hammeranims.core.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import org.zeith.hammeranims.api.geometry.model.IRenderableBone;
import org.zeith.hammeranims.api.texture.ITextureAccess;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;

import java.io.*;

public class McTextureAccess
{
	public static ITextureAccess getPixels(ResourceManager pManager, ResourceLocation pLocation)
			throws IOException
	{
		try(
				InputStream input = pManager.open(pLocation);
				NativeImage image = NativeImage.read(input);
		)
		{
			int[] pixels = makePixelArray(image);
			int w = image.getWidth();
			int h = image.getHeight();
			return ITextureAccess.from(w, h, (x, y) -> pixels[x + y * w]);
		}
	}
	
	public static int[] makePixelArray(NativeImage img)
	{
		if(img.format() != NativeImage.Format.RGBA)
		{
			throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
		} else
		{
			int[] aint = new int[img.getWidth() * img.getHeight()];
			
			for(int i = 0; i < img.getHeight(); ++i)
			{
				for(int j = 0; j < img.getWidth(); ++j)
				{
					int k = img.getPixelRGBA(j, i);
					aint[j + i * img.getWidth()] = FastColor.ARGB32.color(FastColor.ABGR32.alpha(k), FastColor.ABGR32.red(k), FastColor.ABGR32.green(k), FastColor.ABGR32.blue(k));
				}
			}
			
			return aint;
		}
	}
	
}
