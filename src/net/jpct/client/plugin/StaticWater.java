package net.jpct.client.plugin;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

import net.jpct.client.util.GameUtil;

public class StaticWater {

	private Object3D water;
	private Object3D water2;
	private float a = 0;
	
	public StaticWater(World world, SimpleVector transformedCenter, float scale) {
		TextureManager textureManager = TextureManager.getInstance();
//		textureManager.addTexture("water", new Texture("./assets/terrain/water_light.png"));
//		textureManager.addTexture("water_blue", new Texture("./assets/terrain/water_dark.png"));
		textureManager.addTexture("water", new Texture("./assets/terrain/water2.jpg"));
		textureManager.addTexture("water_blue", new Texture("./assets/terrain/water.jpg"));

		this.water = Primitives.getPlane(20, 500);
		water.scale(3.8f);
		water.rotateX((float) Math.PI / 2f);
		world.addObject(water);
		water.setTexture("water");
		water.compileAndStrip();

		water.translate(transformedCenter);
		water.translate(0, 1, 0);
		water.setTransparency(15);
		water.setSortOffset(-200000);
		//GameUtil.reTexture(water, 40, true);
		GameUtil.reTexture(water, 40);
		
		System.out.println(water.getTranslation());

		this.water2 = new Object3D(water, true);
		world.addObject(water2);
		water2.shareCompiledData(water);
		water2.compileAndStrip();
		water2.translate(0, 10, 0);
		water2.setSortOffset(-190000);
		water2.setTexture("water_blue");
		//reTexture(water2, 40, false);
		GameUtil.reTexture(water2, 40);
	}
	
	public void update(long ticks) {
		water.translate((float) Math.sin(a), (float) Math.sin(a) / 20f, (float) Math.cos(a));
		water2.translate((float) -Math.sin(a), (float) Math.cos(a) / 20f, -(float) Math.cos(a));
		a += ticks * 0.01f;
	}
	
}
