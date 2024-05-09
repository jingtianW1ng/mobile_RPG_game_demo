package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Enemies {
    public enum STATE
    {
        PATROLLING,
        BOOSTING,
        CHASING,
        ATTACKING,
        FLEEING,
        DODGING
    }

    STATE currentState;

    float x;
    float y;
    boolean isRight;
    float idleTime = 1.0f;
    float idleCD;


    public Vector2 getPosition() {
        float currentX = this.x;
        float currentY = this.y;
        return new Vector2(currentX, currentY);
    }
    public float getAngle(Vector2 target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - this.getPosition().y, target.x - this.getPosition().x));

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    public boolean canSeePlayer(Player player) {
        float angle = this.getAngle(player.getPosition());
        if(isRight)
        {
            if (player.getPosition().x > this.x) {
                return angle <= 45 || angle >= 315;
            }
        }
        else
        {
            if (player.getPosition().x < this.x) {
                return angle >= 135 && angle <= 225;
            }
        }
        return false;
    }
    public float distanceFrom(Player player) {
        return this.getPosition().dst(player.getPosition());
    }
}