package net.jpct.client.plugin;

import com.threed.jpct.Object3D;
import com.threed.jpct.OcTree;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

import net.jpct.client.util.GameUtil;

/**
 * A generic terrain plugin. Constructed using a textured plane.
 */
public class PrimitiveTerrain {
	
	public final SimpleVector SPAWN_POINT = new SimpleVector(8, 0, 8);
	
	private final Object3D terrain;
	
	public PrimitiveTerrain(World world, boolean collision, float scale) {
		// Create the object.
		this.terrain = Primitives.getPlane(1, 128f * scale);
		
		// Correct the rotation.
		terrain.rotateX((float) (0.5 * Math.PI));
		
		// Load and apply the ground texture.
		TextureManager.getInstance().addTexture("ground", new Texture("./assets/terrain/sand.png"));
		terrain.setTexture("ground");
		GameUtil.reTexture(terrain, 32);
		
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
