package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
public class Flying extends Enemies{

    Animation walkLeftAni;
    Animation walkRightAni;
    Array<TextureRegion> walkLeftFrames = new Array<>();
    Array<TextureRegion> walkRightFrames = new Array<>();
    TextureRegion currentFrame;
    float patrolTime = 2;
    float moveCD;
    float stateTime;
    float flyingSpeed = 30;
    public Flying()
    {
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
        stateTime = 0.0f;
        walkLeftAni = new Animation(0.25f, walkLeftFrames);
        walkRightAni = new Animation(0.25f, walkRightFrames);

        isRight = true;
    }

    public boolean canMove()
    {
        return moveCD >= patrolTime;
    }
    public void update(){
        float dt = Gdx.graphics.getDeltaTime();

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
                break;
            default:
        }
    }
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

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
            default:
        }
    }

    public void dispose() {

    }
}


