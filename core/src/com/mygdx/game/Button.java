package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Button {
    float x;
    float y;
    float w;
    float h;
    boolean isDown;

    BitmapFont font;

    Texture textureUp;
    Texture textureDown;
    String buttonText;


    public Button (String bt,float x, float y, float w, float h, Texture textureUp, Texture textureDown ) {
        this.buttonText = bt;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        isDown = false;

        font = new BitmapFont();
        font.getData().setScale(5,5);



        this.textureUp = textureUp;
        this.textureDown = textureDown;
    }

    public void update(boolean checkTouch, int touchX, int touchY) {
        isDown = false;

        if (checkTouch) {
            int h2 = Gdx.graphics.getHeight();
            //Touch coordinates have origin in top-left instead of bottom left
            if (touchX >= x && touchX <= x + w && h2 - touchY >= y && h2 - touchY <= y + h) {
                isDown = true;
            }
        }

    }

    public void draw(SpriteBatch batch) {
        if (! isDown) {
            batch.draw(textureUp, x, y, w, h);

        } else {
            batch.draw(textureDown, x, y, w, h);

        }

        if(buttonText == "Main Menu"){
            font.draw(batch, buttonText, x+150, y+125);
        }
        else{
            font.draw(batch, buttonText, x+250, y+125);
        }


    }
}
