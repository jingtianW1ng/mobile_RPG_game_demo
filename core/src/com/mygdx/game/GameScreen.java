package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Vector;

public class GameScreen implements Screen {

    MyGdxGame game; // Note it’s "MyGdxGame" not "Game"
    // constructor to keep a reference to the main Game class

    public enum GameState { PLAYING, PAUSED, COMPLETE };

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

    //Enemies
    Flying flying;
    Goblin goblin;
    Slime slime;
    Boss boss;
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
    boolean isResumeButtonDown = false;
    boolean isExitButtonDown = false;
    boolean isPauseButtonDown = false;

    //Sound
    Sound buttonClickSound;

    boolean restartActive;

    //item
    Items redPotion;
    Items greenPotion;

    Texture bosstex;
    Texture goblinTex;
    public GameScreen(MyGdxGame game) {this.game = game;}
    public void create() {

        //Rendering
        spriteBatch = new SpriteBatch();
        uiBatch = new SpriteBatch();

        //TODO Initiate the TiledMap and its renderer
        tiledMap = new TmxMapLoader().load("Background/testMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        //Camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w / 9, h / 9);

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

        //Sound
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("clickSound.wav"));

        //items
        redPotion = new Items(100,90,7,11,"Item/props_itens/potion_red.png");
        greenPotion = new Items(150,90,7,11,"Item/props_itens/potion_green.png");

        //Enemies
        flying = new Flying();
        goblin = new Goblin();
        slime = new Slime();
        boss = new Boss();
        newGame();
    }

    private void newGame() {
        gameState = GameState.PLAYING;

        //Translate camera to center of screen
        camera.position.x = 30; //The 16 is half the size of a tile
        camera.position.y = 30;

        //Player start location, you can have this stored in the tilemaze using an object layer.
        player.characterX = 120;
        player.characterY = 120;

        //enemies start location
        flying.x = 140;
        flying.y = 120;
        goblin.x = 14000;
        goblin.y = 140;
        slime.x = 0;
        slime.y = 0;
        boss.x = 140;
        boss.y = 100;

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

        //Draw Character
        //Apply the camera's transform to the SpriteBatch so the character is drawn in the correct
        //position on screen.
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        //TODO player draw
        player.render(spriteBatch);

        //render enemies
        flying.render(spriteBatch);
        goblin.render(spriteBatch);
        slime.render(spriteBatch);
        boss.render(spriteBatch);

        //items
        redPotion.render(spriteBatch);
        greenPotion.render(spriteBatch);

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
            case COMPLETE:
                restartButton.draw(uiBatch);
                break;
        }
        uiBatch.end();
    }
    public void update(){
        //player update
        player.update();
        //enemies update
        flying.update(this.player);
        goblin.update(this.player);
        slime.update(this.player);
        boss.update(this.player);
        //items update
        redPotion.update();
        greenPotion.update();

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

                    //TODO Loop through selected tiles and correct by each axis
                    //EXTRA: Try counting down if moving left or down instead of counting up

                    //TODO Move player and camera
                    //playerSprite.translate(playerDelta.x, playerDelta.y);
                    player.characterX += player.playerDelta.x;
                    player.characterY += player.playerDelta.y;
                    camera.translate(player.playerDelta);
                }


                //if player hit items
                if(player.getBoundingBox().overlaps(redPotion.getBoundingBox())){
                    if(player.playerHealth<4){
                        player.playerHealth+=1;
                        redPotion.pickUp = true;
                    }
                }
                if(player.getBoundingBox().overlaps(greenPotion.getBoundingBox())){
                    if(player.playerHealth>0){
                        player.playerHealth-=1;
                        greenPotion.pickUp = true;
                    }
                }



                //TODO Check if player has met the winning condition

                break;

            case PAUSED:
                resumeButton.update(checkTouch, touchX, touchY);
                if (Gdx.input.isKeyPressed(Input.Keys.UP) || resumeButton.isDown) {
                    gameState = GameState.PLAYING;
                    isResumeButtonDown = true;
                    buttonClickSound.play();
                }

                exitToMainMenuButton.update(checkTouch, touchX, touchY);
                if (Gdx.input.isKeyPressed(Input.Keys.UP) || exitToMainMenuButton.isDown) {
                    game.setScreen(MyGdxGame.menuScreen); // Return to the main menu
                    buttonClickSound.play();
                }
                break;

            case COMPLETE:
                //Poll for input
                restartButton.update(checkTouch, touchX, touchY);

                if (Gdx.input.isKeyPressed(Input.Keys.DPAD_CENTER) || restartButton.isDown) {
                    restartButton.isDown = true;
                    restartActive = true;
                } else if (restartActive) {
                    newGame();
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
