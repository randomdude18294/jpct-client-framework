package net.jpct.client.model;

import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

import net.jpct.client.model.fire.GenericParticle;

public class PropManager {

	private final static PropManager INSTANCE = new PropManager();

	private Object3D fireModel;
	private GenericParticle particleTest;

	public PropManager() {
		/*
		 * Load the model
		 */
		TextureManager.getInstance().addTexture("flame", new Texture("./assets/fire/flame.jpg"));
		TextureManager.getInstance().addTexture("smoke", new Texture("./assets/fire/smoke.jpg"));
		fireModel = Object3D.mergeAll(Loader.load3DS("./assets/fire/firepit.3ds", 4f));
		TextureManager.getInstance().addTexture("firepit", new Texture("./assets/fire/firepit.jpg"));
		fireModel.setTexture("firepit");
		fireModel.rotateX((float) (-.5 * Math.PI)); // correct the rotation
		fireModel.rotateMesh();
		fireModel.setRotationMatrix(new Matrix());
	}

	public void addFire(World world, float x, float y, float z) {
		Object3D fire = fireModel.cloneObject();
		fire.translate(x, y ,z);
		world.addObject(fire);
		this.particleTest = new GenericParticle(world, fire.getTransformedCenter());
	}
	
	public void update(World world, long ticks) {
		if (particleTest != null) {
			particleTest.update(world);
		}
	}

	public static PropManager getInstance() {
		return INSTANCE;
	}

}
