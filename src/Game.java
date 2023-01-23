import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Game {
	public static final String GAME_TITLE = "Space"; // Name of the game
	private static final int SCREEN_SIZE_WIDTH = 800; // Screen Width
	private static final int SCREEN_SIZE_HEIGHT = 600; // Screen Height
	private static final int FRAMERATE = 60; // Frame rate
	private static final int MAX_LEVEL = 1; // Max Levels count
	private boolean finished; // exit game boolean
	private boolean soundplay = true;
	private boolean pause = false;
	private int extender=3; // exit game boolean
	private final E_Background[] levelTile = new E_Background[MAX_LEVEL];
	private ArrayList<E_Entity> entities;
	private ArrayList<E_Entity> mines;
	private ArrayList<E_Entity> lives;
	private E_Spaceship heroEntity;
	private E_Spacemine spacemineEntity;
	private int currentLevel = 1;
	private TrueTypeFont font;
	private int treasuresCollected = 0;
	private int livesRemaining = 3;
	private Audio wavEffect;

	//Initialize app
	public static void main(String[] args) {
		Game myGame = new Game();
		myGame.start();

	}
	// Error Catcher
	public void start() {
		try {
			init();
			run();


		} catch (Exception e) {
			e.printStackTrace(System.err);
			Sys.alert(GAME_TITLE, "An error occured and the game will exit.");
		} finally {
			cleanup();
		}

		System.exit(0);
	}

	private void init() throws Exception {
		try {
			initGL(SCREEN_SIZE_WIDTH, SCREEN_SIZE_HEIGHT); // Create full screen with exception handler
			initTextures();
		} catch (IOException e) {
			e.printStackTrace();
			finished = true;
		}
	}

	private void initGL(int width, int height) {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle(GAME_TITLE);
			Display.setFullscreen(false);
			Display.create();
			Display.setVSyncEnabled(true); // Vsync if possible
			AL.create(); // Start of sounds
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glEnable(GL11.GL_BLEND); // enable alpha blending
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glViewport(0, 0, width, height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		font = new TrueTypeFont(awtFont, true);
	}

	private void initTextures() throws IOException { // Init Sprites
		entities = new ArrayList<E_Entity>();
		mines = new ArrayList<E_Entity>();
		lives = new ArrayList<E_Entity>();

		Texture texture;

		texture = TextureLoader.getTexture("PNG",
		ResourceLoader.getResourceAsStream("img/space.png")); // Load tile levelTile
		levelTile[0] = new E_Background(texture);

		texture = TextureLoader.getTexture("PNG",
		ResourceLoader.getResourceAsStream("img/spaceship.png")); // Load hero sprite
		heroEntity = new E_Spaceship(this, new E_MySprite(texture), 200, 350);

		texture = TextureLoader.getTexture("PNG",
		ResourceLoader.getResourceAsStream("img/donut.png")); // Load donut sprite
		for (int i = 0; i < 4 ; i++) {
			Random random = new Random();
			int y = random.nextInt(580 - 20) + 20;
			int x = random.nextInt(750 - 50) + 50;
			E_Donut donutEntity = new E_Donut(new E_MySprite(texture),x , y);
			entities.add(donutEntity);
		}

		texture = TextureLoader.getTexture("PNG",
				ResourceLoader.getResourceAsStream("img/mine.png")); // Load spacemine sprite
		for (int i = 0; i < 10 ; i++) {
			Random random = new Random();
			int y = random.nextInt(580 - 20) + 20;
			int x = random.nextInt(10 + 350) - 350;
			E_Spacemine spacemineEntity = new E_Spacemine(this, new E_MySprite(texture),x , y);
			mines.add(spacemineEntity);
		}

		texture = TextureLoader.getTexture("PNG",
		ResourceLoader.getResourceAsStream("img/lives.png")); // Load lives sprite
		E_Lives livesEntity1 = new E_Lives(new E_MySprite(texture),750, 10);
		E_Lives livesEntity2 = new E_Lives(new E_MySprite(texture),700, 10);
		E_Lives livesEntity3 = new E_Lives(new E_MySprite(texture),650, 10);
		lives.add(livesEntity1);
		lives.add(livesEntity2);
		lives.add(livesEntity3);
		sound("sounds/gamestart.wav");

	}



	private void run() { // Initialize game loop
		while (!finished && !pause) {

			Display.update(); // Always call Window.update(), all the time
			if (Display.isCloseRequested()) { // Check for O/S close requests
				finished = true;
			} else if (Display.isActive() && livesRemaining>0) { // The window is in the foreground, so we should play the game
					logic();
					render();

				Display.sync(FRAMERATE);
			} else {
				// The window is not in the foreground, so we can allow other
				// stuff to run and
				// infrequently update
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				logic();
				if (Display.isVisible() || Display.isDirty()) {
					// Only bother rendering if the window is visible or dirty
					render();
				}
			}
		}
	}
	private void cleanup() { // game-specific cleanup
		AL.destroy(); // Stop the sound
		Display.destroy(); // Close game window
	}
	private void logic() { // Calculations, handle input, etc.

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			finished = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {

		}

		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !pause) {
			pause = true;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && pause) {
			pause = false;
		}



		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			if (heroEntity.getX() + heroEntity.getWidth() + 10 < Display
					.getDisplayMode().getWidth()) {
				heroEntity.setX(heroEntity.getX() + 10);
			} else {
				if (currentLevel < MAX_LEVEL) {
					heroEntity.setX(0);
					currentLevel++;
				}
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			if (heroEntity.getX() - 10 >= 0) {
				heroEntity.setX(heroEntity.getX() - 10);
			} else {
				if (currentLevel > 1) {
					currentLevel--;
					heroEntity.setX(Display.getDisplayMode().getWidth()
							- heroEntity.getWidth());
				}
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			if (heroEntity.getY() > 0) {
				heroEntity.setY(heroEntity.getY() - 10);

			}


		}

		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			if (heroEntity.getY() + heroEntity.getHeight() < Display
					.getDisplayMode().getHeight()) {
				heroEntity.setY(heroEntity.getY() + 10);
				sound("sounds/jumpdown.wav");
			}
		}

		//gravity
		final double TIME_STEP=1/60.0;
		final double GRAVITY=9.81;
		int yVelocity=2;
		yVelocity+=GRAVITY*TIME_STEP;
		if (heroEntity.getY() + heroEntity.getHeight() < Display
				.getDisplayMode().getHeight()) {
			heroEntity.setY(heroEntity.getY() + yVelocity);
		}

		//yPosition+=yVelocity*TIME_STEP;



		//Collisions with donuts
		for (int p = 0; p < entities.size(); p++) {
			for (int s = p + 1; s < entities.size(); s++) {
				E_Entity me = entities.get(p);
				E_Entity him = entities.get(s);

				if (me.collidesWith(him)) {
					me.collidedWith(him);
					him.collidedWith(me);
				}
			}
		}

		//Collisions with mines
		for (int p = 0; p < mines.size(); p++) {
			for (int s = p + 1; s < mines.size(); s++) {
				E_Entity me = mines.get(p);
				E_Entity him = mines.get(s);

				if (me.collidesWith(him)) {
					me.collidedWith(him);
					him.collidedWith(me);
				}
			}
		}

		E_Entity me = heroEntity;
		E_Entity him;
		for (int p = 0; p < entities.size(); p++) {
			him = entities.get(p);

			if (me.collidesWith(him)) {
				me.collidedWith(him);
				him.collidedWith(me);
			}
		}

		for (int p = 0; p < mines.size(); p++) {
			him = mines.get(p);

			if (me.collidesWith(him)) {
				me.collidedWith(him);
				him.collidedWith(me);
			}
		}

		if (livesRemaining==0){
			if (extender>0){ // Extends the game 3 more loops to be able to draw the lost lives
				extender--;
			}
		}


		for(int i = 0; i < mines.size(); i++)
		{
			if (mines.get(i).getX() + mines.get(i).getHeight() < Display.getDisplayMode().getWidth() + 50) {
				mines.get(i).setX(mines.get(i).getX() + 1);

			} else {
				mines.remove(mines.get(i));
			}
		}
		//generate new mines when they vanish
		Texture texture;
		if (mines.size()<6){

			try {
				texture = TextureLoader.getTexture("PNG",
						ResourceLoader.getResourceAsStream("img/mine.png")); // Load spacemine sprite
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			for (int i = 0; i < 10 ; i++) {
				Random random = new Random();
				int y = random.nextInt(550 - 50) + 50;
				int x = random.nextInt(10 + 250) - 350;
				E_Spacemine spacemineEntity = new E_Spacemine(this, new E_MySprite(texture),x , y);
				mines.add(spacemineEntity);
			}

		}

		//generate new donuts
		if (entities.size()<3){
			try {
				texture = TextureLoader.getTexture("PNG",
						ResourceLoader.getResourceAsStream("img/donut.png")); // Load donut sprite
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			for (int i = 0; i < 4 ; i++) {
			Random random = new Random();
			int y = random.nextInt(580 - 20) + 20;
			int x = random.nextInt(750 - 50) + 50;
			E_Donut donutEntity = new E_Donut(new E_MySprite(texture), x, y);
			entities.add(donutEntity);
			}
			sound("sounds/donut.wav");


		}

	}

	private void render() { //Render the current frame
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		Color.white.bind();

		E_Background currentLevelTile;
		currentLevelTile = levelTile[currentLevel - 1];
		currentLevelTile.getTexture().bind();
		for (int a = 0; a * currentLevelTile.getHeight() < SCREEN_SIZE_HEIGHT; a++) {
			for (int b = 0; b * currentLevelTile.getWidth() < SCREEN_SIZE_WIDTH; b++) {
				int textureX = currentLevelTile.getWidth() * b;
				int textureY = currentLevelTile.getHeight() * a;
				currentLevelTile.draw(textureX, textureY);
			}
		}
		if (livesRemaining>0) {
			if (entities != null) {
				for (E_Entity entity : entities) {
					if (entity.isVisible()) {
						entity.draw();
					}
				}
			}

			if (mines != null) {
				for (E_Entity entity : mines) {
					if (entity.isVisible()) {
						entity.draw();
					}
				}
			}

			if (livesRemaining==3){
				lives.get(0).draw();
				lives.get(1).draw();
				lives.get(2).draw();
			}else if (livesRemaining==2){
				lives.get(0).draw();
				lives.get(1).draw();
			}else if (livesRemaining==1){
				lives.get(0).draw();
			}

			heroEntity.draw();
			font.drawString(10, 10, "Score " + treasuresCollected, Color.white);
			//font.drawString(SCREEN_SIZE_WIDTH-335, 10, "Lives remaining " + livesRemaining, Color.white);
		}else {
			if (soundplay){
				sound("sounds/gameover.wav");
				soundplay=false;
			}

			String st = "Game over! You Score is " + treasuresCollected ;
			font.drawString(230, 250, st, Color.white);

		}



	}
	private void sound(String sound) {
		try
		{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(sound)));
			if (livesRemaining>=0) {
				clip.start();
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace(System.out);
		}
	}

	public void notifyObjectCollision(E_Entity notifier, Object object) {
		if (object instanceof E_Donut donutEntity && livesRemaining>0) {
			donutEntity.setVisible(false);
			entities.remove(donutEntity);
			treasuresCollected = treasuresCollected + 100;
			if (treasuresCollected%1000==0 && livesRemaining<3){
				livesRemaining++;
				sound("sounds/addlife.wav");
			}
			sound("sounds/collect.wav");
		}else if (object instanceof E_Spacemine spacemineEntity) {
			spacemineEntity.setVisible(false);
			mines.remove(spacemineEntity);
			livesRemaining--;
			sound("sounds/enemyhit.wav");

		}

	}
}
