package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
public class Flying extends Enemies{

    Animation walkLeftAni;
    Animation walkRightAni;
    Animation chargeLeft;
    Animation chargeRight;
    Array<TextureRegion> walkLeftFrames = new Array<>();
    Array<TextureRegion> walkRightFrames = new Array<>();
    Array<TextureRegion> chargeLeftFrames = new Array<>();
    Array<TextureRegion> chargeRightFrames = new Array<>();
    Array<Missile> missiles = new Array<>();
    TextureRegion currentFrame;
    float patrolTime = 2;
    float moveCD;
    float stateTime;
    float flyingSpeed = 30;
    float attackCD;

    public enum MoveState
    {
        IDLE_LEFT,
        IDLE_RIGHT,
        RUN_LEFT,
        RUN_RIGHT
    }
    MoveState moveState;
    Missile missile;
    boolean isAttacking;
    float attackingTime;
    boolean canFire;
    boolean missileFired;
    public Flying()
    {
        moveState = MoveState.IDLE_RIGHT;
        this.currentState = STATE.PATROLLING;
        //animation
        for(int i = 0; i < 5 ; i++)
        {
            walkLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/flying_creature/run_left/fly_anim_L" + i + ".png"))));
        }
        for(int i = 0; i < 5; i++)
        {
            walkRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/flying_creature/run_right/fly_anim_R" + i + ".png"))));
        }
        for(int i = 0; i < 5 ; i++)
        {
            chargeLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/flying_creature/atk_left/atk_L" + i + ".png"))));
        }
        for(int i = 0; i < 5; i++)
        {
            chargeRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/flying_creature/atk_right/atk_R" + i + ".png"))));
        }
        stateTime = 0.0f;
        walkLeftAni = new Animation(0.25f, walkLeftFrames);
        walkRightAni = new Animation(0.25f, walkRightFrames);
        chargeLeft = new Animation(1f, chargeLeftFrames);
        chargeRight = new Animation(1f, chargeRightFrames);

        isRight = true;
        isAttacking = false;
        canFire = false;
        missileFired = false;
        attackCD = 4;
    }

    public boolean canMove()
    {
        return moveCD >= patrolTime;
    }
    public void update(Player player){
        float dt = Gdx.graphics.getDeltaTime();
        //update missiles
        if(missiles.size != 0)
        {
            for(int i = 0; i < missiles.size; i++)
            {
                missiles.get(i).x += missiles.get(i).speed * missiles.get(i).xDegree * dt;
                missiles.get(i).y += missiles.get(i).speed * missiles.get(i).yDegree * dt;
            }
        }

        Gdx.app.log("dt: ","time is: " + canFire);
        switch(this.currentState) {
            case PATROLLING:
                //version 1
                moveCD += dt;
                //Gdx.app.log("sb: ","flying distance: "  + moveCD);
                if(moveCD >= patrolTime)
                {
                    idleCD += dt;
                    if(idleCD >= idleTime)
                    {
                        if(isRight)
                        {
                            moveCD = 0;
                            isRight = false;
                        }
                        else
                        {
                            moveCD = 0;
                            isRight = true;
                        }
                        idleCD = 0;
                    }
                }
                else {
                    if(isRight)
                    {
                        this.x += flyingSpeed * dt;
                    }
                    else
                    {
                        this.x -= flyingSpeed * dt;
                    }
                }
                //check if player close enemy and can see player
                if (distanceFrom(player) <= 70) {
                    if (canSeePlayer(player)) {
                        this.currentState = STATE.CHASING;
                    }
                }
                break;
            case CHASING:
                if (distanceFrom(player) > 70) {
                    this.currentState = STATE.PATROLLING;
                } else {
                    //try to move near the player
                    if (distanceFrom(player) < 40) {
                        //state change to attacking
                        attackCD += dt;
                        if(attackCD >= 4)
                        {
                            attackCD = 0;
                            this.currentState = STATE.ATTACKING;
                        }
                    } else {
                        if (this.getPosition().x < player.getPosition().x) {
                            this.x += flyingSpeed * dt;
                            moveState = MoveState.RUN_RIGHT;
                            isRight = true;
                        }
                        if (this.getPosition().x > player.getPosition().x) {
                            this.x -= flyingSpeed * dt;
                            moveState = MoveState.RUN_LEFT;
                            isRight = false;
                        }
                    }
                    if (this.getPosition().y < player.getPosition().y) {
                        this.y += flyingSpeed * dt;
                    }
                    if (this.getPosition().y > player.getPosition().y) {
                        this.y -= flyingSpeed * dt;
                    }
                }
                break;
            case ATTACKING:
                if(canFire)
                {
                    Missile missile = new Missile();
                    missile.angle = getAngle(player.getPosition());
                    missile.xDegree = MathUtils.cosDeg(missile.angle);
                    missile.yDegree = MathUtils.sinDeg(missile.angle);
                    missile.x = this.x;
                    missile.y = this.y;
                    missiles.add(missile);
                    attackingTime = 0;
                    canFire = false;
                    this.currentState = STATE.CHASING;
                }
                else
                {
                    attackingTime += dt;
                    if(isRight)
                    {
                        if(chargeRight.isAnimationFinished(attackingTime))
                        {
                            canFire = true;
                            attackingTime = 0;
                        }
                    }
                    else
                    {
                        if(chargeRight.isAnimationFinished(attackingTime))
                        {
                            canFire = true;
                            attackingTime = 0;
                        }
                    }
                }
                break;
            default:
        }
    }
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        //render missiles
        for(int i = 0; i < missiles.size; i++)
        {
            missiles.get(i).render(batch);
        }
        //render
        switch(this.currentState) {
            case PATROLLING:
                if(isRight)
                {
                    currentFrame = (TextureRegion)(walkRightAni.getKeyFrame(stateTime, true));
                    batch.draw(currentFrame,this.x,this.y);
                }
                else
                {
                    currentFrame = (TextureRegion)(walkLeftAni.getKeyFrame(stateTime, true));
                    batch.draw(currentFrame,this.x,this.y);
                }
                break;
            case CHASING:
                switch(moveState)
                {
                    case RUN_LEFT:
                        currentFrame = (TextureRegion)(walkLeftAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,this.x,this.y);
                        break;
                    case RUN_RIGHT:
                        currentFrame = (TextureRegion)(walkRightAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,this.x,this.y);
                        break;
                }
                break;
            case ATTACKING:
                if(isRight)
                {
                    currentFrame = (TextureRegion)(chargeRight.getKeyFrame(stateTime, true));
                    batch.draw(currentFrame,this.x,this.y);
                }
                else
                {
                    currentFrame = (TextureRegion)(chargeLeft.getKeyFrame(stateTime, true));
                    batch.draw(currentFrame,this.x,this.y);
                }
                break;
            default:
        }
    }

    public void dispose() {

    }
}


