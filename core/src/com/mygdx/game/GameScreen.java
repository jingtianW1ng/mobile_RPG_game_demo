package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.w3c.dom.Text;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    MyGdxGame game; // Note it’s "MyGdxGame" not "Game"
    // constructor to keep a reference to the main Game class

    public enum GameState { PLAYING, COMPLETE };
    GameState gameState = GameState.PLAYING;
    SpriteBatch batch;
    SpriteBatch uiBatch;
    Stage stage;
    Texture buttonLongTexture;
    Texture buttonLongDownTexture;
    Texture buttonSquareTexture;
    Texture buttonSquareDownTexture;
    Sprite mapSprite;

    OrthographicCamera camera;

    Player player;

    Button mainmenuButton;
    GameButton moveUp;
    GameButton moveDown;
    GameButton moveLeft;
    GameButton moveRight;



    public GameScreen(MyGdxGame game) {
        this.game = game;
    }
    public void create() {
        Gdx.app.log("MenuScreen: ","menuScreen create");

        //create player
        this.player = new Player(this.game);

        mapSprite = new Sprite(new Texture(Gdx.files.internal("Background/B1.png")));
        mapSprite.setPosition(0, 0);
        mapSprite.setSize(100, 100);


        //camera
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();




        //create batch and stage
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();
        stage = new Stage();


        //create button texture
        buttonSquareTexture = new Texture("UI/buttonSquare_blue.png");
        buttonSquareDownTexture = new Texture("UI/buttonSquare_beige_pressed.png");
        buttonLongTexture = new Texture("UI/buttonLong_blue.png");
        buttonLongDownTexture = new Texture("UI/buttonLong_beige_pressed.png");


        //create buttons
        mainmenuButton = new Button ("Main Menu",Gdx.graphics.getWidth()/2-300,Gdx.graphics.getHeight()/2-90, 600, 180, buttonLongTexture, buttonLongDownTexture);
        float buttonSize = h * 0.2f;
        moveLeft = new GameButton(0.0f, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveRight = new GameButton(buttonSize*2, buttonSize, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveDown = new GameButton(buttonSize, 0.0f, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);
        moveUp = new GameButton(buttonSize, buttonSize*2, buttonSize, buttonSize, buttonSquareTexture, buttonSquareDownTexture);




    }
    public void render(float f) {
        this.update();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        //begin batch
        batch.begin();

        stage.getBatch().begin();
        //draw background here
        stage.getBatch().end();

        mapSprite.draw(batch);
        this.player.render(batch);


        batch.end();

        //begin ui batch
        uiBatch.begin();
        switch(gameState) {
            //if gameState is Running: Draw Controls
            case PLAYING:
                moveLeft.draw(uiBatch);
                moveRight.draw(uiBatch);
                moveDown.draw(uiBatch);
                moveUp.draw(uiBatch);
                break;
            //if gameState is Complete: Draw Restart button
            case COMPLETE:
                mainmenuButton.draw(uiBatch);
                break;
        }
        uiBatch.end();
    }
    public void update(){

        //update player
        this.player.update();

        //check input
        boolean checkTouch = Gdx.input.isTouched();
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();
        switch (gameState) {

            case PLAYING:
                moveLeft.update(checkTouch, touchX, touchY);
                moveRight.update(checkTouch, touchX, touchY);
                moveDown.update(checkTouch, touchX, touchY);
                moveUp.update(checkTouch, touchX, touchY);
                break;
            case COMPLETE:
                mainmenuButton.update(checkTouch, touchX, touchY);
                break;

        }

        if(moveLeft.isDown){
            player.moveLeft = true;
        }
        if(moveRight.isDown){
            player.moveRight = true;
        }
        if(moveDown.isDown){
            player.moveDown = true;
        }
        if(moveUp.isDown){
            player.moveUp = true;

        }

        if(mainmenuButton.isDown){
            game.setScreen(MyGdxGame.menuScreen);
        }




    }
    @Override
    public void dispose() {
        batch.dispose();
        this.player.dispose();
        buttonLongTexture.dispose();
        buttonLongDownTexture.dispose();
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
