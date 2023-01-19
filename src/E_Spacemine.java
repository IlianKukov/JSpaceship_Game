

public class E_Spacemine extends E_Entity {
	private final Game game;

	public E_Spacemine(Game game, E_MySprite sprite, int x, int y) {
		super(sprite, x, y);
		this.game = game;
	}


	@Override
	public void collidedWith(E_Entity other) {

	}

}