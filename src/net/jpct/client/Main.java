package net.jpct.client;

import java.io.File;

import com.threed.jpct.Config;
import com.threed.jpct.Logger;

import net.jpct.client.model.DesktopEngine;

public class Main {

	private enum Platform {
		MACOSX, WINDOWS, OTHER;
	}

	private static Platform platform;

	static {
		System.out.println("--> jPCT Configured");
		configure();
	}

	public static void main(String[] args) {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") >= 0) {
			System.setProperty("org.lwjgl.librarypath", new File("./lib/native/windows/").getAbsolutePath());
			platform = Platform.WINDOWS;
		} else if (os.indexOf("mac") >= 0) {
			System.setProperty("org.lwjgl.librarypath", new File("./lib/native/macosx/").getAbsolutePath());
			platform = Platform.MACOSX;
		} else if ((os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0)) {
			System.setProperty("org.lwjgl.librarypath", new File("./lib/native/linux/").getAbsolutePath());
			platform = Platform.OTHER;
		} else if (os.indexOf("sunos") >= 0) {
			System.setProperty("org.lwjgl.librarypath", new File("./lib/native/solaris/").getAbsolutePath());
			platform = Platform.OTHER;
		}
		new DesktopEngine();
	}

	/**
	 * Configure jPCT settings before initializing the game.
	 * http://www.jpct.net/doc/com/threed/jpct/Config.html
	 */
	public static void configure() {
		/*
		 * jPCT debug options:
		 */
		Config.glVerbose = false;
		Logger.setLogLevel(Logger.LL_ONLY_ERRORS);
		Logger.setOnError(Logger.ON_ERROR_THROW_EXCEPTION);

		/*
		 * Color depth:
		 */
		if (platform == Platform.OTHER) {
			Config.glColorDepth = 24;
		} else {
			Config.glColorDepth = 32;
		}

		try {
			// When using the software renderer on a multi core setup, it's a
			// good idea to use all cores (or at least some of them) to speed up
			// rendering.
			// To do so, set Config.useMultipleThreads to true before
			// instantiating the frame buffer.
			// By default, jPCT will use four cores, which matches a quad core
			// cpu. If the setup that runs the application uses less cores, it's
			// advised to adjust this value before instantiating the frame
			// buffer.
			// http://www.jpct.net/wiki/index.php?title=Multithreading#The_threading_framework
			// Config.useMultipleThreads =
			// Runtime.getRuntime().availableProcessors() > 1;

			// Dynamic load balancing usually is the better choice.
			// It's not enable by default simply because it can cause small
			// artifacts in combination with the texel filtering that jPCT uses
			// (some pixels may flicker from time to time).
			Config.loadBalancingStrategy = 1;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// The far clipping plane. jPCT's software renderer doesn't clip but culls on this plane. Default is 1000.
		Config.farPlane = 2048;
		
		// The maximum size of the VisList.
		Config.maxPolysVisible = 16000; // 36500

		// The size in kb of the native buffer cache.
		// Setting this to 0 will turn native buffer caching off.
		// Setting this to a very high value may lead, depending on the
		// application, to a kind of memory leak.
		Config.nativeBufferSize = 512; // Default is 1024.

		// Usually, one have to call build() on all objects that have to be
		// rendered.
		// By setting this to true, jPCT will do this automatically if you omit
		// it.
		// However, this may cause problems with applications that rely (for
		// whatever reason) on unbuild objects.
		// Therefore, it's false by default.
		Config.autoBuild = true;

		// The maximum number in world units a polygon's corner and/or axis
		// aligned bounding box/octree-node (is used) may have from a position
		// vector to be taken into account as a potential collider in the
		// collision detection methods.
		Config.collideOffset = 250;

		// If this is enabled, the ellipsoid collision detection will use a kind
		// of workaround to prevent the detection from producing jerky movement
		// in some cases.
		Config.collideEllipsoidSmoothing = true;

		// When in OpenGL mode, a texture's image data will be transfered to the
		// graphics card and a copy will be kept in the Texture object in case
		// that software rendering will be re-enabled, for applying texture
		// effects or for multiple uploads into multiple contexts.
		Config.glAvoidTextureCopies = true;

		// jPCT can use VertexArrays to render objects.
		Config.glVertexArrays = true;

		// Lets jPCT generate Mipmaps for all textures and enables trilinear
		// filtering between them.
		Config.glTrilinear = true;

		// Makes jPCT use frame buffer objects (FBOs) if possible and
		// applicable.
		// NOTE: This value will be adjusted by jPCT after initializing an
		// OpenGL-renderer to the actually used state.
		Config.glUseFBO = true;

		// If set to true, the OpenGL renderers will ignore the configured near
		// plane for creating the frustum and use a default value instead.
		Config.glIgnoreNearPlane = false;
	}

}
