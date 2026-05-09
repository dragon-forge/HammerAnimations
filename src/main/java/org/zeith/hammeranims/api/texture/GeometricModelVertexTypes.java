package org.zeith.hammeranims.api.texture;

import com.google.common.collect.ImmutableMap;
import lombok.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;

import java.util.Map;
import java.util.function.Function;

@ToString
@AllArgsConstructor
public class GeometricModelVertexTypes
		implements Function<String, @Nullable VertexType>
{
	public final Map<String, VertexType> bones;
	
	public static GeometricModelVertexTypes forModelAndTexture(IGeometricModel model, ResourceLocation texture)
	{
		ITextureAccess tex = HammerAnimations.PROXY.loadTextureAccess(texture);
		ImmutableMap.Builder<String, VertexType> builder = ImmutableMap.builder();
		for(IRenderableBone bone : model.getBones())
			builder.put(bone.getName(), ITextureAccess.determineBoneType(bone, tex));
		return new GeometricModelVertexTypes(builder.build());
	}
	
	@Override
	public @Nullable VertexType apply(String s)
	{
		return bones.get(s);
	}
}