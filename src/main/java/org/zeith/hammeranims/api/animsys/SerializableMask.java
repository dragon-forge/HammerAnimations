package org.zeith.hammeranims.api.animsys;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import lombok.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.util.*;

import static org.zeith.hammeranims.api.HammerAnimationsApi.APPROX_ZERO;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SerializableMask
		implements ICompoundSerializable
{
	@Singular
	protected Set<String> excludes = new HashSet<>();
	
	protected Object2FloatMap<String> boneWeights = null;
	
	public SerializableMask(CompoundTag mask)
	{
		deserializeNBT(mask);
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		var nbt = InstanceHelpers.newNBTCompound();
		
		var excludeNBT = InstanceHelpers.newNBTList();
		for(String exclude : excludes) excludeNBT.add(InstanceHelpers.newNBTString(exclude));
		nbt.put("Excludes", excludeNBT);
		
		if(boneWeights != null && !boneWeights.isEmpty())
		{
			var weightNBT = InstanceHelpers.newNBTCompound();
			for(Object2FloatMap.Entry<String> e : boneWeights.object2FloatEntrySet())
				weightNBT.putFloat(e.getKey(), e.getFloatValue());
			nbt.put("Weights", weightNBT);
		}
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		excludes.clear();
		var excludeNBT = nbt.getList("Excludes", Tag.TAG_STRING);
		for(int i = 0; i < excludeNBT.size(); i++) excludes.add(excludeNBT.getString(i));
		
		if(nbt.contains("Weights", Tag.TAG_COMPOUND))
		{
			var weightNBT = nbt.getCompound("Weights");
			boneWeights = new Object2FloatOpenHashMap<>();
			for(String key : weightNBT.getAllKeys())
				boneWeights.put(key, weightNBT.getFloat(key));
		}
	}
	
	public WeightFunction getBoneWeight()
	{
		return boneWeights == null ? WeightFunction.ONE : b -> boneWeights.getOrDefault(b, 1F);
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