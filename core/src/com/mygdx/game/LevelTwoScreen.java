package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class LevelTwoScreen implements Screen {

    MyGdxGame game; // Note it’s "MyGdxGame" not "Game"

    // constructor to keep a reference to the main Game class

    public enum GameState { PLAYING, PAUSED, COMPLETE, FAILED };

    GameState gameState = GameState.PLAYING;

    //speed
    public static final float MOVEMENT_SPEED = 100.0f;

    //Map and rendering
    SpriteBatch spriteBatch;
    SpriteBatch uiBatch; //Second SpriteBatch without camera transforms, for drawing UI
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    OrthographicCamera camera;

    //Player Character
    Player player;
    float dt = Gdx.graphics.getDeltaTime();

    //UI textures
    Texture buttonSquareTexture;
    Texture buttonSquareDownTexture;
    Texture buttonLongTexture;
    Texture buttonLongDownTexture;

    // pause button textures
    Texture pauseButtonTexture;
    Texture pauseButtonPressedTexture;
    Texture menuButtonTexture;
    Texture menuButtonPressedTexture;

    //UI Buttons
    Button moveLeftButton;
    Button moveRightButton;
    Button moveDownButton;
    Button moveUpButton;
    Button restartButton;
    //Just use this to only restart when the restart button is released instead of immediately as it's pressed

    Button pauseButton;
    Button resumeButton;
    Button exitToMainMenuButton;
    Button lvlTwoButton;
    boolean isResumeButtonDown = false;
    boolean isExitButtonDown = false;
    boolean isPauseButtonDown = false;
    boolean isLvlTwoButtonDown = false;

    //Sound
    Sound buttonClickSound;

    //Gate
    Texture gateTexture;
    Sprite gateSprite;
    Vector2 gatePosition;

    //Door animation
    DoorAnimation doorAnimation = new DoorAnimation();
    boolean doorOpened = false;

    //Storage class for collision
    Rectangle tileRectangle;

    boolean restartActive;

    public LevelTwoScreen(MyGdxGame game) {this.game = game;}
    public void create() {
        Gdx.app.log("LevelTwoScreen: ","levelTwoScreen create");

        //Rendering
        spriteBatch = new SpriteBatch();
        uiBatch = new SpriteBatch();

        //TODO Initiate the TiledMap and its renderer
        tiledMap = new TmxMapLoader().load("Background/levelTwoMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        //Camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w / 4, h / 4);

        //player
        player = new Player(this.game);

        //Texture
        buttonSquareTexture = new Texture("UI/buttonSquare_blue.png");
        buttonSquareDownTexture = new Texture("UI/buttonSquare_beige_pressed.png");
        buttonLongTexture = new Texture("UI/buttonLong_blue.png");
        buttonLongDownTexture = new Texture("UI/buttonLong_beige_pressed.png");

        pauseButtonTexture = new Texture("UI/new_ui/pause_button.png");
        pauseButtonPressedTexture = new Texture("UI/new_ui/pause_button_press.png");

        menuButtonTexture = new Texture("UI/new_ui/menu_button.png");
        menuButtonPressedTexture = new Texture("UI/new_ui/menu_button_press.png");

        //Gate
        gateTexture = new Texture(("Background/tiles/wall/door_closed.png"));
        gateSprite = new Sprite(gateTexture);
        gatePosition = new Vector2(144, 192);

        //Buttons
        float buttonSize = h * 0.2f;
        moveLeftButton = new Button("",0.0f, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRightButton = new Button("", buttonSize*2, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveDownButton = new Button("", buttonSize, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveUpButton = new Button("", buttonSize, buttonSize*2, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        restartButton = new Button("", w/2 - buttonSize*2, h * 0.2f, buttonSize*4, buttonSize, buttonLongTexture, buttonLongDownTexture);

        pauseButton = new Button("", 10, Gdx.graphics.getHeight() - pauseButtonTexture.getHeight() - 250, buttonSize, buttonSize,pauseButtonTexture,pauseButtonPressedTexture);
        resumeButton = new Button("      Resume", 700, 500, 1000, 180, menuButtonTexture, menuButtonPressedTexture);
        exitToMainMenuButton = new Button("Exit to Main Menu", 700, 300, 1000, 180, menuButtonTexture, menuButtonPressedTexture);

        lvlTwoButton = new Button("Let's start the advanture", 500,100,1200,180, menuButtonTexture, menuButtonPressedTexture);

        //Sound
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("clickSound.wav"));

        //Collision
        tileRectangle = new Rectangle();
        MapLayer collisionLayer = tiledMap.getLayers().get("Collision");
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;
        tileRectangle.width = tileLayer.getTileWidth();
        tileRectangle.height = tileLayer.getTileHeight();

        newGame();
    }

    private void newGame() {
        gameState = GameState.PLAYING;

        //Translate camera to center of screen
        camera.position.x = 30; //The 16 is half the size of a tile
        camera.position.y = 30;

        //Player start location, you can have this stored in the tilemaze using an object layer.
        player.characterX = 40;
        player.characterY = 630;

        camera.translate(player.characterX, player.characterY);
        restartActive = false;
    }

    public void render(float f) {
        //set dt
        dt = Gdx.graphics.getDeltaTime();

        //Update the Game State
        update();

        //Clear the screen before drawing.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //Allows transparent sprites/tiles
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        //TODO Render Map Here
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        //update the door animation
        doorAnimation.update(f);

        //Draw Character
        //Apply the camera's transform to the SpriteBatch so the character is drawn in the correct
        //position on screen.
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        //TODO player draw
        player.render(spriteBatch);
        spriteBatch.end();

        // Draw the gate
        if (!doorOpened) {
            spriteBatch.begin();
            gateSprite.setPosition(gatePosition.x, gatePosition.y);
            gateSprite.draw(spriteBatch);
            spriteBatch.end();
        }

        //Draw the door opening animation
        TextureRegion currentFrame = doorAnimation.getCurrentFrame();
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, gatePosition.x, gatePosition.y);
        spriteBatch.end();

        //Draw UI
        uiBatch.begin();
        switch(gameState) {
            //if gameState is Running: Draw Controls
            case PLAYING:
                moveLeftButton.draw(uiBatch);
                moveRightButton.draw(uiBatch);
                moveDownButton.draw(uiBatch);
                moveUpButton.draw(uiBatch);
                pauseButton.draw(uiBatch);
                break;
            //if gameState is Paused: Draw buttons
            case PAUSED:
                resumeButton.draw(uiBatch);
                exitToMainMenuButton.draw(uiBatch);
                break;
            //if gameState is Complete: Draw Restart button
            case FAILED:
                restartButton.draw(uiBatch);
                break;
            case COMPLETE:
                lvlTwoButton.draw(uiBatch);
                break;
        }
        uiBatch.end();
    }
    public void update(){
        //player update
        player.update();
        //Touch Input Info
        boolean checkTouch = Gdx.input.isTouched();
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();

        //Update Game State based on input
        switch (gameState) {

            case PLAYING:
                //Poll user for input
                moveLeftButton.update(checkTouch, touchX, touchY);
                moveRightButton.update(checkTouch, touchX, touchY);
                moveDownButton.update(checkTouch, touchX, touchY);
                moveUpButton.update(checkTouch, touchX, touchY);
                pauseButton.update(checkTouch,touchX,touchY);

                float moveX = 0;
                float moveY = 0;
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || moveLeftButton.isDown) {
                    moveLeftButton.isDown = true;
                    moveX -= 1f;
                    player.setState(Player.PlayerState.walkLeft);
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || moveRightButton.isDown) {
                    moveRightButton.isDown = true;
                    moveX += 1f;
                    player.setState(Player.PlayerState.walkRight);
                } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || moveDownButton.isDown) {
                    moveDownButton.isDown = true;
                    moveY -= 1f;
                    if(player.state == Player.PlayerState.walkLeft ||player.state == Player.PlayerState.idleLeft){
                        player.setState(Player.PlayerState.walkLeft);
                    }else {player.setState(Player.PlayerState.walkRight);}

                } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || moveUpButton.isDown) {
                    moveUpButton.isDown = true;
                    moveY += 1f;
                    if(player.state == Player.PlayerState.walkLeft ||player.state == Player.PlayerState.idleLeft){
                        player.setState(Player.PlayerState.walkLeft);
                    } else {player.setState(Player.PlayerState.walkRight);}
                } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || pauseButton.isDown) {
                    gameState = GameState.PAUSED;
                    isPauseButtonDown = true;
                    buttonClickSound.play();
                }


                player.playerDelta.x = moveX * MOVEMENT_SPEED * dt;
                Gdx.app.log("sb: ","delta x: " + player.playerDelta.x);
                player.playerDelta.y = moveY * MOVEMENT_SPEED * dt;

                if (player.playerDelta.len2() > 0) { //Don't do anything if we're not moving
                    //Retrieve Collision layer
                    MapLayer collisionLayer = tiledMap.getLayers().get("Collision");
                    TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;

                    //TODO Determine bounds to check within
                    // Find top-right corner tile
                    int right = (int) Math.ceil(Math.max(player.characterX + player.playerSprite.getWidth(),player.characterX + player.playerSprite.getWidth() + player.playerDelta.x));
                    int top = (int) Math.ceil(Math.max(player.characterY + player.playerSprite.getHeight(),player.characterY + player.playerSprite.getHeight() + player.playerDelta.y));

                    // Find bottom-left corner tile
                    int left = (int) Math.floor(Math.min(player.characterX,player.characterX + player.playerDelta.x));
                    int bottom = (int) Math.floor(Math.min(player.characterY,player.characterY + player.playerDelta.y));

                    // Divide bounds by tile sizes to retrieve tile indices
                    right /= tileLayer.getTileWidth();
                    top /= tileLayer.getTileHeight();
                    left /= tileLayer.getTileWidth();
                    bottom /= tileLayer.getTileHeight();

                    //TODO Loop through selected tiles and correct by each axis
                    //EXTRA: Try counting down if moving left or down instead of counting up
                    for (int y = bottom; y <= top; y++) {
                        for (int x = left; x <= right; x++) {
                            TiledMapTileLayer.Cell targetCell = tileLayer.getCell(x, y);
                            // If the cell is empty, ignore it
                            if (targetCell == null) continue;
                            // Otherwise correct against tested squares
                            tileRectangle.x = x * tileLayer.getTileWidth();
                            tileRectangle.y = y * tileLayer.getTileHeight();

                            player.playerDeltaRectangle.x = player.characterX + player.playerDelta.x;
                            player.playerDeltaRectangle.y = player.characterY;
                            if (tileRectangle.overlaps(player.playerDeltaRectangle)) player.playerDelta.x = 0;

                            player.playerDeltaRectangle.x = player.characterX;
                            player.playerDeltaRectangle.y = player.characterY + player.playerDelta.y;
                            if (tileRectangle.overlaps(player.playerDeltaRectangle)) player.playerDelta.y = 0;

                        }
                    }

                    //TODO Move player and camera
                    player.playerSprite.translate(player.playerDelta.x, player.playerDelta.y);
                    player.characterX += player.playerDelta.x;
                    player.characterY += player.playerDelta.y;
                    camera.translate(player.playerDelta);
                }

                //TODO Check if player has met the winning condition
                if (!doorOpened && player.getBoundingBox().overlaps(gateSprite.getBoundingRectangle())) {
                    doorAnimation = new DoorAnimation();
                    doorOpened = true;
                    gameState = GameState.COMPLETE;

                }

            case PAUSED:
                resumeButton.update(checkTouch, touchX, touchY);
                if (Gdx.input.isKeyPressed(Input.Keys.UP) || resumeButton.isDown) {
                    gameState = GameState.PLAYING;
                    isResumeButtonDown = true;
                    buttonClickSound.play();
                }

                exitToMainMenuButton.update(checkTouch, touchX, touchY);
                if (Gdx.input.isKeyPressed(Input.Keys.UP) || exitToMainMenuButton.isDown) {
                    buttonClickSound.play();
                    game.setScreen(MyGdxGame.menuScreen); // Return to the main menu
                }
                break;

            case FAILED:
                //Poll for input
                restartButton.update(checkTouch, touchX, touchY);

                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_CENTER) || restartButton.isDown) {
                    restartButton.isDown = true;
                    restartActive = true;
                } else if (restartActive) {
                    doorOpened = false;
                    newGame();
                }
                break;

            case COMPLETE:
                lvlTwoButton.update(checkTouch, touchX, touchY);
                if (Gdx.input.isKeyPressed(Input.Keys.UP) || lvlTwoButton.isDown) {
                    isLvlTwoButtonDown = true;
                    doorOpened = false;
                    game.setScreen(MyGdxGame.levelTwoScreen);
                }
                break;
        }

    }

    @Override
    public void dispose() {
        tiledMap.dispose();
        buttonSquareTexture.dispose();
        buttonSquareDownTexture.dispose();
        buttonLongTexture.dispose();
        buttonLongDownTexture.dispose();
        menuButtonPressedTexture.dispose();
        menuButtonTexture.dispose();
        pauseButtonTexture.dispose();
        pauseButtonPressedTexture.dispose();
        gateTexture.dispose();
    }
    @Override
    public void resize(int width, int height) {

    }
    @Override
    public void pause() { }
    @Override
    public void resume() { }
    @Override
    public void show() {
        Gdx.app.log("MenuScreen: ","menuScreen show called");
        create();
    }
    @Override
    public void hide() {
        Gdx.app.log("MenuScreen: ","menuScreen hide called");
    }
}