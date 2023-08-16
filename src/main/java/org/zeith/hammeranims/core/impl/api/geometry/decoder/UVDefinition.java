package org.zeith.hammeranims.core.impl.api.geometry.decoder;

import com.google.gson.*;
import org.zeith.hammeranims.core.client.model.CubeUVs;
import org.zeith.hammeranims.core.utils.EnumFacing;
import org.zeith.hammeranims.joml.*;

import java.lang.reflect.Type;
import java.util.*;

public interface UVDefinition
{
	CubeUVs bake(Vector3f size);
	
	class Deserializer
			implements JsonDeserializer<UVDefinition>
	{
		private static final Simple.Deserializer simpleDecoder = new Simple.Deserializer();
		private static final PerFace.Deserializer perFaceDecoder = new PerFace.Deserializer();
		
		@Override
		public UVDefinition deserialize(JsonElement jsonIn, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException
		{
			if(jsonIn.isJsonArray())
			{
				return simpleDecoder.deserialize(jsonIn, typeOfT, context);
			} else
			{
				return perFaceDecoder.deserialize(jsonIn, typeOfT, context);
			}
		}
	}
	
	/**
	 * uv - The starting point in the texture foo.png (x -> horizontal, y -> vertical) for that cube.
	 */
	final class Simple
			implements UVDefinition
	{
		private final Vector2i uv;
		
		private Simple(Vector2i uv)
		{
			this.uv = uv;
		}
		
		@Override
		public CubeUVs bake(Vector3f size)
		{
			return new CubeUVs.BoxUVResolver(uv, size);
		}
		
		private static class Deserializer
				implements JsonDeserializer<Simple>
		{
			@Override
			public Simple deserialize(JsonElement jsonIn, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException
			{
				return new Simple(context.deserialize(jsonIn, Vector2i.class));
			}
		}
	}
	
	final class PerFace
			implements UVDefinition
	{
		private final EnumMap<EnumFacing, FaceUVDefinition> mappings;
		
		private PerFace(EnumMap<EnumFacing, FaceUVDefinition> mappings)
		{
			this.mappings = mappings;
		}
		
		@Override
		public CubeUVs bake(Vector3f size)
		{
			return new CubeUVs.FacedUVResolver(Collections.unmodifiableMap(mappings));
		}
		
		private static class Deserializer
				implements JsonDeserializer<PerFace>
		{
			public static final EnumFacing[] DIRECTIONS = EnumFacing.values();
			
			@Override
			public PerFace deserialize(JsonElement jsonIn, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException
			{
				JsonObject json = jsonIn.getAsJsonObject();
				EnumMap<EnumFacing, FaceUVDefinition> map = new EnumMap<>(EnumFacing.class);
				
				for(EnumFacing direction : DIRECTIONS)
				{
					FaceUVDefinition face = context.deserialize(json.get(direction.getName()), FaceUVDefinition.class);
					
					if(face == null)
					{
						face = new FaceUVDefinition(new Vector2i(), new Vector2i());
					}
					
					map.put(direction, face);
					
				}
				return new PerFace(map);
			}
		}
	}
	
	class FaceUVDefinition
	{
		private final Vector2i uv;
		private final Vector2i size;
		
		public FaceUVDefinition(Vector2i uv, Vector2i size)
		{
			this.uv = uv;
			this.size = size;
		}
		
		public Vector2i uv()
		{
			return uv;
		}
		
		public Vector2i size()
		{
			return size;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(!(o instanceof FaceUVDefinition)) return false;
			
			FaceUVDefinition that = (FaceUVDefinition) o;
			
			if(!uv.equals(that.uv)) return false;
			return size.equals(that.size);
		}
		
		@Override
		public int hashCode()
		{
			int result = uv.hashCode();
			result = 31 * result + size.hashCode();
			return result;
		}
		
		public static class Deserializer
				implements JsonDeserializer<FaceUVDefinition>
		{
			@Override
			public FaceUVDefinition deserialize(JsonElement jsonIn, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException
			{
				JsonObject json = jsonIn.getAsJsonObject();
				
				Vector2i uv = context.deserialize(json.get("uv"), Vector2i.class);
				Vector2i size = context.deserialize(json.get("uv_size"), Vector2i.class);
				
				return new FaceUVDefinition(uv, size);
			}
		}
	}
}