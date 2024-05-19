package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;


public class Items {

    float itemX;
    float itemY;

    Texture texture;
    boolean pickUp;

    Rectangle boundingBox;


    public Items (float x, float y, float w , float h, String texturePath){
        itemX = x;
        itemY = y;


        //texture
        texture = new Texture(texturePath);

        boundingBox = new Rectangle(x,y,w,h);

    }



    public void update(){
        float dt = Gdx.graphics.getDeltaTime();


        if(pickUp){
            boundingBox = new Rectangle(0,0,0,0);
        }


    }

    public void render(Batch batch){
        if(!pickUp){
            batch.draw(texture,itemX,itemY);
        }




    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void dispose(){


    }


}