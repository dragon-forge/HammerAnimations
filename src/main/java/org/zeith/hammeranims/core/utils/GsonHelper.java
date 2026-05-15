package org.zeith.hammeranims.core.utils;

import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.json.*;

import java.util.*;
import java.util.Map.Entry;

public class GsonHelper
{
	public static Vector3f getAsVec3f(JSONObject parent, String memberName)
	{
		return toVec3f(getAsJsonArray(parent, memberName), memberName);
	}
	
	public static Vector3f getAsVec3f(JSONObject parent, String memberName, Vector3f defaultVal)
	{
		if(parent.has(memberName))
		{
			return getAsVec3f(parent, memberName);
		} else
		{
			return defaultVal;
		}
	}
	
	public static Vector3f toVec3f(JSONArray array, String name)
	{
		if(array.length() != 3)
		{
			throw new JSONException("Expected 3 elements in '" + name + "' array, found: " + array.length());
		}
		
		return new Vector3f(convertToFloat(array.get(0), "member of '" + name + "'"),
				convertToFloat(array.get(1), "member of '" + name + "'"),
				convertToFloat(array.get(2), "member of '" + name + "'")
		);
	}
	
	public static Vector2f getAsVec2f(JSONObject parent, String memberName)
	{
		return toVec2f(getAsJsonArray(parent, memberName), memberName);
	}
	
	public static Vector2f getAsVec3f(JSONObject parent, String memberName, Vector2f defaultVal)
	{
		if(parent.has(memberName))
		{
			return getAsVec2f(parent, memberName);
		} else
		{
			return defaultVal;
		}
	}
	
	public static Vector2f toVec2f(JSONArray array, String name)
	{
		if(array.length() != 2)
		{
			throw new JSONException("Expected 2 elements in '" + name + "' array, found: " + array.length());
		}
		
		return new Vector2f(convertToFloat(array.get(0), "member of '" + name + "'"),
				convertToFloat(array.get(1), "member of '" + name + "'")
		);
	}
	
	/**
	 * Does the given JSONObject contain an array field with the given name?
	 */
	public static boolean isArrayNode(JSONObject pJson, String pMemberName)
	{
		return !isValidNode(pJson, pMemberName) ? false : pJson.get(pMemberName) instanceof JSONArray;
	}
	
	public static boolean isObjectNode(JSONObject pJson, String pMemberName)
	{
		return !isValidNode(pJson, pMemberName) ? false : pJson.get(pMemberName) instanceof JSONObject;
	}
	
	/**
	 * Does the given JSONObject contain a field with the given name whose type is primitive (String, Java primitive, or
	 * Java primitive wrapper)?
	 */
	public static boolean isValidPrimitive(JSONObject pJson, String pMemberName)
	{
		return !isValidNode(pJson, pMemberName)
			   ? false
			   : pJson.get(pMemberName) instanceof Object o;
	}
	
	/**
	 * Does the given JSONObject contain a field with the given name?
	 */
	public static boolean isValidNode(@Nullable JSONObject pJson, String pMemberName)
	{
		if(pJson == null)
		{
			return false;
		} else
		{
			return pJson.get(pMemberName) != null;
		}
	}
	
	public static Object getNonNull(JSONObject pJson, String pMemberName)
	{
		return pJson.get(pMemberName);
	}
	
	/**
	 * Gets the string value of the given JsonElement.  Expects the second parameter to be the name of the element's
	 * field if an error message needs to be thrown.
	 */
	public static String convertToString(Object pJson, String pMemberName)
	{
		if(pJson instanceof String s)
		{
			return s;
		} else
		{
			throw new JSONException("Expected " + pMemberName + " to be a string, was " + getType(pJson));
		}
	}
	
	/**
	 * Gets the string value of the field on the JSONObject with the given name.
	 */
	public static String getAsString(JSONObject pJson, String pMemberName)
	{
		if(pJson.has(pMemberName))
		{
			return convertToString(pJson.get(pMemberName), pMemberName);
		} else
		{
			throw new JSONException("Missing " + pMemberName + ", expected to find a string");
		}
	}
	
