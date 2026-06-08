package dev.zeith.lzvm.api;

import lombok.Getter;

import java.util.*;

public class LzCompilerManager
{
	private final Map<String, LzCompilerFactory> nameAssociations = new HashMap<>();
	
	@Getter
	protected final Set<String> knownLanguages;
	
	public LzCompilerManager()
	{
		this(Thread.currentThread().getContextClassLoader());
	}
	
	public LzCompilerManager(final ClassLoader loader)
	{
		Set<String> langs = new HashSet<>();
		for(LzCompilerFactory compiler : getServiceLoader(loader))
		{
			langs.addAll(compiler.getLanguages());
			for(String lng : compiler.getLanguages()) nameAssociations.put(lng, compiler);
		}
		this.knownLanguages = Collections.unmodifiableSet(langs);
	}
	
	public LzCompilerFactory findByLanguage(String language)
	{
		language = language.toLowerCase(Locale.ROOT);
		return nameAssociations.get(language);
	}
	
	private ServiceLoader<LzCompilerFactory> getServiceLoader(final ClassLoader loader)
	{
		if(loader != null)
		{
			return ServiceLoader.load(LzCompilerFactory.class, loader);
		} else
		{
			return ServiceLoader.loadInstalled(LzCompilerFactory.class);
		}
	}
}