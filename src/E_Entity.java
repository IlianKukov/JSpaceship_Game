import java.awt.*;
public abstract class E_Entity {
	private int x;
	private int y;
	private final E_MySprite sprite;
	private boolean visible;

	private final Rectangle me = new Rectangle();
	private final Rectangle him = new Rectangle();

	public E_Entity(E_MySprite sprite, int x, int y) {
		this.x = x;
		this.y = y;
		this.sprite = sprite;
		this.visible = true;
	}

	public boolean collidesWith(E_Entity other) {
		me.setBounds(x, y, sprite.getWidth(), sprite.getHeight());
		him.setBounds(other.x, other.y, other.sprite.getWidth(),
				other.sprite.getHeight());

		return me.intersects(him);
	}

	public abstract void collidedWith(E_Entity other);

	public void draw() {
		sprite.draw(x, y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return sprite.getWidth();
	}

	public int getHeight() {
		return sprite.getHeight();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
