package net.jpct.client.model;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.Light;

/**
 * A primitive mobile entity with A* path finding, animations, and an automated movement system.
 */
public class Player {

	private final World world;

	// The 3D model which represents the human
	private Object3D model;

	// Position tracking
	private SimpleVector cachedPosition = new SimpleVector(0, 0, 0);

	// Animation related
	private float animationFrame = 0;
	private int animationId = Animations.IDLE;

	private Light light;

	public Player(World world) {
		this.world = world;

		this.light = new Light(world);
		light.setDiscardDistance(6 * 16); // 16 tiles
		light.setIntensity(255, 255, 255);
		updateLightPosition();
		light.enable();
	}

	public void update() {
		animate(); // animate
	}

	/**
	 * Here we're just using jPCT's md2 animation system.
	 */
	private void animate() {
		animationFrame += 0.02f;
		if (animationId != Animations.WALK && animationId != Animations.IDLE) {
			if (animationFrame > 1) {
				animationId = Animations.IDLE;
			}
		} else {
//			animationId = path != null ? Animations.WALK : Animations.IDLE;
		}
		if (animationFrame > 1) {
			animationFrame -= 1;
		}
		if (model != null) {
			model.animate(animationFrame, animationId);
		}
	}

	/**
	 * Builds a new model instance.
	 */
	public void build(boolean male, float[][] colors) {
		if (model != null) {
			cachedPosition = model.getTransformedCenter();
			world.removeObject(model);
			model = null;
		}
		this.model = AssetManager.getInstance().getMob(colors);
		model.build();
		model.compile(true);
		world.addObject(model);
		model.translate(cachedPosition);
		model.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
	}

	private void updateLightPosition() {
		light.setPosition(new SimpleVector(getX(), -20, getZ()));
	}

	/**
	 * Sets an action animation.
	 * @note Movement animations are automated, and setting a manual animation
	 * will delay the automated animations from happening until the frame set is completed.
	 */
	public void setAnimation(int animationId) {
		this.animationId = animationId;
		this.animationFrame = 0;
	}

	public void goUp() {
		getModel().translate(0, -3f, 0);
	}

	public void goDown() {
		getModel().translate(0, 3f, 0);
	}

	public void rotateLeft(float rotationSpeed) {
		getModel().rotateY(-rotationSpeed);
	}

	public void rotateRight(float rotationSpeed) {
		getModel().rotateY(rotationSpeed);
	}

	public int getHeight() {
		return (int) getModel().getTransformedCenter().y;
	}

	/**
	 * Returns the mobs current position.
	 */
	public SimpleVector getTransformedCenter() {
		return model == null ? cachedPosition : model.getTransformedCenter();
	}

	/**
	 * Returns the mobs unique model instance.
	 */
	public Object3D getModel() {
		return model;
	}

	/**
	 * Returns the mobs X coordinate.
	 */
	public int getX() {
		int x = (int) cachedPosition.x / 6;
		return x * 6;
	}

	/**
	 * Returns the mobs Z coordinate.
	 */
	public int getZ() {
		int z = (int) cachedPosition.z / 6;
		return z * 6;
	}

	public int getAnimationId() {
		return animationId;
	}

	// XXX TEMPORARY
	public void teleport(SimpleVector transformedCenter) {
		this.cachedPosition = transformedCenter;
		model.clearTranslation();
		model.translate(transformedCenter);
	}

}