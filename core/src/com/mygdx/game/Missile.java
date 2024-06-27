package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Missile {
    float angle;
    float xDegree;
    float yDegree;
    Texture missile;
    float x;
    float y;
    float speed;
    Rectangle missileBound;
    boolean isCollision;
    public Missile()
    {
        missile = new Texture(Gdx.files.internal("Enemies/enemies/flying_creature/missile.png"));
        speed = 45;
        missileBound = new Rectangle(x,y,16,16);
    }

    public void render(SpriteBatch batch) {
        batch.draw(missile,x,y);
    }
}
