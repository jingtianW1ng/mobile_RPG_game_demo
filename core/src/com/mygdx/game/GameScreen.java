package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;


public class GameScreen implements Screen {

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

    //Enemies
    Array<Flying> flyings = new Array<>();
    Array<Goblin> goblins = new Array<>();
    Array<Slime> slimes = new Array<>();
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

    //player attack button
    Button attackButton;
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
        tiledMap = new TmxMapLoader().load("Background/levelOne.tmx");
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

        //player attack button
        float screenWidth = Gdx.graphics.getWidth() - buttonSize;
        attackButton = new Button("",screenWidth, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);

        pauseButton = new Button("", 10, Gdx.graphics.getHeight() - pauseButtonTexture.getHeight() - 250, buttonSize, buttonSize,pauseButtonTexture,pauseButtonPressedTexture);
        resumeButton = new Button("      Resume", 700, 500, 1000, 180, menuButtonTexture, menuButtonPressedTexture);
        exitToMainMenuButton = new Button("Exit to Main Menu", 700, 300, 1000, 180, menuButtonTexture, menuButtonPressedTexture);

        lvlTwoButton = new Button("Let's start the adventure", 600,100,1200,180, menuButtonTexture, menuButtonPressedTexture);


        //Sound
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("clickSound.wav"));

        //Collision
        tileRectangle = new Rectangle();
        MapLayer collisionLayer = tiledMap.getLayers().get("Collision");
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;
        tileRectangle.width = tileLayer.getTileWidth();
        tileRectangle.height = tileLayer.getTileHeight();

        //items
        redPotion = new Items(100,90,7,11,"Item/props_itens/potion_red.png");
        greenPotion = new Items(150,90,7,11,"Item/props_itens/potion_green.png");

        //每个level的要创建的enemies在这里
        //flyings
        spawnFlying(250,90);

        //goblins
        spawnGoblin(1900, 110);

        //slimes
        spawnSlime(250,90);

        //boss只有一个不用多个生成
        boss = new Boss();
        newGame();
    }

    private void spawnFlying(float x, float y)
    {
        Flying newFlying = new Flying(x,y);
        flyings.add(newFlying);
    }

    private void spawnGoblin(float x, float y)
    {
        Goblin newGoblin = new Goblin(x,y);
        goblins.add(newGoblin);
    }
    private void spawnSlime(float x, float y)
    {
        Slime newSlime = new Slime(x,y);
        slimes.add(newSlime);
    }


    private void newGame() {
        gameState = GameState.PLAYING;

        //Translate camera to center of screen
        camera.position.x = 30; //The 16 is half the size of a tile
        camera.position.y = 30;

        //Player start location, you can have this stored in the tilemaze using an object layer.
        player.characterX = 120;
        player.characterY = 120;

        //boss location
        boss.x = 200;
        boss.y = 140;

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

        //render each loop enemies
        for(int i = 0; i < flyings.size; i++)
        {
            flyings.get(i).render(spriteBatch);
        }
        for(int i = 0; i < goblins.size; i++)
        {
            goblins.get(i).render(spriteBatch);
        }
        for(int i = 0; i < slimes.size; i++)
        {
            slimes.get(i).render(spriteBatch);
        }
        boss.render(spriteBatch);

        //items
        redPotion.render(spriteBatch);
        greenPotion.render(spriteBatch);

        spriteBatch.end();


        // Draw the gate
        if (!doorOpened) {
            spriteBatch.begin();
            gateSprite.setPosition(gatePosition.x, gatePosition.y);
            gateSprite.draw(spriteBatch);
            spriteBatch.end();
        }

        //Draw the door opening animation
        if (doorOpened) {
            TextureRegion currentFrame = doorAnimation.getCurrentFrame();
            spriteBatch.begin();
            spriteBatch.draw(currentFrame, gatePosition.x, gatePosition.y);
            spriteBatch.end();
        }


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

                //player attack
                attackButton.draw(uiBatch);

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
        player.update(flyings, goblins, slimes, boss);

        //enemies update
        for(int i = 0; i < flyings.size; i++)
        {
            flyings.get(i).update(this.player);
        }
        for(int i = 0; i < goblins.size; i++)
        {
            goblins.get(i).update(this.player);
        }
        for(int i = 0; i < slimes.size; i++)
        {
            slimes.get(i).update(this.player);
        }

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
                attackButton.update(checkTouch,touchX,touchY);

                float moveX = 0;
                float moveY = 0;
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || moveLeftButton.isDown) {
                    moveLeftButton.isDown = true;
                    player.isRight = false;
                    moveX -= 1f;
                    player.setState(Player.PlayerState.walkLeft);
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || moveRightButton.isDown) {
                    moveRightButton.isDown = true;
                    player.isRight = true;
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

                if(attackButton.isDown)
                {
                    player.attack();
                }

                player.playerDelta.x = moveX * MOVEMENT_SPEED * dt;
                Gdx.app.log("sb: ","delta x: " + player.playerDelta.x);
                player.playerDelta.y = moveY * MOVEMENT_SPEED * dt;

                if (player.playerDelta.len2() > 0) { //Don't do anything if we're not moving
                    //Retrieve Collision layer
                    MapLayer collisionLayer = tiledMap.getLayers().get("Collision");
                    TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;

                    player.collisionCheck(tileRectangle, tileLayer);

                    //TODO Move player and camera
                    player.playerSprite.translate(player.playerDelta.x, player.playerDelta.y);
                    player.characterX += player.playerDelta.x;
                    player.characterY += player.playerDelta.y;
                    camera.translate(player.playerDelta);
                }
                if(flyings.size != 0 && goblins.size != 0 && slimes.size != 0 && boss.bossHealth > 0)
                {
                    //Retrieve Collision layer
                    MapLayer collisionLayer = tiledMap.getLayers().get("Collision");
                    TiledMapTileLayer tileLayer = (TiledMapTileLayer) collisionLayer;

                    for(int i = 0; i < flyings.size; i++)
                    {
                        flyings.get(i).collisionCheckLeft(tileRectangle, tileLayer);
                        flyings.get(i).collisionCheckRight(tileRectangle, tileLayer);
                        flyings.get(i).collisionCheckBottom(tileRectangle, tileLayer);
                        flyings.get(i).collisionCheckTop(tileRectangle, tileLayer);
                        for(int j = 0; j < flyings.get(i).missiles.size; j++)
                        {
                            flyings.get(i).missileCollisionCheck(tileRectangle, tileLayer, flyings.get(i).missiles.get(j));
                        }
                    }
                    for(int i = 0; i < goblins.size; i++)
                    {
                        goblins.get(i).collisionCheckLeft(tileRectangle, tileLayer);
                        goblins.get(i).collisionCheckRight(tileRectangle, tileLayer);
                        goblins.get(i).collisionCheckBottom(tileRectangle, tileLayer);
                        goblins.get(i).collisionCheckTop(tileRectangle, tileLayer);
                    }
                    for(int i = 0; i < slimes.size; i++)
                    {
                        slimes.get(i).collisionCheckLeft(tileRectangle, tileLayer);
                        slimes.get(i).collisionCheckRight(tileRectangle, tileLayer);
                        slimes.get(i).collisionCheckBottom(tileRectangle, tileLayer);
                        slimes.get(i).collisionCheckTop(tileRectangle, tileLayer);
                    }

                    boss.collisionCheckLeft(tileRectangle, tileLayer);
                    boss.collisionCheckRight(tileRectangle, tileLayer);
                    boss.collisionCheckBottom(tileRectangle, tileLayer);
                    boss.collisionCheckTop(tileRectangle, tileLayer);
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
                if (!doorOpened && player.getBoundingBox().overlaps(gateSprite.getBoundingRectangle())) {
                    doorAnimation = new DoorAnimation();
                    doorOpened = true;
                    gameState = GameState.COMPLETE;

                }
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
