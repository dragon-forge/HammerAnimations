package org.zeith.hammeranims.core.impl.api.geometry.decoder;

import shaded.joml.Vector2i;
import shaded.joml.Vector3f;
import org.zeith.hammeranims.core.client.model.CubeUVs;
import org.zeith.hammeranims.core.jomljson.Vector2iGsonAdapter;
import org.zeith.hammeranims.core.utils.EnumFacing;
import shaded.json.*;

import java.util.Collections;
import java.util.EnumMap;

public interface UVDefinition
{
	CubeUVs bake(Vector3f size);
	
	static UVDefinition parse(Object jsonIn)
	{
		if(jsonIn instanceof JSONObject j)
			return PerFace.parse(j);
		else if(jsonIn instanceof JSONArray a)
			return Simple.parse(a);
		else throw new JSONException("Unknown UV definition.");
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
		
		public static Simple parse(JSONArray json)
		{
			return new Simple(Vector2iGsonAdapter.parse(json));
		}
	}
	
	final class PerFace
			implements UVDefinition
	{
		public static final EnumFacing[] DIRECTIONS = EnumFacing.values();
		
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
		
		public static PerFace parse(JSONObject json)
		{
			EnumMap<EnumFacing, FaceUVDefinition> map = new EnumMap<>(EnumFacing.class);
			
			for(EnumFacing direction : DIRECTIONS)
			{
				if(!json.has(direction.getName())) continue;
				
				FaceUVDefinition face = FaceUVDefinition.parse(json.getJSONObject(direction.getName()));
				if(face == null) continue;
				
				map.put(direction, face);
			}
			return new PerFace(map);
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
		
		public static FaceUVDefinition parse(JSONObject json)
		{
			Vector2i uv = Vector2iGsonAdapter.parse(json.getJSONArray("uv"));
			Vector2i size = Vector2iGsonAdapter.parse(json.getJSONArray("uv_size"));
			
			return new FaceUVDefinition(uv, size);
		}
	}
}