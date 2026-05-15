package org.zeith.hammeranims.core.contents.particles.components.expiration;

import com.google.gson.JsonElement;
import dev.zeith.lzvm.LzVariableStore;
import lombok.var;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.components.inst.IParticleCompInstance;
import org.zeith.hammeranims.api.particles.emitter.*;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.util.*;
import java.util.stream.Collectors;

public abstract class ParcomExpireBlocks
		implements IParticleComponent
{
	public List<Block> blocks = new ArrayList<>();
	public List<String> blockTags = new ArrayList<>();
	
	public ParcomExpireBlocks(JsonElement element)
	{
		if(!element.isJsonArray()) return;
		for(JsonElement value : element.getAsJsonArray())
		{
			var s = value.getAsString();
			if(s.startsWith("#"))
			{
				blockTags.add(s.substring(1));
				continue;
			}
			var location = InstanceHelpers.tryParseLocation(s);
			Block block = ForgeRegistries.BLOCKS.getValue(location);
			if(block != null) this.blocks.add(block);
		}
	}
	
	@Override
	public abstract ParcomExpireBlocksInstance createInstance(LzVariableStore vars);
	
	public static abstract class ParcomExpireBlocksInstance
			implements IParticleCompInstance
	{
		private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		public Set<Block> blocks;
		public Set<Block> blockTags;
		
		public ParcomExpireBlocksInstance(ParcomExpireBlocks owner)
		{
			this.blocks = new HashSet<>(owner.blocks);
			this.blockTags = owner.blockTags
					.stream()
					.map(OreDictionary::getOres)
					.flatMap(NonNullList::stream)
					.filter(s -> !s.isEmpty() && s.getItem() instanceof ItemBlock)
					.map(s -> (ItemBlock) s.getItem())
					.map(ItemBlock::getBlock)
					.collect(Collectors.toSet());
		}
		
		public boolean matches(IBlockState state)
		{
			return blocks.contains(state.getBlock()) || blockTags.contains(state.getBlock());
		}
		
		public IBlockState getBlockState(ParticleEmitter emitter, BedrockParticle particle)
		{
			if(emitter.world == null) return Blocks.AIR.getDefaultState();
			var position = particle.getGlobalPosition(emitter);
			this.pos.setPos(position.x(), position.y(), position.z());
			return emitter.world.getBlockState(this.pos);
		}
		
	}
}