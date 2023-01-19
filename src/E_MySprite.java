import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

public class E_MySprite {
	private int width;
	private int height;
	private Texture texture;

	public E_MySprite(Texture texture) {
		this.width = texture.getImageWidth();
		this.height = texture.getImageHeight();
		this.texture = texture;
	}

	public void draw(int x, int y) {
		GL11.glPushMatrix(); // store the current model matrix
		texture.bind(); // bind to the appropriate texture for this sprite
		GL11.glTranslatef(x, y, 0); // translate to the right location and prepare to draw
		GL11.glBegin(GL11.GL_QUADS); // draw a quad textured to match the sprite
		glTexCoord2f(0, 0);
		glVertex2f(0, 0);
		glTexCoord2f(0, texture.getHeight());
		glVertex2f(0, height);
		glTexCoord2f(texture.getWidth(), texture.getHeight());
		glVertex2f(width, height);
		glTexCoord2f(texture.getWidth(), 0);
		glVertex2f(width, 0);
		GL11.glEnd();
		GL11.glPopMatrix();// restore the model view matrix to prevent contamination
	}

	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public Texture getTexture() {
		return texture;
	}
	public void setTexture(Texture texture) {
		this.texture = texture;
	}

}
