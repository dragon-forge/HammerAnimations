package org.zeith.hammeranims.core.impl.api.geometry.decoder;

import lombok.val;
import org.joml.Vector3f;
import org.json.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.core.client.render.vertex.VertexType;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.GeometryConstrainsImpl;
import org.zeith.hammeranims.core.jomljson.GeometryConstrainsImplAdapter;
import org.zeith.hammeranims.core.utils.GsonHelper;
import org.zeith.hammeranims.standalone.mc.ResourceLocation;

import java.util.*;

public class GsonGeometryDecoder
{
	private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[] {
			"1.12.0"
	};
	
	public static GeometryConstrainsImpl readGeometryConstraints(String text)
	{
		JSONObject json = GsonHelper.parse(text, true);
		return GeometryConstrainsImplAdapter.parse(json);
	}
	
	public static GeometryDataImpl readGeometryFile(IGeometryContainer container, ResourceLocation location, String text)
	{
		if(text != null && !text.isEmpty())
		{
			try
			{
				JSONObject json = GsonHelper.parse(text, true);
				return readGeometryFile(container, location, json);
			} catch(Throwable e)
			{
				throw new RuntimeException("Geometry file can't be read.", e);
			}
		} else
		{
			throw new RuntimeException("Geometry file not found: " + location + " got " + (text == null ? "null" : "empty") + " string...");
		}
	}
	
	private static GeometryDataImpl readGeometryFile(IGeometryContainer container, ResourceLocation fileLocation, JSONObject object)
	{
		for(var key : object.keySet())
			if(key.equals("format_version"))
			{
				String formatVersion = GsonHelper.convertToString(object.get(key), key);
				checkFormatVersion(fileLocation, formatVersion);
			} else if(key.equals("minecraft:geometry"))
			{
				return parseGeometry(container, fileLocation, object.getJSONArray(key));
			}
		
		return null;
	}
	
	private static GeometryDataImpl parseGeometry(IGeometryContainer container, ResourceLocation fileLocation, JSONArray subModelArr)
	{
		JSONObject subModel = GsonHelper.convertToJsonObject(subModelArr.get(0), "member of 'minecraft:geometry'");
		JSONArray bones = GsonHelper.getAsJsonArray(subModel, "bones");
		
		JSONObject description = GsonHelper.getAsJsonObject(subModel, "description");
		
		ModelMaterialInfo material = new ModelMaterialInfo(description);
		
		HashMap<String, ModelPartInfo> parts = new HashMap<>();
		for(var bone : bones)
		{
			ModelPartInfo part = parseBone(GsonHelper.convertToJsonObject(bone, "member of 'bones'"));
			parts.put(part.getName(), part);
		}
		
		List<ModelPartInfo> rootChildren = new ArrayList<>();
		for(ModelPartInfo value : parts.values())
		{
			if(!value.getParentName().equals("root"))
			{
				ModelPartInfo parent = parts.get(value.getParentName());
				if(parent != null)
				{
					parent.addChild(value);
				} else
				{
					throw new JSONException("Can't find parent '"
											+ value.getParentName() + "' for bone '"
											+ value.getName() + "'"
					);
				}
			} else
			{
				rootChildren.add(value);
			}
		}
		
		return makeDefinition(container, material, rootChildren);
	}
	
	private static GeometryDataImpl makeDefinition(IGeometryContainer container, ModelMaterialInfo material, List<ModelPartInfo> roots)
	{
		ModelMeshInfo mesh = new ModelMeshInfo();
		ModelPartInfo root = mesh.getRoot();
		root.addChildren(roots);
		return GeometryDataImpl.create(container, mesh, material);
	}
	
	private static ModelPartInfo parseBone(JSONObject bone)
	{
		Vector3f pivot = GsonHelper.getAsVec3f(bone, "pivot");
		Vector3f rotation = GsonHelper.getAsVec3f(bone, "rotation", new Vector3f(0, 0, 0));
		boolean mirror = GsonHelper.getAsBoolean(bone, "mirror", false);
		boolean neverRender = GsonHelper.getAsBoolean(bone, "neverRender", false);
		String name = GsonHelper.getAsString(bone, "name");
		String parentName = GsonHelper.getAsString(bone, "parent", "root");
		VertexType boneVertexType = VertexType.byId(GsonHelper.getAsString(bone, "render_type", ""));
		
		List<ModelPartInfo> children = new ArrayList<>();
		
		List<ModelCubeInfo> cubes = new ArrayList<>();
		if(bone.has("cubes"))
		{
			int i = 0;
			for(var cubeJson : GsonHelper.getAsJsonArray(bone, "cubes"))
			{
				var cubeObject = GsonHelper.convertToJsonObject(cubeJson, "member of 'cubes'");
				Vector3f origin = GsonHelper.getAsVec3f(cubeObject, "origin");
				Vector3f size = GsonHelper.getAsVec3f(cubeObject, "size");
				UVDefinition uv = UVDefinition.parse(cubeObject.get("uv"));
				boolean cubeMirror = GsonHelper.getAsBoolean(cubeObject, "mirror", mirror);
				float inflate = GsonHelper.getAsFloat(cubeObject, "inflate", 0F);
				VertexType vertexType = VertexType.byId(GsonHelper.getAsString(cubeObject, "render_type", null), boneVertexType);
				
				ModelCubeInfo cube = new ModelCubeInfo(origin, size, uv, inflate, cubeMirror, vertexType);
				
				if(cubeObject.has("rotation"))
				{
					Vector3f innerRotation = GsonHelper.getAsVec3f(cubeObject, "rotation", new Vector3f(0, 0, 0));
					Vector3f innerPivot = GsonHelper.getAsVec3f(cubeObject, "pivot", new Vector3f(0, 0, 0));
					
					children.add(new ModelPartInfo(Collections.singletonList(cube),
							List.of(),
							innerPivot, innerRotation,
							false,
							name + "_generated_" + (i++), name
					));
				} else
					cubes.add(cube);
			}
		}
		
		List<ModelLocatorInfo> locators = new ArrayList<>();
		if(bone.has("locators"))
		{
			int i = 0;
			val locs = GsonHelper.getAsJsonObject(bone, "locators");
			for(val key : locs.keySet())
			{
				val theLoc = locs.get(key);
				if(theLoc instanceof JSONObject obj)
				{
					Vector3f offset = GsonHelper.getAsVec3f(obj, "offset");
					Vector3f innerRotation = GsonHelper.getAsVec3f(obj, "rotation", new Vector3f(0, 0, 0));
					locators.add(new ModelLocatorInfo(offset, innerRotation, key));
				} else
				{
					Vector3f origin = GsonHelper.toVec3f(GsonHelper.convertToJsonArray(theLoc, key), key);
					locators.add(new ModelLocatorInfo(origin, new Vector3f(), key));
				}
			}
		}
		
		ModelPartInfo part = new ModelPartInfo(cubes, locators, pivot, rotation, neverRender, name, parentName);
		part.addChildren(children);
		return part;
	}
	
	private static void checkFormatVersion(ResourceLocation fileLocation, String version)
	{
		if(!contains(ACCEPTABLE_FORMAT_VERSIONS, version))
			HammerAnimations.LOG.warn("[{}]: Potentially unsupported version of geometry {}. Supported versions: {}",
					fileLocation, version, Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS)
			);
	}
	
	public static <T> boolean contains(T[] array, T object)
	{
		for(T t : array)
			if(t.equals(object))
				return true;
		return false;
	}
}