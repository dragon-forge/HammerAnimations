package org.zeith.hammeranims.api.animsys;

import it.unimi.dsi.fastutil.objects.*;
import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.util.*;

import static org.zeith.hammeranims.api.HammerAnimationsApi.APPROX_ZERO;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class SerializableMask
		implements ICompoundSerializable
{
	protected Set<String> excludes = new HashSet<>();
	protected Object2FloatMap<String> boneWeights = null;
	
	@Builder
	public SerializableMask(Set<String> excludes, Object2FloatMap<String> boneWeights)
	{
		this.excludes = excludes;
		this.boneWeights = boneWeights;
	}
	
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
		for(int i = 0; i < excludeNBT.tagCount(); i++) excludes.add(excludeNBT.getStringTagAt(i).toLowerCase(Locale.ROOT));
		
		if(nbt.hasKey("Weights", Constants.NBT.TAG_COMPOUND))
		{
			var weightNBT = nbt.getCompoundTag("Weights");
			boneWeights = new Object2FloatOpenHashMap<>();
			for(String key : weightNBT.getKeySet()) boneWeights.put(key.toLowerCase(Locale.ROOT), weightNBT.getFloat(key));
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
		public SerializableMaskBuilder exclude(String bone)
		{
			if(bone == null) throw new NullPointerException("excludes cannot be null");
			if(this.excludes == null) this.excludes = new HashSet<>();
			this.excludes.add(bone.toLowerCase(Locale.ROOT));
			return this;
		}
		
		public SerializableMaskBuilder excludeAll(String... bones)
		{
			if(bones == null) throw new NullPointerException("excludes cannot be null");
			if(this.excludes == null) this.excludes = new HashSet<>();
			for(String bone : bones) this.excludes.add(bone.toLowerCase(Locale.ROOT));
			return this;
		}
		
		public SerializableMaskBuilder excludes(Collection<? extends String> excludes)
		{
			if(excludes == null) throw new NullPointerException("excludes cannot be null");
			if(this.excludes == null) this.excludes = new HashSet<>();
			for(String s : excludes) this.excludes.add(s.toLowerCase(Locale.ROOT));
			return this;
		}
		
		public SerializableMaskBuilder boneWeight(String bone, float weight)
		{
			if(Math.abs(weight) < APPROX_ZERO) return exclude(bone);
			boneWeights().put(bone.toLowerCase(Locale.ROOT), weight);
			return this;
		}
		
		public SerializableMaskBuilder boneWeights(Object2FloatMap<String> weightMap)
		{
			if(weightMap == null || weightMap.isEmpty()) return this;
			Object2FloatMap<String> bw = boneWeights();
			for(Object2FloatMap.Entry<String> e : weightMap.object2FloatEntrySet())
			{
				String bone = e.getKey().toLowerCase(Locale.ROOT);
				float weight = e.getFloatValue();
				if(Math.abs(weight) < APPROX_ZERO)
				{
					exclude(bone);
					continue;
				}
				bw.put(bone, weight);
			}
			return this;
		}
		
		private Object2FloatMap<String> boneWeights()
		{
			if(this.boneWeights != null) return this.boneWeights;
			return this.boneWeights = new Object2FloatOpenHashMap<>();
		}
		
		private SerializableMaskBuilder excludes(Set<String> excludes)
		{
			return this;
		}
	}
}