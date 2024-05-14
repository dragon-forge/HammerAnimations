package org.zeith.hammeranims.api.animsys;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.util.*;

import static org.zeith.hammeranims.api.HammerAnimationsApi.APPROX_ZERO;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SerializableMask
		implements ICompoundSerializable
{
	@Singular
	protected Set<String> excludes = new HashSet<>();
	
	protected Object2FloatMap<String> boneWeights = null;
	
	public SerializableMask(NBTTagCompound mask)
	{
		deserializeNBT(mask);
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		var nbt = InstanceHelpers.newNBTCompound();
		
		var excludeNBT = InstanceHelpers.newNBTList();
		for(String exclude : excludes) excludeNBT.appendTag(InstanceHelpers.newNBTString(exclude));
		nbt.setTag("Excludes", excludeNBT);
		
		if(boneWeights != null && !boneWeights.isEmpty())
		{
			var weightNBT = InstanceHelpers.newNBTCompound();
			for(Object2FloatMap.Entry<String> e : boneWeights.object2FloatEntrySet())
				weightNBT.setFloat(e.getKey(), e.getFloatValue());
			nbt.setTag("Weights", weightNBT);
		}
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		excludes.clear();
		var excludeNBT = nbt.getTagList("Excludes", Constants.NBT.TAG_STRING);
		for(int i = 0; i < excludeNBT.tagCount(); i++) excludes.add(excludeNBT.getStringTagAt(i));
		
		if(nbt.hasKey("Weights", Constants.NBT.TAG_COMPOUND))
		{
			var weightNBT = nbt.getCompoundTag("Weights");
			boneWeights = new Object2FloatOpenHashMap<>();
			for(String key : weightNBT.getKeySet())
				boneWeights.put(key, weightNBT.getFloat(key));
		}
	}
	
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