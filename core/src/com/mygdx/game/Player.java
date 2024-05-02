package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

public class Player {

    MyGdxGame game;
    GameScreen gameScreen;



    float x = 0;
    float y = 0;
    float frame = 0;
    float moveSpeed = 12;
    boolean moveLeft = false;
    boolean moveRight = false;
    boolean moveUp = false;
    boolean moveDown = false;
    boolean die;




    Texture[] playerIdelLeft = new Texture[6];




    public Player (MyGdxGame game ){

        for(int i = 0; i < 6; i++){
            this.playerIdelLeft[i] = new Texture("Player/Idel_left/player_IL" + (i) +".png");
        }

    }



    public void update(){
        float dt = Gdx.graphics.getDeltaTime();
        this.frame += 20*dt;
        if (this.frame>=6){
            this.frame=0;
        }
        if(moveLeft){
            x -= moveSpeed;
            moveLeft = false;
        }
        if(moveRight){
            x += moveSpeed;
            moveRight = false;
        }
        if(moveDown){
            y -= moveSpeed;
            moveDown = false;
        }
        if(moveUp){
            y += moveSpeed;
            moveUp = false;
        }

    }

    public void render(Batch batch){
        batch.draw(this.playerIdelLeft[(int)this.frame],this.x,this.y);


    }

    public Rectangle getBoundingBox(){
        //return new Rectangle((int)x + 20,(int)y + 20,65,90);
        return null;
    }

    public void dispose(){
        //this.texture.dispose();
    }


}
