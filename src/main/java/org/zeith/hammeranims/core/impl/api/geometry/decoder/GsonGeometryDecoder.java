package org.zeith.hammeranims.core.impl.api.geometry.decoder;

import com.google.gson.*;
import com.zeitheron.hammercore.utils.java.tuples.*;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.utils.EmbeddedLocation;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryDataImpl;
import org.zeith.hammeranims.core.impl.api.geometry.constrains.*;
import org.zeith.hammeranims.core.jomljson.*;
import org.zeith.hammeranims.core.utils.GsonHelper;
import org.zeith.hammeranims.joml.*;

import java.util.*;

public class GsonGeometryDecoder
{
	private static final String[] ACCEPTABLE_FORMAT_VERSIONS = new String[] {
			"1.12.0"
	};
	
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(Vector2i.class, new Vector2iGsonAdapter())
			.registerTypeAdapter(UVDefinition.FaceUVDefinition.class, new UVDefinition.FaceUVDefinition.Deserializer())
			.registerTypeAdapter(UVDefinition.class, new UVDefinition.Deserializer())
			.registerTypeAdapter(Vector3f.class, new Vector3fGsonAdapter())
			.registerTypeAdapter(Vector3d.class, new Vector3dGsonAdapter())
			.registerTypeAdapter(BoneConstraintsImpl.class, new BoneConstrainsImplAdapter())
			.registerTypeAdapter(GeometryConstrainsImpl.class, new GeometryConstrainsImplAdapter())
			.create();
	
	public static GeometryConstrainsImpl readGeometryConstraints(String text)
	{
		JsonObject json = GsonHelper.parse(text, true);
		return GSON.fromJson(json, GeometryConstrainsImpl.class);
	}
	
	public static List<Tuple2<EmbeddedLocation, GeometryDataImpl>> readGeometryFile(IGeometryContainer container, ResourceLocation location, String text)
	{
		if(text != null && !text.isEmpty())
		{
			try
			{
				JsonObject json = GsonHelper.parse(text, true);
				return readGeometryFile(container, location, json);
			} catch(Throwable e)
			{
				throw new RuntimeException("Geometry file can't be read.", e);
			}
		} else
		{
			throw new RuntimeException("Geometry file not found: " + location);
		}
	}
	
	private static List<Tuple2<EmbeddedLocation, GeometryDataImpl>> readGeometryFile(IGeometryContainer container, ResourceLocation fileLocation, JsonObject object)
	{
		List<Tuple2<EmbeddedLocation, GeometryDataImpl>> definitions = new ArrayList<>();
		for(Map.Entry<String, JsonElement> entry : object.entrySet())
			if(entry.getKey().equals("format_version"))
			{
				String formatVersion = GsonHelper.convertToString(entry.getValue(), entry.getKey());
				checkFormatVersion(formatVersion);
			} else if(entry.getKey().equals("minecraft:geometry"))
			{
				Tuple2<EmbeddedLocation, GeometryDataImpl> identifierAndModel = parseGeometry(container, fileLocation, GsonHelper.convertToJsonArray(entry.getValue(), entry.getKey()));
				definitions.add(identifierAndModel);
			}
		
		return definitions;
	}
	
	private static Tuple2<EmbeddedLocation, GeometryDataImpl> parseGeometry(IGeometryContainer container, ResourceLocation fileLocation, JsonArray subModelArr)
	{
		JsonObject subModel = GsonHelper.convertToJsonObject(subModelArr.get(0), "member of 'minecraft:geometry'");
		JsonArray bones = GsonHelper.getAsJsonArray(subModel, "bones");
		
		JsonObject description = GsonHelper.getAsJsonObject(subModel, "description");
		String identifier = GsonHelper.getAsString(description, "identifier");
		
		ModelMaterialInfo material = new ModelMaterialInfo(GsonHelper.getAsInt(description, "texture_width"), GsonHelper.getAsInt(description, "texture_height"));
		
		HashMap<String, ModelPartInfo> parts = new HashMap<>();
		for(JsonElement bone : bones)
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
					throw new JsonSyntaxException("Can't find parent '"
							+ value.getParentName() + "' for bone '"
							+ value.getName() + "'"
					);
				}
			} else
			{
				rootChildren.add(value);
			}
		}
		
		return Tuples.immutable(new EmbeddedLocation(fileLocation, identifier), makeDefinition(container, material, rootChildren));
	}
	
	private static GeometryDataImpl makeDefinition(IGeometryContainer container, ModelMaterialInfo material, List<ModelPartInfo> roots)
	{
		ModelMeshInfo mesh = new ModelMeshInfo();
		ModelPartInfo root = mesh.getRoot();
		root.addChildren(roots);
		return GeometryDataImpl.create(container, mesh, material.getTextureWidth(), material.getTextureHeight());
	}
	
	private static ModelPartInfo parseBone(JsonObject bone)
	{
		Vector3f pivot = GsonHelper.getAsVec3f(bone, "pivot");
		Vector3f rotation = GsonHelper.getAsVec3f(bone, "rotation", new Vector3f(0, 0, 0));
		boolean mirror = GsonHelper.getAsBoolean(bone, "mirror", false);
		boolean neverRender = GsonHelper.getAsBoolean(bone, "neverRender", false);
		String name = GsonHelper.getAsString(bone, "name");
		String parentName = GsonHelper.getAsString(bone, "parent", "root");
		
		List<ModelPartInfo> children = new ArrayList<>();
		
		List<ModelCubeInfo> cubes = new ArrayList<>();
		if(bone.has("cubes"))
		{
			int i = 0;
			for(JsonElement cubeJson : GsonHelper.getAsJsonArray(bone, "cubes"))
			{
				JsonObject cubeObject = GsonHelper.convertToJsonObject(cubeJson, "member of 'cubes'");
				Vector3f origin = GsonHelper.getAsVec3f(cubeObject, "origin");
				Vector3f size = GsonHelper.getAsVec3f(cubeObject, "size");
				UVDefinition uv = GSON.fromJson(cubeObject.get("uv"), UVDefinition.class);
				boolean cubeMirror = GsonHelper.getAsBoolean(cubeObject, "mirror", mirror);
				float inflate = GsonHelper.getAsFloat(cubeObject, "inflate", 0F);
				
				ModelCubeInfo cube = new ModelCubeInfo(origin, size, uv, inflate, cubeMirror);
				
				if(cubeObject.has("rotation"))
				{
					Vector3f innerRotation = GsonHelper.getAsVec3f(cubeObject, "rotation", new Vector3f(0, 0, 0));
					Vector3f innerPivot = GsonHelper.getAsVec3f(cubeObject, "pivot", new Vector3f(0, 0, 0));
					
					children.add(new ModelPartInfo(Collections.singletonList(cube),
							innerPivot, innerRotation,
							false,
							name + "_generated_" + (i++), name
					));
				} else
					cubes.add(cube);
			}
		}
		
		ModelPartInfo part = new ModelPartInfo(cubes, pivot, rotation, neverRender, name, parentName);
		part.addChildren(children);
		return part;
	}
	
	private static void checkFormatVersion(String version)
	{
		if(!contains(ACCEPTABLE_FORMAT_VERSIONS, version)) throw new JsonSyntaxException("The format version "
				+ version + " is not supported. Supported versions: "
				+ Arrays.toString(ACCEPTABLE_FORMAT_VERSIONS)
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