package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemies {
    public enum STATE
    {
        PATROLLING,
        BOOSTING,
        CHASING,
        ATTACKING,
        WEAKING,
        IDLE,
        FLEEING,
        DODGING
    }
    Array<Vector2> xyChecker = new Array<>();
    STATE currentState;

    float x;
    float y;
    boolean isRight;
    float idleTime = 1.0f;
    float idleCD;
    Rectangle truePos;
    public void updatePrevPos()
    {
        if(xyChecker.size >= 2)
        {
            Vector2 currentPos = new Vector2(x, y);
            // get prev element
            Vector2 prevPos = xyChecker.get(0);
            // update element
            xyChecker.removeIndex(0);
            xyChecker.add(currentPos);
            Gdx.app.log("isMove: ", "this is current x: " + x);
            Gdx.app.log("isMove: ", "list size is: " + xyChecker.size);
            Gdx.app.log("isMove: ", "this is list's now x: " + xyChecker.get(1).x);
            Gdx.app.log("isMove: ", "this is list's prev x: " + xyChecker.get(0).x);
        }
        else
        {
            Vector2 currentPos = new Vector2(x, y);
            xyChecker.add(currentPos);
        }
    }

    public boolean checkMove()
    {
        return xyChecker.get(0).x == xyChecker.get(1).x
                && xyChecker.get(0).y == xyChecker.get(1).y;
    }

    public Vector2 getPosition() {
        float currentX = this.x;
        float currentY = this.y;
        return new Vector2(currentX, currentY);
    }
    public Vector2 getBossPosition() {
        float currentX = truePos.x;
        float currentY = truePos.y;
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
    public float bossGetAngle(Vector2 target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - this.getBossPosition().y, target.x - this.getBossPosition().x));

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    public boolean bossCanSeePlayer(Player player) {
        float angle = this.bossGetAngle(player.getPosition());
        if(isRight)
        {
            if (player.getPosition().x > truePos.x) {
                return angle <= 45 || angle >= 315;
            }
        }
        else
        {
            if (player.getPosition().x < truePos.x) {
                return angle >= 135 && angle <= 225;
            }
        }
        return false;
    }
    public float distanceFrom(Player player) {
        return this.getPosition().dst(player.getPosition());
    }

    public float bossDistanceFrom(Player player) {
        return this.getBossPosition().dst(player.getPosition());
    }
}