package dev.zeith.lzvm.molang.parser;

import lombok.Builder;

@Builder(toBuilder = true)
public final class ParserConfig
{
	/**
	 * Whether a cache should be used to speed up executing MoLang.
	 * The cache saves an AST for every parsed expression.
	 * This allows us to skip the tokenization & parsing step before executing known Molang expressions
	 */
	@Builder.Default
	public final boolean useCache = true;
	
	/**
	 * How many expressions can be cached. After reaching `maxCacheSize`, the whole cache is cleared automatically.
	 */
	@Builder.Default
	public final int maxCacheSize = 256;
	
	/**
	 * The optimizer can drastically speed up parsing & executing MoLang.
	 * It enables skipping of unreachable statements, pre-evaluating static expressions and skipping of statements with no effect when used together with the `useAggressiveStaticOptimizer` option
	 */
	@Builder.Default
	public final boolean useOptimizer = true;
	
	/**
	 * Skip execution of statements with no effect
	 * when used together with the `useOptimizer` option
	 */
	@Builder.Default
	public final boolean useAggressiveStaticOptimizer = true;
	
	/**
	 * This options makes early return statements skip all parsing work completely
	 */
	@Builder.Default
	public final boolean earlyReturnsSkipParsing = true;
	
	/**
	 * This options makes early return statements skip all tokenization work completely if earlyReturnsSkipParsing is set to true
	 */
	@Builder.Default
	public final boolean earlyReturnsSkipTokenization = true;
	
	/**
	 * Create expression instances for brackets ("()", "{}")
	 * <p>
	 * This should only be set to true if you want to use the .toString() method of an expression
	 * or you want to iterate over the whole AST
	 */
	@Builder.Default
	public final boolean keepGroups = false;
	
	/**
	 * Whether to convert undefined variables to "0"
	 */
	@Builder.Default
	public final boolean convertUndefined = false;
}