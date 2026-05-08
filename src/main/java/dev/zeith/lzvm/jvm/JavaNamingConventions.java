package dev.zeith.lzvm.jvm;

import java.util.*;

public class JavaNamingConventions
{
	private static final Set<String> JAVA_KEYWORDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
			"continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
			"for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
			"native", "new", "package", "private", "protected", "public", "return", "short", "static",
			"strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
			"try", "void", "volatile", "while", "true", "false", "null"
	)));
	
	public static String processUniqueMethodName(String name, Set<String> used)
	{
		String base = processMethodName(name);
		String candidate = base;
		int index = 1;
		
		// Ensure uniqueness
		while(used.contains(candidate))
		{
			candidate = base + "_" + index++;
		}
		
		used.add(candidate);
		return candidate;
	}
	
	public static String processMethodName(String name)
	{
		if(name == null || name.isEmpty())
			return "_";
		
		StringBuilder sb = new StringBuilder(name.length());
		
		for(int i = 0; i < name.length(); i++)
		{
			char c = name.charAt(i);
			
			if(i == 0)
			{
				if(Character.isJavaIdentifierStart(c))
					sb.append(c);
				else
					sb.append('_');
			} else
			{
				if(Character.isJavaIdentifierPart(c))
					sb.append(c);
				else
					sb.append('_');
			}
		}
		
		String result = sb.toString();
		
		// Avoid single underscore (invalid since Java 9)
		if(result.equals("_"))
			result = "__";
		
		// Avoid keywords
		if(JAVA_KEYWORDS.contains(result))
			result = "_" + result;
		
		return result;
	}
}