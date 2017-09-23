package net.jpct.client.util;

import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.TextureInfo;

/**
 * A collection of static game utilities which are particularly useful for bots.
 */
public class GameUtil {

	public GameUtil() {
		// ..
	}

	/**
	 * Determines whether or not 2 vectors are within the given range of eachother.
	 */
	public static boolean inRange(float distance, boolean checkHeight, SimpleVector source, SimpleVector target) {
		SimpleVector p = new SimpleVector(source);
		SimpleVector t = new SimpleVector(target);
		if (!checkHeight) {
			p.y = 0;
			t.y = 0;
		}
		return p.distance(t) <= distance;
	}

	/**
	 * Returns the distance between 2 vectors.
	 */
	public static int getDistance(boolean checkHeight, SimpleVector source, SimpleVector target) {
		SimpleVector p = new SimpleVector(source);
		SimpleVector t = new SimpleVector(target);
		if (!checkHeight) {
			p.y = 0;
			t.y = 0;
		}
		return (int) p.distance(t);
	}
	
	/**
	 * Good for entities face eachother or making projectiles face the target!
	 */
	public static void turnTowardsObject(Object3D source, Object3D target) {
		SimpleVector s = new SimpleVector(source.getTransformedCenter().x, 0, source.getTransformedCenter().z);
		SimpleVector t = new SimpleVector(target.getTransformedCenter().x, 0, target.getTransformedCenter().z);
		SimpleVector direction = new SimpleVector(t.calcSub(s)).normalize();
		Matrix rotationMatrix = new Matrix(direction.getRotationMatrix());
		source.setRotationMatrix(rotationMatrix);
	}

	/**
	 * Good for entities face eachother or making projectiles face the target!
	 */
	public static void turnTowardsObject(Object3D source, SimpleVector target) {
		SimpleVector s = new SimpleVector(source.getTransformedCenter().x, 0, source.getTransformedCenter().z);
		SimpleVector t = target;//new SimpleVector(target.getTransformedCenter().x, 0, target.getTransformedCenter().z);
		SimpleVector direction = new SimpleVector(t.calcSub(s)).normalize();
		Matrix rotationMatrix = new Matrix(direction.getRotationMatrix());
		source.setRotationMatrix(rotationMatrix);
	}

	/**
	 * Translates and object forward using the given velocity.
	 */
	public static void translateForward(Object3D source, float velocity) {
		SimpleVector forward = source.getZAxis();
		forward.scalarMul(velocity);
		SimpleVector moveVector = new SimpleVector(forward);
		source.translate(moveVector);
	}

	public static void reTexture(Object3D obj, float sizeFactor) {
		PolygonManager pm = obj.getPolygonManager();
		int end = pm.getMaxPolygonID();
		for (int i = 0; i < end; i++) {
			int t1 = pm.getPolygonTexture(i);
			SimpleVector uv0 = pm.getTextureUV(i, 0);
			SimpleVector uv1 = pm.getTextureUV(i, 1);
			SimpleVector uv2 = pm.getTextureUV(i, 2);
			uv0.scalarMul(sizeFactor);
			uv1.scalarMul(sizeFactor);
			uv2.scalarMul(sizeFactor);
			TextureInfo textureInfo = new TextureInfo(t1, uv0.x, uv0.y, uv1.x, uv1.y, uv2.x, uv2.y);
			pm.setPolygonTexture(i, textureInfo);
		}
	}

}