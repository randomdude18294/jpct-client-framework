package net.jpct.client.model;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.threed.jpct.Camera;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.World;

/**
 * An RPG style jPCT camera system.
 */
public class CameraController {

	private final Player player;
	private final Object3D orb;
	private final Camera camera;
	private float cameraRotation = 0;
	private boolean rotateCameraLeft;
	private boolean rotateCameraRight;
	private boolean goUp;
	private boolean goDown;
	private float zoom = 2;
	private float height = 30;

	/**
	 * Initialize the camera manager.
	 */
	public CameraController(final Camera camera, final Player player, final World world) {
		this.camera = camera;
		this.player = player;
		camera.setFOV(1.5f);
		
		/*
		 * Here we create a "focus point", an invisible sphere
		 * which will be translated alongside the character model.
		 */
		this.orb = Primitives.getSphere(1);
		orb.setVisibility(false);
		orb.compile();
		world.addObject(orb);
	}

	/**
	 * Poll keyboard input. Keyboard.KEY_'s used: KEY_LEFT, KEY_RIGHT, KEY_TAB
	 */
	public void pollKeyboard(int keyCode, boolean pressed) {
		/*
		 * Let the user control camera rotation so they can have a 360 degree vision range.
		 * Also give them the option to view the virtual world using a third person camera.
		 */
		switch (keyCode) {
		case Keyboard.KEY_UP:
			goDown = pressed;
			break;
		case Keyboard.KEY_DOWN:
			goUp = pressed;
			break;
		case Keyboard.KEY_LEFT:
			rotateCameraLeft = pressed;
			break;
		case Keyboard.KEY_RIGHT:
			rotateCameraRight = pressed;
			break;
		}
	}
	
	public void pollMouseInput() {
		int dWheel = Mouse.getDWheel();
		if (dWheel < 0) {
			zoom++; // down (zoom out)
			if (zoom > 4f) {
				zoom = 4;
			}
		} else if (dWheel > 0){
			zoom--; // up (zoom in)
			if (zoom < 1f) {
				zoom = 1f;
			}
		}
		if (Mouse.getEventButton() == 2) {
			// middle mouse button
			int dx = Mouse.getDX();
			int dy = Mouse.getDY();
			if (dx != 0) {
				// TODO let the user lift/lower the camera with the dwheel
				//camera.rotateX(dy / 500f);
			}
			if (dy != 0) {
				// TODO let the user rotate the camera with the dwheel
				//camera.rotateAxis(camera.getYAxis(), dx / 500f);
			}
		}
	}

	/**
	 * Update the camera.
	 */
	public void update() {
		if (rotateCameraLeft) {
			cameraRotation += 0.082f;
		}
		if (rotateCameraRight) {
			cameraRotation -= 0.082f;
		}
		if (goUp) {
			height -= 2.8f;
			if (height < 15f) {
				height = 15f;
			}
		}
		if (goDown) {
			height += 2.8f;
			if (height > 75f) {
				height = 75f;
			}
		}
		
		// Translate the true focus point to the player's position in 3D space
		orb.clearTranslation();
		orb.translate(player.getModel().getTransformedCenter());
		
		camera.setPositionToCenter(orb/*abstractHuman.getModel()*/);
		camera.align(orb/*abstractHuman.getModel()*/); // Align the camera with the player
		camera.rotateY(cameraRotation);
		
		camera.rotateCameraX((float) Math.toRadians(height)); // angle
		camera.moveCamera(Camera.CAMERA_MOVEOUT, zoom * 30); // zoom
	}


}