package org.zeith.hammeranims.core.contents.particles.components.expiration;

import com.google.gson.JsonElement;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammeranims.api.particles.components.IParticleComponent;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.core.utils.InstanceHelpers;
import org.zeith.hammeranims.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class ParcomExpireBlocks
		implements IParticleComponent
{
	public List<Block> blocks = new ArrayList<>();
	public List<TagKey<Block>> blockTags = new ArrayList<>();
	private BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
	
	public ParcomExpireBlocks(JsonElement elem)
	{
		if(!elem.isJsonArray()) return;
		for(JsonElement value : elem.getAsJsonArray())
		{
			var s = value.getAsString();
			if(s.startsWith("#"))
			{
				blockTags.add(BlockTags.create(InstanceHelpers.tryParseLocation(s.substring(1))));
				continue;
			}
			var location = InstanceHelpers.tryParseLocation(s);
			Block block = ForgeRegistries.BLOCKS.getValue(location);
			if(block != null) this.blocks.add(block);
		}
	}
	
	public boolean matches(BlockState state)
	{
		return blocks.contains(state.getBlock()) || blockTags.stream().anyMatch(state::is);
	}
	
	public BlockState getBlockState(ParticleEmitter emitter, BedrockParticle particle)
	{
		if(emitter.world == null) return Blocks.AIR.defaultBlockState();
		var position = particle.getGlobalPosition(emitter);
		this.pos.set(position.x(), position.y(), position.z());
		return emitter.world.getBlockState(this.pos);
	}
}