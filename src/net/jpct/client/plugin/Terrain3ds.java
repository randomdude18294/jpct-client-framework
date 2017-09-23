package net.jpct.client.plugin;

import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.OcTree;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

public class Terrain3ds {

	public final SimpleVector SPAWN_POINT = new SimpleVector(470.2736, 148.25569, -1086.8627);
	
	private Object3D terrain;

	public Terrain3ds(World world, boolean collision, float scale) {
		TextureManager.getInstance().addTexture("grass", new Texture("./assets/terrain/grass.png"));

		/*
		 * Load the map (i.e. the terrain or "ground")
		 */
		Object3D[] objs = Loader.load3DS("./assets/terrain/terascene.3ds", 100 * scale);
		if (objs.length > 0) {
			terrain = objs[0];
			terrain.setTexture("grass");
		}

		/*
		 * Build the terrain here...
		 */
		terrain.build();

		/*
		 * The terrain isn't located where we want it to, so we take care of
		 * this here:
		 */
		SimpleVector pos = terrain.getCenter();
		pos.scalarMul(-1f);
		terrain.translate(pos);
		terrain.rotateX((float) -Math.PI / 2f);
		terrain.translateMesh();
		terrain.rotateMesh();
		terrain.setTranslationMatrix(new Matrix());
		terrain.setRotationMatrix(new Matrix());
		
		if (collision) {
			OcTree oc = new OcTree(terrain, 50, OcTree.MODE_OPTIMIZED);
			terrain.setOcTree(oc);
			oc.setCollisionUse(OcTree.COLLISION_USE);
			terrain.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			terrain.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
			oc.setCollisionUse(true);
			oc.setRenderingUse(false);
		}
		

		// Optimize the object.
		terrain.createTriangleStrips(2);
		terrain.compileAndStrip();
		
		// Add the object to the jPCT world.
		world.addObject(terrain);
	}

	public void translate(float x, float y, float z) {
		terrain.translate(x, y, z);
		terrain.enableLazyTransformations();
	}
	
	public Object3D getTerrain() {
		return terrain;
	}

}
