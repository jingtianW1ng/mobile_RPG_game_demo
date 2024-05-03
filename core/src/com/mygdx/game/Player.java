package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    Texture characterTexture;
    float characterX;
    float characterY;


    //player movement delta
    float dt;
    Vector2 playerDelta;

    public Player (MyGdxGame game ){
        //texture goes here
        playerDelta = new Vector2();
        characterTexture = new Texture("Player/Idel_right/player_IR0.png");

        //animation

        //rectangle
    }



    public void update(){
        float dt = Gdx.graphics.getDeltaTime();
    }

    public void render(Batch batch){
        batch.draw(characterTexture, characterX, characterY, 16, 16);
    }

    public Rectangle getBoundingBox(){
        //return new Rectangle((int)x + 20,(int)y + 20,65,90);
        return null;
    }

    public void dispose(){
        //this.texture.dispose();
        characterTexture.dispose();
    }


}
