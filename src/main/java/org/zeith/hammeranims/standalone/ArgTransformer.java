package org.zeith.hammeranims.standalone;

import shaded.json.*;

import java.io.File;
import java.nio.file.Files;
import java.util.function.Function;

public record ArgTransformer<T>(SafeFunction<T> validator, String requirements)
{
	public static final ArgTransformer<JSONObject> JSON_OBJECT = new ArgTransformer<>(ArgTransformer::getJsonObject, "must be a valid json object");
	public static final ArgTransformer<JSONArray> JSON_ARRAY = new ArgTransformer<>(ArgTransformer::getJsonArray, "must be a valid json array");
	public static final ArgTransformer<Integer> INTEGER = new ArgTransformer<>(ArgTransformer::getInteger, "must be a valid integer");
	public static final ArgTransformer<Integer> POSITIVE_INTEGER = new ArgTransformer<>(ArgTransformer::getPositiveInteger, "must be a positive integer");
	
	private static JSONObject getJsonObject(String path)
			throws Exception
	{
		return (JSONObject) new JSONTokener(path).nextValue();
	}
	
	private static JSONArray getJsonArray(String path)
			throws Exception
	{
		return (JSONArray) new JSONTokener(path).nextValue();
	}
	
	private static int getInteger(String path)
			throws Exception
	{
		return Integer.parseInt(path);
	}
	
	private static int getPositiveInteger(String path)
			throws Exception
	{
		int i = getInteger(path);
		if(i <= 0) throw new IllegalArgumentException();
		return i;
	}
	
	private static boolean isValidWritableLocation(String path)
			throws Exception
	{
		File f = new File(path);
		if(f.exists()) return false;
		try(var out = Files.newOutputStream(f.toPath()))
		{
		}
		f.delete();
		return true;
	}
	
	public interface SafeFunction<T>
			extends Function<String, T>
	{
		@Override
		default T apply(String s)
		{
			try
			{
				return applyUnsafe(s);
			} catch(Exception e)
			{
				throw new IProcedure.ConfigureException("Unable to parse input " + s, e);
			}
		}
		
		T applyUnsafe(String s)
				throws Exception;
	}
}