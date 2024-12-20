package org.zeith.hammeranims.api.animsys;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import lombok.*;

import java.util.*;

import static org.zeith.hammeranims.api.HammerAnimationsApi.APPROX_ZERO;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SerializableMask
{
	@Singular
	protected Set<String> excludes = new HashSet<>();
	
	protected Object2FloatMap<String> boneWeights = null;
	
	public WeightFunction getBoneWeight()
	{
		return boneWeights != null ? WeightFunction.ONE : b -> boneWeights.getOrDefault(b, 1F);
	}
	
	public interface WeightFunction
	{
		WeightFunction ONE = t -> 1F;
		
		float get(String bone);
	}
	
	public static class SerializableMaskBuilder
	{
		public SerializableMaskBuilder excludeAll(String... bones)
		{
			return excludes(Arrays.asList(bones));
		}
		
		public SerializableMaskBuilder boneWeight(String bone, float weight)
		{
			if(Math.abs(weight) < APPROX_ZERO) return exclude(bone);
			boneWeights().put(bone, weight);
			return this;
		}
		
		private Object2FloatMap<String> boneWeights()
		{
			if(this.boneWeights != null) return this.boneWeights;
			return this.boneWeights = new Object2FloatOpenHashMap<>();
		}
	}
}