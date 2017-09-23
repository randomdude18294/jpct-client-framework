package net.jpct.client.model;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.threed.jpct.FrameBuffer;

import net.jpct.client.util.gui.GLFont;
import net.jpct.client.util.gui.TexturePack;

/**
 * Gui Helper is designed to simplify the use of
 * TexturePack & GLFont feature implementations.
 */
public class GuiHelper {
	
	private final FrameBuffer frameBuffer;
	private final TexturePack texturePack;
	private final GLFont font;
	private Map<String, Integer> textureCache = new HashMap<String, Integer>();
	
	public GuiHelper(final FrameBuffer frameBuffer) {
		this.frameBuffer = frameBuffer;
		this.texturePack = new TexturePack();
		this.font = GLFont.getGLFont(new Font("Arial", Font.PLAIN, 14));
	}

	public void addTexture(String name, File file) {
		try {
			textureCache.put(name, texturePack.addImage(ImageIO.read(file)));
			texturePack.pack(true);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void drawSprite(String name, int x, int y, boolean alpha) {
		texturePack.blit(frameBuffer, textureCache.get(name), x, y, alpha);
	}
	
	public void drawString(String string, int x, int y, Color color) {
		font.blitString(frameBuffer, string, x, y, 0, color);
	}

}
