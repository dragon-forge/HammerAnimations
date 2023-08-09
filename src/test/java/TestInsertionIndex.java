import com.google.common.base.Stopwatch;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.animation.data.*;
import org.zeith.hammeranims.api.animation.interp.Query;
import org.zeith.hammeranims.core.impl.api.animation.*;
import org.zeith.hammeranims.core.impl.api.geometry.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TestInsertionIndex
{
	public static Optional<byte[]> read(ResourceLocation path)
	{
		File root = new File("src\\main\\resources\\assets");
		File namespace = new File(root, path.getNamespace());
		if(!namespace.isDirectory()) return Optional.empty();
		File file = new File(namespace, path.getPath().replace('/', File.separatorChar));
		if(!file.isFile()) return Optional.empty();
		try
		{
			return Optional.of(Files.readAllBytes(file.toPath()));
		} catch(IOException e)
		{
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	public static void main(String[] args)
	{
		AnimationDecoder.init();
		GeometryDecoder.init();
		
		AnimationContainerImpl aCtr = new AnimationContainerImpl()
		{
			@Override
			public ResourceLocation getRegistryKey()
			{
				return HammerAnimations.id("billy");
			}
		};
		
		GeometryContainerImpl gCtr = new GeometryContainerImpl()
		{
			@Override
			public ResourceLocation getRegistryKey()
			{
				return HammerAnimations.id("billy");
			}
		};
		
		aCtr.reload(TestInsertionIndex::read);
		gCtr.reload(TestInsertionIndex::read);
		
//		testAnim(aCtr);
	}
	
	public static void testAnim(IAnimationContainer aCtr)
	{
		String animation = "animation.model.breath";
		String boneName = "chest";
		int frames = 25;
		
		IAnimationData anim = aCtr.getAnimations().get(animation).getData();
		
		BoneAnimation boneAnimation = anim.getBoneAnimations().get(boneName);
		
		System.out.println("Animating " + boneName + " for " + frames + " frames:");
		float seconds = anim.getLength().toMillis() / 1000F;
		float sample = seconds / frames;
		
		Query q = new Query();
		for(int i = 0; i < frames; i++)
		{
			q.anim_time = sample * i;
			System.out.println("S[" + i + "] = " + boneAnimation.getScale(q));
		}
		
		System.out.println("Start");
		
		int count = 0;
		
		Stopwatch sw = Stopwatch.createStarted();
		do
		{
			boneAnimation.getScale(q);
			++count;
		} while(sw.elapsed(TimeUnit.SECONDS) < 10);
		count /= 5;
		
		System.out.println(count);
		System.out.println("144 FPS: " + count / 144);
		
		// With constant: 1.25
		// P = 72837982 op/sec
		// B= ~24279327 bones/sec
		// E = 4046554 ent/sec
		// 144 FPS: 505819
		
		// With constant formula: 1.25
		// P = 35303418 op/sec
		// B = 11767806 bones/sec
		// E = 1961301 ent/sec
		// 144 FPS: 245162
		
		// With formula: 1 + math.sin(query.anim_time / 1.25 * 360) * 0.01 + 0.01
		// P = 35599497 op/sec
		// B = 11866499 bones/sec
		// E = 1977749 ent/sec
		// 144 FPS: 247218
		
		// With keyframes: { "0.0":{"post":[0,0,0],"lerp_mode":"catmullrom"}, "0.5":[46.5,0,0], "1.0":{"pre":[0,0,0],"post":[0,0,0],"lerp_mode":"catmullrom"}, "1.5":[-46.5,0,0], "2.0":{"pre":[0,0,0],"post":[0,0,0],"lerp_mode":"catmullrom"}}
		// P = 40934889 op/sec
		// B = 13644963 bones/sec
		// E = 2,274,160 ent/sec
		// 144 FPS: 284270
		
		// Op/sec (P) = operations per second
		// Operation is a computation of a vec3 for any given bone property (3 ops = 1 bone)
		// Therefore, a number of bones simulated within a second is [P / 3] = B
		// I estimate that any given entity may use ~6 bones on average.. (head, body, 2 hands, 2 legs - not including knees & stuff because some may or may not have those, and some may have much less bones overall)
		// Therefore, a number of entities animated within a second is [B / 6] = E
	}
}