package net.jpct.client.model;

import java.util.Random;

import com.threed.jpct.*;

public class AssetManager {

	private final static AssetManager INSTANCE = new AssetManager();

	private TextureManager textureManager;
	private final Object3D male;
	private TextureInfo maleBaseTexture;

	private final Random random;
	
	public AssetManager() {
		this.textureManager = TextureManager.getInstance();
		this.male = generateMale();
		this.random = new Random(6812);
	}

	private Object3D generateMale() {
		Object3D male = Loader.loadMD2("./assets/player/male.md2", .10f);
		textureManager.addTexture("male", new Texture("./assets/player/male.jpg"));
		textureManager.addTexture("male-mask", new Texture("./assets/player/male.png"));
		this.maleBaseTexture = new TextureInfo(textureManager.getTextureID("male"));
		maleBaseTexture.add(textureManager.getTextureID("male-mask"), TextureInfo.MODE_ADD);
		male.setTexture(maleBaseTexture);
		male.rotateY((float) Math.PI * 1.5f); // Properly rotate the MD2 model
		male.rotateMesh();
		male.compile(true); // Compile dynamic object
		return male;
	}

	public Object3D getMob(final float[][] colors) {
		Object3D obj = new Object3D(male, false);
		GLSLShader shader = new GLSLShader(Loader.loadTextFile("./assets/player/player.vert"), Loader.loadTextFile("./assets/player/player.frag")) {
			@Override
			public void setCurrentObject3D(Object3D obj) {
				setUniform("colorMul0", colors[0]);
				setUniform("colorMul1", colors[1]);
				setUniform("colorMul2", colors[2]);
				setStaticUniform("map0", 0);
				setStaticUniform("map1", 1);
			}
		};
		obj.setRenderHook(shader);
		return obj;
	}

	public float[][] getRandomMobAppearanceColors() {
		float[][] colors = new float[][] { { random.nextFloat(), random.nextFloat(), random.nextFloat() }, { random.nextFloat(), random.nextFloat(), random.nextFloat() }, { random.nextFloat(), random.nextFloat(), random.nextFloat() } };
		return colors;
	}

	public static AssetManager getInstance() {
		return INSTANCE;
	}

}