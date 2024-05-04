package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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

public class MenuScreen implements Screen{

    MyGdxGame game; // Note it’s "MyGdxGame" not "Game"
    // constructor to keep a reference to the main Game class

    SpriteBatch batch;
    SpriteBatch uiBatch;
    Stage stage;
    Texture buttonLongTexture;
    Texture buttonLongDownTexture;

    Button playButton;
    Button exitButton;

    BitmapFont font;

    Music backgroundMusic;



    public MenuScreen(MyGdxGame game) {
        this.game = game;


    }
    public void create() {
        Gdx.app.log("MenuScreen: ","menuScreen create");
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();
        stage = new Stage();

        font = new BitmapFont();
        font.getData().setScale(6,6);

        buttonLongTexture = new Texture("UI/buttonLong_blue.png");
        buttonLongDownTexture = new Texture("UI/buttonLong_beige_pressed.png");

        playButton = new Button ("Play",150,400, 600, 180, buttonLongTexture, buttonLongDownTexture);
        exitButton = new Button ("Exit",150,400 - buttonLongTexture.getHeight()*6 , 600, 180, buttonLongTexture, buttonLongDownTexture);

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("fantacyBackgroundMusic.wav"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();


    }
    public void render(float f) {
        this.update();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        font.draw(batch, "ASSIGNMENT 2", Gdx.graphics.getWidth()/2-350, Gdx.graphics.getHeight()/2+350);

        stage.getBatch().begin();
        //draw background here
        stage.getBatch().end();

        batch.end();

        uiBatch.begin();
        playButton.draw(uiBatch);
        exitButton.draw(uiBatch);
        uiBatch.end();
    }
    public void update(){
        boolean checkTouch = Gdx.input.isTouched();
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();

        playButton.update(checkTouch, touchX, touchY);
        exitButton.update(checkTouch, touchX, touchY);

        if(playButton.isDown){
            backgroundMusic.stop();
            game.setScreen(MyGdxGame.gameScreen);
        }



        if(exitButton.isDown){
            System.exit(0);
        }


    }
    @Override
    public void dispose() {
        batch.dispose();
        buttonLongTexture.dispose();
        buttonLongDownTexture.dispose();
        backgroundMusic.dispose();
    }
    @Override
    public void resize(int width, int height) { }
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
