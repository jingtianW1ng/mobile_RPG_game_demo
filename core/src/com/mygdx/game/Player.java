package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;


public class Player {

    //playerstate
    enum PlayerState
    {
        walkLeft,
        walkRight,
        idleLeft,
        idleRight

    }
    public PlayerState state;
    float characterX;
    float characterY;

    //animation
    Animation walkLeftAni;
    Animation walkRightAni;
    Animation idleLeftAni;
    Animation idleRightAni;
    Array<TextureRegion> walkLeftFrames = new Array<>();
    Array<TextureRegion> walkRightFrames = new Array<>();
    Array<TextureRegion> idleLeftFrames = new Array<>();
    Array<TextureRegion> idleRightFrames = new Array<>();


    TextureRegion currentFrame;
    int frameIndex;
    float stateTime;




    //player movement delta
    float dt;
    Vector2 playerDelta;

    Texture playerTexture;
    Sprite playerSprite;
    Rectangle playerDeltaRectangle;



    public Player (MyGdxGame game ){
        //texture goes here
        playerDelta = new Vector2();
        state = PlayerState.idleRight;


        playerTexture = new Texture("Player/Idel_right/IR0.png");
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(11,16);
        playerDeltaRectangle = new Rectangle(0, 0, playerSprite.getWidth(), playerSprite.getHeight());


        //animation
        for(int i = 0; i < 6 ; i++)
        {
            walkLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Player/Run_left/RL" + i + ".png"))));
            i++;
        }
        for(int i = 0; i < 6; i++)
        {
            walkRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Player/Run_right/RR" + i + ".png"))));
            i++;
        }
        for(int i = 0; i < 6; i++)
        {
            idleLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Player/Idel_left/IL" + i + ".png"))));
            i++;
        }
        for(int i = 0; i < 6; i++)
        {
            idleRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Player/Idel_right/IR" + i + ".png"))));
            i++;
        }

        stateTime = 0.0f;
        walkLeftAni = new Animation(0.25f, walkLeftFrames);
        walkRightAni = new Animation(0.25f, walkRightFrames);
        idleLeftAni = new Animation(0.5f, idleLeftFrames);
        idleRightAni = new Animation(0.5f, idleRightFrames);


        //rectangle
    }

    public void setState(PlayerState state){
        this.state = state;
    }



    public void update(){
        float dt = Gdx.graphics.getDeltaTime();
        playerDeltaRectangle.x = characterX;
        playerDeltaRectangle.y = characterY;
        switch (state)
        {
            case walkLeft:
                state = PlayerState.idleLeft;
                break;
            case walkRight:
                state = PlayerState.idleRight;
                break;
        }

    }

    public void render(Batch batch){

        stateTime += Gdx.graphics.getDeltaTime();
        switch (state)
        {
            case walkLeft:
                currentFrame = (TextureRegion)(walkLeftAni.getKeyFrame(stateTime, true));
                batch.draw(currentFrame,characterX,characterY);
                break;
            case walkRight:
                currentFrame = (TextureRegion)(walkRightAni.getKeyFrame(stateTime, true));
                batch.draw(currentFrame,characterX,characterY);
                break;
            case idleLeft:
                currentFrame = (TextureRegion)(idleLeftAni.getKeyFrame(stateTime, true));
                batch.draw(currentFrame,characterX,characterY);
                break;
            case idleRight:
                currentFrame = (TextureRegion)(idleRightAni.getKeyFrame(stateTime, true));
                batch.draw(currentFrame,characterX,characterY);
                break;
        }

    }

    public Rectangle getBoundingBox() {
        float width = playerSprite.getWidth();
        float height = playerSprite.getHeight();

        float x = characterX;
        float y = characterY;

        return new Rectangle(x, y, width, height);
    }

    public void dispose(){
        //this.texture.dispose();

    }


}
