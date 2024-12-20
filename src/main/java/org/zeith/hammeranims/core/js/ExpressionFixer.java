package org.zeith.hammeranims.core.js;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Contains a list of fixes that should be applied to all expressions before they are passed to evaluator.
 */
public class ExpressionFixer
{
	private static final List<UnaryOperator<String>> FIXES = new ArrayList<>();
	
	static
	{
		// Fix dangling math operators matching +-*/%&^|? FOLLOWED BY 0+ whitespaces FOLLOWED BY ) by replacing with just )
		registerFix(input -> input.replaceAll("[+\\-*\\/%&^|?]+\\s*\\)", ")"));
		
		// Fix dangling math operators matching +-*/%&^|? FOLLOWED BY 0+ whitespaces by deleting them.
		registerFix(input -> input.replaceAll("[+\\-*\\/%&^|?]+\\s*$", ""));
	}
	
	public static void registerFix(UnaryOperator<String> fix)
	{
		FIXES.add(fix);
	}
	
	public static String fixExpression(String input)
	{
		for(UnaryOperator<String> fix : FIXES) input = fix.apply(input);
		return input;
	}
}