	/**
	 * Gets the string value of the field on the JSONObject with the given name, or the given default value if the field
	 * is missing.
	 */
	@Nullable
	public static String getAsString(JSONObject pJson, String pMemberName, @Nullable String pFallback)
	{
		return pJson.has(pMemberName) ? convertToString(pJson.get(pMemberName), pMemberName) : pFallback;
	}
	
	/**
	 * Gets the boolean value of the given JsonElement.  Expects the second parameter to be the name of the element's
	 * field if an error message needs to be thrown.
	 */
	public static boolean convertToBoolean(Object pJson, String pMemberName)
	{
		if(pJson instanceof Boolean b)
		{
			return b.booleanValue();
		} else if(pJson instanceof String s && (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")))
			return Boolean.parseBoolean(s);
		else
		{
			throw new JSONException("Expected " + pMemberName + " to be a Boolean, was " + getType(pJson));
		}
	}
	
	/**
	 * Gets the boolean value of the field on the JSONObject with the given name.
	 */
	public static boolean getAsBoolean(JSONObject pJson, String pMemberName)
	{
		return pJson.getBoolean(pMemberName);
	}
	
	/**
	 * Gets the boolean value of the field on the JSONObject with the given name, or the given default value if the field
	 * is missing.
	 */
	public static boolean getAsBoolean(JSONObject pJson, String pMemberName, boolean pFallback)
	{
		return pJson.optBoolean(pMemberName, pFallback);
	}
	
	public static double convertToDouble(Object pJson, String pMemberName)
	{
		if(pJson instanceof Number num)
		{
			return num.doubleValue();
		} else
		{
			throw new JSONException("Expected " + pMemberName + " to be a Double, was " + getType(pJson));
		}
	}
	
	public static double getAsDouble(JSONObject pJson, String pMemberName)
	{
		return pJson.getDouble(pMemberName);
	}
	
	public static double getAsDouble(JSONObject pJson, String pMemberName, double pFallback)
	{
		return pJson.optDouble(pMemberName, pFallback);
	}
	
	/**
	 * Gets the float value of the given JsonElement.  Expects the second parameter to be the name of the element's field
	 * if an error message needs to be thrown.
	 */
	public static float convertToFloat(Object pJson, String pMemberName)
	{
		if(pJson instanceof Number num)
		{
			return num.floatValue();
		} else
		{
			throw new JSONException("Expected " + pMemberName + " to be a Float, was " + getType(pJson));
		}
	}
	
	/**
	 * Gets the float value of the field on the JSONObject with the given name.
	 */
	public static float getAsFloat(JSONObject pJson, String pMemberName)
	{
		return pJson.getFloat(pMemberName);
	}
	
	/**
	 * Gets the float value of the field on the JSONObject with the given name, or the given default value if the field
	 * is missing.
	 */
	public static float getAsFloat(JSONObject pJson, String pMemberName, float pFallback)
	{
		return pJson.optFloat(pMemberName, pFallback);
	}
	
	/**
	 * Gets the integer value of the given JsonElement.  Expects the second parameter to be the name of the element's
	 * field if an error message needs to be thrown.
	 */
	public static int convertToInt(Object pJson, String pMemberName)
	{
		if(pJson instanceof Number num)
		{
			return num.intValue();
		} else
		{
			throw new JSONException("Expected " + pMemberName + " to be a Int, was " + getType(pJson));
		}
	}
	
	/**
	 * Gets the integer value of the field on the JSONObject with the given name.
	 */
	public static int getAsInt(JSONObject pJson, String pMemberName)
	{
		if(pJson.has(pMemberName))
		{
			return convertToInt(pJson.get(pMemberName), pMemberName);
		} else
		{
			throw new JSONException("Missing " + pMemberName + ", expected to find a Int");
		}
	}
	
	/**
	 * Gets the integer value of the field on the JSONObject with the given name, or the given default value if the field
	 * is missing.
	 */
	public static int getAsInt(JSONObject pJson, String pMemberName, int pFallback)
	{
		return pJson.has(pMemberName) ? convertToInt(pJson.get(pMemberName), pMemberName) : pFallback;
	}
	
	/**
	 * Gets the given JsonElement as a JSONObject.  Expects the second parameter to be the name of the element's field if
	 * an error message needs to be thrown.
	 */
	public static JSONObject convertToJSONObject(Object pJson, String pMemberName)
	{
		if(pJson instanceof JSONObject o)
			return o;
		throw new JSONException("Expected " + pMemberName + " to be a JSONObject, was " + getType(pJson));
	}
	
	public static JSONObject getAsJSONObject(JSONObject pJson, String pMemberName)
	{
		return pJson.getJSONObject(pMemberName);
	}
	
	/**
	 * Gets the JSONObject field on the JSONObject with the given name, or the given default value if the field is
	 * missing.
	 */
	@Nullable
	public static JSONObject getAsJSONObject(JSONObject pJson, String pMemberName, @Nullable JSONObject pFallback)
	{
		return pJson.optJSONObject(pMemberName, pFallback);
	}
	
	/**
	 * Gets the given JsonElement as a JsonArray.  Expects the second parameter to be the name of the element's field if
	 * an error message needs to be thrown.
	 */
	public static JSONArray convertToJsonArray(Object pJson, String pMemberName)
	{
		if(pJson instanceof JSONArray arr)
			return arr;
		
		throw new JSONException("Expected " + pMemberName + " to be a JsonArray, was " + getType(pJson));
	}
	
	/**
	 * Gets the JsonArray field on the JSONObject with the given name.
	 */
	public static JSONArray getAsJsonArray(JSONObject pJson, String pMemberName)
	{
		return pJson.getJSONArray(pMemberName);
	}
	
	/**
	 * Gets the JsonArray field on the JSONObject with the given name, or the given default value if the field is
	 * missing.
	 */
	@Nullable
	public static JSONArray getAsJsonArray(JSONObject pJson, String pMemberName, @Nullable JSONArray pFallback)
	{
		return pJson.optJSONArray(pMemberName, pFallback);
	}
	
	public static boolean isEmpty(final CharSequence cs)
	{
		return cs == null || cs.length() == 0;
	}
	
	public static String abbreviateMiddle(final String str, final String middle, final int length)
	{
		if(isEmpty(str) || isEmpty(middle))
		{
			return str;
		}
		
		if(length >= str.length() || length < middle.length() + 2)
		{
			return str;
		}
		
		final int targetSting = length - middle.length();
		final int startOffset = targetSting / 2 + targetSting % 2;
		final int endOffset = str.length() - targetSting / 2;
		
		final StringBuilder builder = new StringBuilder(length);
		builder.append(str.substring(0, startOffset));
		builder.append(middle);
		builder.append(str.substring(endOffset));
		
		return builder.toString();
	}
	
	/**
	 * Gets a human-readable description of the given JsonElement's type.  For example: "a number (4)"
	 */
	public static String getType(@Nullable Object pJson)
	{
		String s = abbreviateMiddle(String.valueOf(pJson), "...", 10);
		if(pJson == null) return "null (missing)";
		else if(pJson instanceof JSONArray) return "an array (" + s + ")";
		else if(pJson instanceof JSONObject) return "an object (" + s + ")";
		else if(pJson instanceof Number) return "a number (" + s + ")";
		else if(pJson instanceof Boolean) return "a boolean (" + s + ")";
		return s;
	}
	
	private static Collection<Entry<String, Object>> sortByKeyIfNeeded(Collection<Entry<String, Object>> pEntries, @Nullable Comparator<String> pSorter)
	{
		if(pSorter == null)
		{
			return pEntries;
		} else
		{
			List<Entry<String, Object>> list = new ArrayList<>(pEntries);
			list.sort(Entry.comparingByKey(pSorter));
			return list;
		}
	}
	
	
	/**
	 * Gets the given JsonElement as a JsonObject.  Expects the second parameter to be the name of the element's field if
	 * an error message needs to be thrown.
	 */
	public static JSONObject convertToJsonObject(Object pJson, String pMemberName)
	{
		if(pJson instanceof JSONObject o)
		{
			return o;
		} else
		{
			throw new JSONException("Expected " + pMemberName + " to be a JsonObject, was " + getType(pJson));
		}
	}
	
	public static JSONObject getAsJsonObject(JSONObject pJson, String pMemberName)
	{
		return pJson.getJSONObject(pMemberName);
	}
	
	public static JSONObject parse(String pJson, boolean pLenient)
	{
		return new JSONObject(pJson);
	}
}