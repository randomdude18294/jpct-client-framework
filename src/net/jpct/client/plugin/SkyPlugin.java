package net.jpct.client.plugin;

import java.awt.Color;

import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.LensFlare;
import com.threed.jpct.util.Light;

/**
 * A collection of sky-related objects.
 * Enable some, or all, modify to suit your needs.
 */
public class SkyPlugin {

	private World sky;
	private Object3D dome;
	private Object3D sunObj;
	private LensFlare lens;
	private Light sun;
	private SimpleVector sunDir = new SimpleVector(0, -20000, 0);
	private SimpleVector sunPos = new SimpleVector(100, 0, 20100);
	private SimpleVector up = new SimpleVector(0, -1, 0);
	private float oldAng = 999;
	private float ang = 0f;
	private SimpleVector sunp = new SimpleVector(sunPos);
	private Color skyColor = new Color(195, 210, 230);
	//private float fogParam[] = { 15000, 20000 }; // fog coords
	private float fogParam[] = { Config.farPlane - (Config.farPlane / 4), Config.farPlane }; // fog coords

	public SkyPlugin() {
		this.sky = new World();
		sky.createTriangleStrips();
		sky.setClippingPlanes(1, 750000); // TODO
	}

	public void initFog(World world) {
		world.setFoggingMode(World.FOGGING_PER_PIXEL);
		world.setFogging(World.FOGGING_ENABLED);
		world.setFogParameters(fogParam[0], fogParam[1], skyColor.getRed(), skyColor.getGreen(), skyColor.getBlue());

	}
	public void initDome() {
		TextureManager.getInstance().addTexture("sky", new Texture("./assets/sky/sky.jpg"));
		dome = Object3D.mergeAll(Loader.load3DS("./assets/sky/dome.3ds", 5000));
		dome.setTexture("sky");
		sky.addObject(dome);
		dome.compileAndStrip();
		dome.setLighting(Object3D.LIGHTING_NO_LIGHTS);
		dome.setAdditionalColor(Color.WHITE);
		dome.rotateX(-(float) Math.PI / 2f);
		sky.buildAllObjects();
		dome.setTransparency(-1);
	}

	public void initSkybox() {
		// TODO
	}

	public void initSun(World world) {
		TextureManager.getInstance().addTexture("sun", new Texture("./assets/sky/sun.jpg"));
		sunObj = Primitives.getPlane(1, 4000);
		sunObj.setTexture("sun");
		sunObj.setTransparency(12);
		sunObj.setTransparencyMode(Object3D.TRANSPARENCY_MODE_ADD);
		sunObj.setLighting(Object3D.LIGHTING_NO_LIGHTS);
		sunObj.setAdditionalColor(Color.WHITE);
		sunObj.setBillboarding(true);
		sky.addObject(sunObj);
		if (sun == null) {
			sun = new Light(world);
			sun.setIntensity(150, 150, 150);
			sun.setAttenuation(-1);
		}
	}

	public void initLens(World world) {
		TextureManager.getInstance().addTexture("burst", new Texture("./assets/sky/lens1.jpg", true));
		TextureManager.getInstance().addTexture("halo1", new Texture("./assets/sky/lens2.jpg", true));
		TextureManager.getInstance().addTexture("halo2", new Texture("./assets/sky/lens3.jpg", true));
		TextureManager.getInstance().addTexture("halo3", new Texture("./assets/sky/lens4.jpg", true));
		if (sun == null) {
			sun = new Light(world);
			sun.setIntensity(150, 150, 150);
			sun.setAttenuation(-1);
		}
		lens = new LensFlare(SimpleVector.ORIGIN, "burst", "halo1", "halo2", "halo3");
		lens.setTransparency(12);
		lens.setGlobalScale(1.5f);
	}

	public void update(World world, long ticks) {

		/*
		 * Process the sun and sky
		 */
		sunDir.rotateZ(ticks * 0.00002f); // down
		//sunDir.rotateZ(ticks * -0.01f); // up

		sunp = new SimpleVector(sunPos);
		sunp.add(sunDir);
		if (sun != null) {
			sun.setPosition(sunp);
		}
		if (sunObj != null) {
			sunObj.getTranslationMatrix().setIdentity();
			sunObj.translate(sunp);
		}
		if (lens != null) {
			lens.setLightPosition(sunp);
		}
		sunp = sunp.calcSub(sunPos);
		SimpleVector sn = sunp.normalize();
		ang = sn.calcAngle(up);

		if (ang != oldAng) {
			oldAng = ang;
			float cos = (float) Math.cos(ang);
			world.setAmbientLight((int) (90f * Math.sqrt(cos) + 10), (int) (90f * cos + 10), (int) (90f * cos + 10));
			if (sun != null) {
				sun.setIntensity((int) (140f * Math.sqrt(cos) + 10), (int) (140f * cos + 10), (int) (140f * cos + 10));
			}
			if (cos > 0) {
				Color col = new Color((int) (245f * cos + 10), (int) (245f * cos * cos + 10), (int) (245f * cos * cos + 10));
				skyColor = new Color((int) (195f * Math.sqrt(Math.sqrt(cos))), (int) (210f * Math.sqrt(cos)), (int) (230f * Math.sqrt(cos)));
				if (world.getFoggingMode() != World.FOGGING_DISABLED) {
					world.setFogParameters(fogParam[0], fogParam[1], skyColor.getRed(), skyColor.getGreen(), skyColor.getBlue());
				}
				if (dome != null) {
					dome.setAdditionalColor(col);
				}
				if (sunObj != null) {
					sunObj.setAdditionalColor(col);
				}
			}
		}
		if (dome != null) {
			dome.rotateY(0.0002f * ticks);
		}
	}

	public void renderPreWorld(FrameBuffer frameBuffer) {
		frameBuffer.clear(skyColor);
		sky.renderScene(frameBuffer);
		sky.draw(frameBuffer);
	}

	public void renderPostWorld(FrameBuffer frameBuffer, World world) {
		if (lens == null) {
			return;
		}
		lens.update(frameBuffer, world);
		lens.render(frameBuffer);
	}

	public int getPolyCount() {
		return sky.getVisibilityList().getSize();
	}


}