package net.jpct.client.model;

import java.awt.Color;
import java.io.File;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.threed.jpct.*;
import com.threed.jpct.threading.WorkLoad;
import com.threed.jpct.threading.Worker;

import net.jpct.client.plugin.*;
import net.jpct.client.util.RayPicker;
import net.jpct.client.util.Ticker;

public class DesktopEngine {

	private boolean keepAlive = false;
	private World world;
	private FrameBuffer frameBuffer;
	private int outputHeight;
	private Camera camera;
	public Ticker ticker = new Ticker(15);
	private boolean wireFrame = false;
	private Player player;
	private CameraController cameraController;
	private GuiHelper gui;
	private SkyPlugin sky;
	private RayPicker picker;

	private int frameCount = 0;
	private int polyCount = 0;
	private String memUsage = "..";
	
	private boolean cpuThrottle = true; // eh..

	private StaticWater water;
	private PropManager propManager;
	
	public DesktopEngine() {
		Config.glWindowName = "jPCT Client Framework";
		try {
			this.frameBuffer = new FrameBuffer(800, 600, FrameBuffer.SAMPLINGMODE_HARDWARE_ONLY);
			frameBuffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
			frameBuffer.enableRenderer(IRenderer.RENDERER_OPENGL);
			this.outputHeight = frameBuffer.getOutputHeight();
			Display.setResizable(true);
			Mouse.create();
			Keyboard.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.err.println("Error initializing display / listeners!");
			System.exit(1);
		} finally {
			System.out.println("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
			System.out.println("jPCT Version: " + Config.getVersion());
		}
		
		/*
		 * Load 2D textures.
		 */
		this.gui = new GuiHelper(frameBuffer);
		gui.addTexture("cursor", new File("./assets/cursor.png"));
		
		/*
		 * Construct the jPCT world instance.
		 */
		initGame();
		
		/*
		 * Initialize the ray picker.
		 */
		this.picker = new RayPicker(this, frameBuffer, world);
		
		/*
		 * Initialize entity related objects/handlers.
		 */
		// XXX TEMPORARY - JUST TESTING
		this.propManager = PropManager.getInstance();
		propManager.addFire(world, 411.59613f, 172.20947f, -1491.0514f);
		
		run();
	}

	private void run() {
		long lastReset = Ticker.currentTime();
		int ticks = 0;
		int fps = 0;
		@SuppressWarnings("unused")
		int tickCount = 0;
		while (keepAlive) {
			ticks = ticker.getTicks();

			if (Display.isCloseRequested()) {
				keepAlive = false;
				return;
			}
			if (Display.wasResized()) {
				frameBuffer.resize(Display.getWidth(), Display.getHeight());
				frameBuffer.refresh();
				this.outputHeight = frameBuffer.getOutputHeight();
			}

			long currentTime = Ticker.currentTime();
			if (currentTime - lastReset > 999) {
				lastReset = currentTime;
				frameCount = fps;
				fps = 0;
				polyCount = world.getVisibilityList().getSize() + (sky == null ? 0 : sky.getPolyCount());
				memUsage = (((int) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) >> 10) + "K";
				
				//System.out.println("tps: " + tickCount);
				tickCount = 0;
			}

			if (ticks > 0) {
				tickCount++;
				if (Keyboard.isCreated()) {
					pollKeyboard();
				}
				if (Mouse.isCreated()) {
					pollMouse();
				}
				update(ticks); // Update game logic
			}

			// Render
			render();
			fps++;
			
			if (cpuThrottle) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException ignore) {
				}
			}
		}
		shutdown();
	}

	private void initGame() {
		this.world = new World();
		world.createTriangleStrips();
		world.setAmbientLight(120, 120, 120);
		world.getLights().setRGBScale(Lights.OVERBRIGHT_LIGHTING_DISABLED);

		/*
		 * Configure the sky.
		 */
		this.sky = new SkyPlugin();
		sky.initFog(world);
		sky.initDome();
		sky.initSun(world);
		sky.initLens(world);

		/*
		 * Construct the main character.
		 */
		this.player = new Player(world);
		player.build(true, AssetManager.getInstance().getRandomMobAppearanceColors());

		/*
		 * Initialize the camera controller.
		 */
		this.camera = world.getCamera();
		this.cameraController = new CameraController(camera, player, world);
		
		/*
		 * Construct a terrain.
		 */
		Worker worker = new Worker(1);
		worker.add(new WorkLoad() {
			@Override
			public void doWork() {
				//PrimitiveTerrain terrain = new PrimitiveTerrain(world, true, 4f);
				//Terrain3ds terrain = new Terrain3ds(world, true, 4f);
				SplatterTerrain terrain = new SplatterTerrain(world, true, 1f);
				
				player.teleport(terrain.SPAWN_POINT);
				water = new StaticWater(world, terrain.getTerrain().getTransformedCenter(), 1);
			}
			@Override
			public void done() {
				System.out.println(Config.glWindowName + " is Running!");
				keepAlive = true;
			}
			@Override
			public void error(Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		});
		worker.waitForAll();
		worker.dispose();
	}

	private void render() {
		Config.glTransparencyOffset = 0.0f;
		Config.glTransparencyMul = 0.025f;
		if (wireFrame) {
			// TODO wireframe renderer (green and black)
		} else {
			if (sky == null) {
				frameBuffer.clear(Color.BLACK);
			}
			if (sky != null) {
				sky.renderPreWorld(frameBuffer);
			}
			world.renderScene(frameBuffer);
			world.draw(frameBuffer);
			if (sky != null) {
				sky.renderPostWorld(frameBuffer, world);
			}
		}
		Config.glTransparencyOffset = 0.7f;
		Config.glTransparencyMul = 0.06f;
		render2D();
		frameBuffer.update();
		frameBuffer.displayGLOnly();
	}

	public void render2D() {
		gui.drawSprite("cursor", getMouseX() - 4, getMouseY() - 4, true);
		gui.drawString("jPCT " + Config.getVersion(), 15, 15, Color.WHITE);
		gui.drawString("Frame Count: " + frameCount, 15, 30, Color.WHITE);
		gui.drawString("Polygons: " + polyCount, 15, 45, Color.WHITE);
		gui.drawString("Mem Usage: " + memUsage, 15, 60, Color.WHITE);
	}

	private void update(int ticks) {
		if (sky != null) {
			sky.update(world, ticks);
		}
		if (cameraController != null) {
			cameraController.update();
		}
		player.update();
		propManager.update(world, ticks);
		
		if (water != null) {
			water.update(ticks);
		}
	}

	private void pollKeyboard() {
		while (Keyboard.next()) {
			final int keyCode = Keyboard.getEventKey();
			final boolean pressed = Keyboard.getEventKeyState();
			if (keyCode == Keyboard.KEY_ESCAPE) {
				frameBuffer.dispose();
				System.exit(0);
			}
			if (keyCode == Keyboard.KEY_LEFT || keyCode == Keyboard.KEY_RIGHT || keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_DOWN) {
				if (cameraController != null) {
					cameraController.pollKeyboard(keyCode, pressed);
				}
			}
		}
	}

	private void pollMouse() {
		while (Mouse.next()) {
			
			/*
			 * Update the camera.
			 */
			if (cameraController != null) {
				cameraController.pollMouseInput();
			}
			
			/*
			 * Handle mouse button events.
			 */
			if (Mouse.getEventButtonState()) {
				/*
				 * XXX Needs bug-tested. I've only made sure that "click to translate" works properly..
				 */
				switch (Mouse.getEventButton()) {
				case 0: // left mouse button
					float[] tile = picker.pickTile(player, camera);
					player.teleport(new SimpleVector(tile[0], tile[1], tile[2]));
					break;
				case 1: // right mouse button
					Object[] collisions = picker.pickObjects(camera);
					if (collisions == null) {
						System.out.println("pickObjects() == NULL");
						return;
					}
					if (collisions[0] instanceof Player) {
						if ((Player) collisions[0] == player) {
							// Them player shouldn't be able to interact with self...
							System.out.println("CLICKED SELF");
						}
					} else if (collisions[1] instanceof Npc) {
						Npc npc = (Npc) collisions[1];
						npc.handleClick();
					} else {
						System.out.println("NO ENTITIES WERE CLICKED..");
						System.out.println("TODO: teleport to the clicked location!");
					}
					break;
				}
			}
		}
	}

	public int getMouseX() {
		return Mouse.getX();
	}

	public int getMouseY() {
		return (outputHeight - Mouse.getY());
	}

	private void shutdown() {
		System.out.println("Game Engine terminated.");
		frameBuffer.dispose();
		Keyboard.destroy();
		Display.destroy();
	}

}