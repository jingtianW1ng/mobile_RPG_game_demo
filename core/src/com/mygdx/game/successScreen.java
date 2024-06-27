package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class successScreen implements Screen{

    MyGdxGame game; // Note it’s "MyGdxGame" not "Game"
    // constructor to keep a reference to the main Game class

    SpriteBatch batch;
    SpriteBatch uiBatch;
    Stage stage;
    Texture buttonLongTexture;
    Texture buttonLongDownTexture;

    Texture background;

    Button mainmenuButton;

    BitmapFont font;

    //Music backgroundMusic;



    public successScreen(MyGdxGame game) {
        this.game = game;


    }
    public void create() {
        Gdx.app.log("successScreen: ","successscreen create");
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();
        stage = new Stage();

        font = new BitmapFont();
        font.getData().setScale(6,6);
        background = new Texture("Background/sucBackground.png");

        buttonLongTexture = new Texture("UI/buttonLong_blue.png");
        buttonLongDownTexture = new Texture("UI/buttonLong_beige_pressed.png");

        mainmenuButton = new Button ("Bcak to Main Menu",600,400, 1050, 180, buttonLongTexture, buttonLongDownTexture);


        //backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("fantacyBackgroundMusic.wav"));
        //backgroundMusic.setLooping(true);
        //backgroundMusic.play();
        //


    }
    public void render(float f) {
        this.update();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        font.draw(batch, "Congratulations You Completed Demo", Gdx.graphics.getWidth()/2-600, Gdx.graphics.getHeight()/2+350);

        stage.getBatch().begin();
        //draw background here
        stage.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        stage.getBatch().end();

        batch.end();

        uiBatch.begin();
        mainmenuButton.draw(uiBatch);
        uiBatch.end();
    }
    public void update(){
        boolean checkTouch = Gdx.input.isTouched();
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();

        mainmenuButton.update(checkTouch, touchX, touchY);


        if(mainmenuButton.isDown){
            //backgroundMusic.stop();
            game.setScreen(MyGdxGame.menuScreen);
        }


    }
    @Override
    public void dispose() {
        batch.dispose();
        buttonLongTexture.dispose();
        buttonLongDownTexture.dispose();
        //backgroundMusic.dispose();
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
