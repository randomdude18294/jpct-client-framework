package net.jpct.client.plugin;

import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.OcTree;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

import net.jpct.client.util.GameUtil;

/**
 * A standalone version of the terrain from the "Terrain with foliage example".
 * http://www.jpct.net/wiki/index.php?title=Terrain_with_foliage_example
 */
public class SplatterTerrain {

	public final SimpleVector SPAWN_POINT = new SimpleVector(1049.7107, -372.9779, 2293.5686);
	
	private Object3D terrain;
	private Object3D terrain2;

	public SplatterTerrain(World world, boolean collision, float scale) {
		//world.setClippingPlanes(1, 10000);
		
		TextureManager textureManager = TextureManager.getInstance();
		textureManager.addTexture("ground", new Texture("./assets/terrain/grass.png"));
		textureManager.addTexture("rocks", new Texture("./assets/terrain/snow.png"));
		textureManager.addTexture("sand", new Texture("./assets/terrain/sand.png"));

		Texture details = new Texture("./assets/terrain/details.jpg");
		textureManager.addTexture("details", details);
		details.setEnabled(false);

		terrain = Object3D.mergeAll(Loader.loadOBJ("./assets/terrain/terrain.obj", "./assets/terrain/terrain.mtl", scale));

		terrain.rotateX(-(float) Math.PI / 2f);
		terrain.rotateMesh();
		terrain.setRotationMatrix(new Matrix());

		float[] bb = terrain.getMesh().getBoundingBox();
		float minX = bb[0];
		float maxX = bb[1];
		float minZ = bb[4];
		float maxZ = bb[5];
		float minY = bb[2];
		float maxY = bb[3];
		float dx = maxX - minX;
		float dz = maxZ - minZ;
		dx /= 20f;
		dz /= 20f;

		int tid = textureManager.getTextureID("ground");
		int sid = textureManager.getTextureID("sand");
		int did = textureManager.getTextureID("details");

		PolygonManager pm = terrain.getPolygonManager();
		for (int i = 0; i < pm.getMaxPolygonID(); i++) {
			SimpleVector v0 = pm.getTransformedVertex(i, 0);
			SimpleVector v1 = pm.getTransformedVertex(i, 1);
			SimpleVector v2 = pm.getTransformedVertex(i, 2);
			TextureInfo ti = new TextureInfo(tid, v0.x / dx, v0.z / dz, v1.x / dx, v1.z / dz, v2.x / dx, v2.z / dz);
			float dxd = dx / 10;
			float dzd = dz / 10;
			ti.add(did, v0.x / dxd, v0.z / dzd, v1.x / dxd, v1.z / dzd, v2.x / dxd, v2.z / dzd, TextureInfo.MODE_BLEND);
			// ti.add(did, v0.x / dxd, v0.z / dzd, v1.x / dxd, v1.z / dzd, v2.x
			// / dxd, v2.z / dzd, TextureInfo.MODE_MODULATE);
			pm.setPolygonTexture(i, ti);
		}

		terrain2 = new Object3D(terrain, true);
		terrain2.setSortOffset(-1000);
		terrain2.setTexture("rocks");
		world.addObject(terrain2);

		pm = terrain2.getPolygonManager();
		for (int i = 0; i < pm.getMaxPolygonID(); i++) {
			for (int p = 0; p < 3; p++) {
				SimpleVector v = pm.getTransformedVertex(i, p);
				if (v.y < -1200) {
					pm.setVertexAlpha(i, p, (float) Math.sqrt(2 * (v.y + 1200) / (minY + 1200)));
				} else {
					pm.setVertexAlpha(i, p, 0);
				}
				if (v.y > -200) {
					pm.setPolygonTexture(i, sid);
					pm.setVertexAlpha(i, p, (float) Math.sqrt((v.y + 200) / (maxY + 200)));
				}
			}
		}
		terrain2.setTransparency(0);

		if (collision) {
			OcTree oc = new OcTree(terrain, 50, OcTree.MODE_OPTIMIZED);
			terrain.setOcTree(oc);
			oc.setCollisionUse(OcTree.COLLISION_USE);
			terrain.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			terrain.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
			oc.setCollisionUse(true);
			oc.setRenderingUse(false);
		}

		GameUtil.reTexture(terrain, 32);
		
		/*
		 * Build the terrain here...
		 */
		System.out.println("we made it this far..1");
		terrain.build();
		System.out.println("we made it this far..2");
		
		//TextureManager.getInstance().getTexture("details").setEnabled(showDetails);
		
		// Optimize the object.
//		terrain.createTriangleStrips(2);
//		terrain.compileAndStrip();
//		terrain2.createTriangleStrips(2);
//		terrain2.compileAndStrip();
		System.out.println("we made it this far..3");

		world.addObject(terrain);
	}

	public void translate(float x, float y, float z) {
		terrain.translate(x, y, z);
		terrain.enableLazyTransformations();
		terrain2.translate(x, y, z);
		terrain2.enableLazyTransformations();
	}
	
	public Object3D getTerrain() {
		return terrain;
	}

}
