package org.zeith.hammeranims.api.animsys;

import lombok.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.util.*;

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
	
	public SerializableMask(CompoundNBT mask)
	{
		deserializeNBT(mask);
	}
	
	@Override
	public CompoundNBT serializeNBT()
	{
		var nbt = InstanceHelpers.newNBTCompound();
		
		var excludeNBT = InstanceHelpers.newNBTList();
		for(String exclude : excludes) excludeNBT.add(InstanceHelpers.newNBTString(exclude));
		nbt.put("Excludes", excludeNBT);
		
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt)
	{
		excludes.clear();
		var excludeNBT = nbt.getList("Excludes", Constants.NBT.TAG_STRING);
		for(int i = 0; i < excludeNBT.size(); i++) excludes.add(excludeNBT.getString(i));
	}
	
	public static class SerializableMaskBuilder
	{
		public SerializableMaskBuilder excludeAll(String... bones)
		{
			return excludes(Arrays.asList(bones));
		}
	}
}