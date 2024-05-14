package org.zeith.hammeranims.api.animsys;

import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.util.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SerializableMask
		implements ICompoundSerializable
{
	@Singular
	protected Set<String> excludes = new HashSet<>();
	
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
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		excludes.clear();
		var excludeNBT = nbt.getTagList("Excludes", Constants.NBT.TAG_STRING);
		for(int i = 0; i < excludeNBT.tagCount(); i++) excludes.add(excludeNBT.getStringTagAt(i));
	}
	
	public static class SerializableMaskBuilder
	{
		public SerializableMaskBuilder excludeAll(String... bones)
		{
			return excludes(Arrays.asList(bones));
		}
	}
}