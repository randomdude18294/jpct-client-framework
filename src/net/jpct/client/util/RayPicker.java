package net.jpct.client.util;

import com.threed.jpct.*;

import net.jpct.client.model.*;


// XXX I DID NOT THOROUGHLY BUG TEST or QUALITY TEST THE RAY PICKER IMPLEMENTATIONS
public class RayPicker {

	private final float MAXIMUM_COLLISION_DISTANCE = Config.farPlane;

	private final DesktopEngine engine;
	private final FrameBuffer frameBuffer;
	private final World world;

	public RayPicker(DesktopEngine engine, FrameBuffer frameBuffer, World world) {
		this.engine = engine;
		this.frameBuffer = frameBuffer;
		this.world = world;
	}

	public float[] pickTile(Player player, Camera camera) {
		float tile[] = { -1, -1, -1 };

		// Get the 3D coordinates in camera space, and convert the coordinates to world space...
		SimpleVector rayProjection = new SimpleVector(Interact2D.reproject2D3D(camera, frameBuffer, engine.getMouseX(), engine.getMouseY()));
		rayProjection.matMul(camera.getBack().invert3x3());
		rayProjection.add(camera.getPosition());

		// Determine the direction and distance from the camera to the click in the 3D world...
		SimpleVector direction = rayProjection.calcSub(camera.getPosition()).normalize();
		float distance = world.calcMinDistance(rayProjection, direction, MAXIMUM_COLLISION_DISTANCE);

		if (distance == Object3D.COLLISION_NONE) {
			// We didn't click an object with collision enabled. Nothing to do!
			return tile;
		}

		// Calculate the exact 3D coordinates for the point that was clicked...
		SimpleVector mouseCollision3D = new SimpleVector(direction);
		mouseCollision3D.scalarMul(distance);
		mouseCollision3D.add(rayProjection);
		SimpleVector mousePosition3D = null;

		// Get the mouse coordinates in 3D space...
		SimpleVector ray = Interact2D.reproject2D3DWS(camera, frameBuffer, engine.getMouseX(), engine.getMouseY());
		if (ray != null) {
			SimpleVector norm = ray.normalize();
			float f = world.calcMinDistance(camera.getPosition(), norm, MAXIMUM_COLLISION_DISTANCE);
			if (f != Object3D.COLLISION_NONE) {
				mousePosition3D = new SimpleVector();
				SimpleVector offset = new SimpleVector(norm);
				norm.scalarMul(f);
				norm = norm.calcSub(offset);
				mousePosition3D.add(norm);
				mousePosition3D.add(camera.getPosition());
			}
		}
		if (mousePosition3D != null) {
			mousePosition3D = mousePosition3D.calcSub(player.getModel().getTranslation());
//			tile[0] = (int) (Math.floor(mouseCollision3D.x) - (Math.floor(mouseCollision3D.x) % 2));
//			tile[1] = (float) mousePosition3D.y;
//			tile[2] = (int) (Math.floor(mouseCollision3D.z) - (Math.floor(mouseCollision3D.z) % 2));
			tile[0] = mouseCollision3D.x;
			tile[1] = mouseCollision3D.y;
			tile[2] = mouseCollision3D.z;
			System.out.println("Clicked x:" + tile[0] + ", z:" + tile[2] + " || height: " + tile[1]);
		}
		return tile;
	}

	public Object[] pickObjects(Camera camera) {
		SimpleVector ray = Interact2D.reproject2D3DWS(camera, frameBuffer, engine.getMouseX(), engine.getMouseY()).normalize();
		Object[] collisions = world.calcMinDistanceAndObject3D(camera.getPosition(), ray, MAXIMUM_COLLISION_DISTANCE);
		if (collisions.length == 0) {
			return null;
		}
		if (collisions[0] != (Object) Object3D.COLLISION_NONE && collisions[1] != null) {
			return collisions;
		}
		return null;
	}

}