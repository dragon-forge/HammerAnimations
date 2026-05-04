import org.zeith.hammeranims.molang.MoLang;
import org.zeith.hammeranims.molang.runtime.MoLangEnvironment;
import org.zeith.hammeranims.molang.runtime.struct.QueryStruct;

import java.util.*;

public class TestMolang
{
	public static void main(String[] args)
	{
		var runtime = MoLang.createRuntime(false);
		
		MoLangEnvironment otherEnv = new MoLangEnvironment(false);
		otherEnv.setStruct("teststruct", new QueryStruct(otherEnv, new HashMap<>()));
		otherEnv.store("teststruct.test", 5);
		
		var expr = MoLang.parse("context.other->testStruct.test");
		System.out.println(runtime.execute(expr, Map.of(
						"other", otherEnv
				)
		));
	}
}