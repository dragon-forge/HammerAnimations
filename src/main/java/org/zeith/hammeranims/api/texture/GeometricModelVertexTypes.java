package org.zeith.hammeranims.api.texture;

import com.google.common.collect.ImmutableMap;
import lombok.*;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;

import java.util.*;
import java.util.function.Function;

@ToString
@AllArgsConstructor
public class GeometricModelVertexTypes
		implements Function<String, @Nullable VertexType>
{
	private static final Optional<VertexType> DEF = Optional.of(VertexType.DIRECT);
	public final Map<String, Optional<VertexType>> bones;
	
	public static GeometricModelVertexTypes forModelAndTexture(IGeometricModel model, ResourceLocation texture)
	{
		ITextureAccess tex = HammerAnimations.PROXY.getTextureAccess(texture);
		ImmutableMap.Builder<String, Optional<VertexType>> builder = ImmutableMap.builder();
		for(IRenderableBone bone : model.getBones())
			builder.put(bone.getName(), ITextureAccess.determineBoneType(bone, tex));
		return new GeometricModelVertexTypes(builder.build());
	}
	
	public boolean isInvisible(String bone)
	{
		return !bones.getOrDefault(bone, DEF).isPresent();
	}
	
	@Override
	public @Nullable VertexType apply(String s)
	{
		return bones.getOrDefault(s, Optional.empty()).orElse(null);
	}
}