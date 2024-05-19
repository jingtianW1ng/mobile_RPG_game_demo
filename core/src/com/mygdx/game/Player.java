package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Sprite;



public class Player {

    //playerstate
    enum PlayerState
    {
        walkLeft,
        walkRight,
        idleLeft,
        idleRight,
        attacking

    }
    public PlayerState state;

    float characterX;
    float characterY;

    //animation
    Animation walkLeftAni;
    Animation walkRightAni;
    Animation idleLeftAni;
    Animation idleRightAni;
    Animation attackLeft;
    Animation attackRight;
    Texture swordLeft;
    Texture swordRight;
    Texture stayLeft;
    Texture stayRight;
    Array<TextureRegion> walkLeftFrames = new Array<>();
    Array<TextureRegion> walkRightFrames = new Array<>();
    Array<TextureRegion> idleLeftFrames = new Array<>();
    Array<TextureRegion> idleRightFrames = new Array<>();
    Array<TextureRegion> attackLeftFrames = new Array<>();
    Array<TextureRegion> attackRightFrames = new Array<>();
    TextureRegion currentFrame;

    //player health
    Texture[] healthUi = new Texture[5];
    int playerHealth = 4;

    int frameIndex;
    float stateTime;

    Rectangle AttackBound;
    boolean isRight;
    float attackTime;
    boolean attacked;


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
        isRight = true;

        //health UI
        for(int i = 0; i < 5 ; i++)
        {
            healthUi[i] = new Texture("UI/new_ui/health_ui/health_ui"+i+".png");
        }


        playerTexture = new Texture("Player/Idel_right/IR0.png");
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(11,16);
        playerDeltaRectangle = new Rectangle(0, 0, playerSprite.getWidth(), playerSprite.getHeight());



        //animation
        for(int i = 0; i < 6 ; i++)
        {
            walkLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Player/Run_left/RL" + i + ".png"))));
        }
        for(int i = 0; i < 6; i++)
        {
            walkRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Player/Run_right/RR" + i + ".png"))));
        }
        for(int i = 0; i < 6; i++)
        {
            idleLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Player/Idel_left/IL" + i + ".png"))));
        }
        for(int i = 0; i < 6; i++)
        {
            idleRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Player/Idel_right/IR" + i + ".png"))));
        }
        for(int i = 0; i < 3; i++)
        {
            attackLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Effects/slash_left/slash_effect_L" + i + ".png"))));
        }
        for(int i = 0; i < 3; i++)
        {
            attackRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Effects/slash_right/slash_effect_R" + i + ".png"))));
        }

        stateTime = 0.0f;
        walkLeftAni = new Animation(0.25f, walkLeftFrames);
        walkRightAni = new Animation(0.25f, walkRightFrames);
        idleLeftAni = new Animation(0.5f, idleLeftFrames);
        idleRightAni = new Animation(0.5f, idleRightFrames);


        //attack effect
        attackLeft = new Animation(0.25f, attackLeftFrames);
        attackRight = new Animation(0.25f, attackRightFrames);

        //sword texture
        swordLeft = new Texture("Player/sword_left.png");
        swordRight = new Texture("Player/sword_right.png");

        //stay texture
        stayLeft = new Texture("Player/Idel_left/IL0.png");
        stayRight = new Texture("Player/Idel_right/IR0.png");

        AttackBound = new Rectangle();

        attacked = false;
    }

    public void setState(PlayerState state){
        this.state = state;
    }

    public Vector2 getPosition() {
        float currentX = this.characterX;
        float currentY = this.characterY;
        return new Vector2(currentX, currentY);
    }

    public void attack()
    {
        state = PlayerState.attacking;
    }


    public void update(Array<Flying> flyings, Array<Goblin> goblins, Array<Slime> slimes, Boss boss)
    {
        if(state != PlayerState.attacking)
        {
            AttackBound.set(0,0,0,0);
            for(int i = 0; i < flyings.size; i++)
            {
                flyings.get(i).isHit = false;
            }
            for(int i = 0; i < goblins.size; i++)
            {
                goblins.get(i).isHit = false;
            }
            for(int i = 0; i < slimes.size; i++)
            {
                slimes.get(i).isHit = false;
            }
            boss.isHit = false;
        }
        Gdx.app.log("checkH","flying heath is: " + flyings.get(0).flyingHealth);

        float dt = Gdx.graphics.getDeltaTime();
        switch (state)
        {
            case walkLeft:
                state = PlayerState.idleLeft;
                break;
            case walkRight:
                state = PlayerState.idleRight;
                break;
            case attacking:
                attackTime += dt;
                if(isRight)
                {
                    if(attackRight.isAnimationFinished(attackTime))
                    {
                        attackTime = 0;
                        attacked = false;
                        state = PlayerState.idleRight;
                    }

                    if(attackTime >= 0.2 && !attacked)
                    {
                        //spawn attack bound
                        AttackBound.set(characterX + 14,characterY + 2,16,16);
                        //TODO player overlap checker
                        for(int i = 0; i < flyings.size; i++)
                        {
                            if(AttackBound.overlaps(flyings.get(i).enemyBound))
                            {
                                flyings.get(i).flyingHealth -= 1;
                                flyings.get(i).isHit = attackTime >= 0.2 && attackTime <= 0.65;
                            }
                        }
                        for(int i = 0; i < goblins.size; i++)
                        {
                            if(AttackBound.overlaps(goblins.get(i).enemyBound))
                            {
                                goblins.get(i).goblinHeath -= 1;
                                goblins.get(i).isHit = attackTime >= 0.2 && attackTime <= 0.65;
                            }
                        }
                        for(int i = 0; i < slimes.size; i++)
                        {
                            if(AttackBound.overlaps(slimes.get(i).enemyBound))
                            {
                                slimes.get(i).slimeHeath -= 1;
                                slimes.get(i).isHit = attackTime >= 0.2 && attackTime <= 0.65;
                            }
                        }
                        if(AttackBound.overlaps(boss.bossBound))
                        {
                            boss.bossHealth -= 1;
                            boss.isHit = attackTime >= 0.2 && attackTime <= 0.65;
                        }
                        attacked = true;
                    }
                }
                else
                {
                    if(attackLeft.isAnimationFinished(attackTime))
                    {
                        attackTime = 0;
                        attacked = false;
                        state = PlayerState.idleLeft;
                    }
                    //TODO player overlap checker
                    if(attackTime >= 0.2 && !attacked)
                    {
                        //spawn attack bound
                        AttackBound.set(characterX - 14,characterY + 2,16,16);
                        //TODO player overlap checker
                        for(int i = 0; i < flyings.size; i++)
                        {
                            if(AttackBound.overlaps(flyings.get(i).enemyBound))
                            {
                                flyings.get(i).flyingHealth -= 1;
                                flyings.get(i).isHit = attackTime >= 0.2 && attackTime <= 0.65;
                            }
                        }
                        for(int i = 0; i < goblins.size; i++)
                        {
                            if(AttackBound.overlaps(goblins.get(i).enemyBound))
                            {
                                goblins.get(i).goblinHeath -= 1;
                                goblins.get(i).isHit = attackTime >= 0.2 && attackTime <= 0.65;
                            }
                        }
                        for(int i = 0; i < slimes.size; i++)
                        {
                            if(AttackBound.overlaps(slimes.get(i).enemyBound))
                            {
                                slimes.get(i).slimeHeath -= 1;
                                slimes.get(i).isHit = attackTime >= 0.2 && attackTime <= 0.65;
                            }
                        }
                        if(AttackBound.overlaps(boss.bossBound))
                        {
                            boss.bossHealth -= 1;
                            boss.isHit = attackTime >= 0.2 && attackTime <= 0.65;
                        }
                        attacked = true;
                    }
                }
                break;
        }

    }

    public void render(Batch batch){
        stateTime += Gdx.graphics.getDeltaTime();

        batch.draw(healthUi[playerHealth], characterX+70,characterY+70);

        switch (state)
        {
            case walkLeft:
                currentFrame = (TextureRegion)(walkLeftAni.getKeyFrame(stateTime, true));
                batch.draw(currentFrame,characterX,characterY);
                batch.draw(swordLeft, characterX - 14, characterY + 2);
                break;
            case walkRight:
                currentFrame = (TextureRegion)(walkRightAni.getKeyFrame(stateTime, true));
                batch.draw(currentFrame,characterX,characterY);
                batch.draw(swordRight, characterX + 14, characterY + 2);
                break;
            case idleLeft:
                currentFrame = (TextureRegion)(idleLeftAni.getKeyFrame(stateTime, true));
                batch.draw(currentFrame,characterX,characterY);
                batch.draw(swordLeft, characterX - 14, characterY + 2);
                break;
            case idleRight:
                currentFrame = (TextureRegion)(idleRightAni.getKeyFrame(stateTime, true));
                batch.draw(currentFrame,characterX,characterY);
                batch.draw(swordRight, characterX + 14, characterY + 2);
                break;
            case attacking:
                if(isRight)
                {
                    currentFrame = (TextureRegion)(attackRight.getKeyFrame(attackTime, true));
                    batch.draw(currentFrame,characterX + 14,characterY + 2);
                    batch.draw(stayRight,characterX ,characterY);
                }
                else
                {
                    currentFrame = (TextureRegion)(attackLeft.getKeyFrame(attackTime, true));
                    batch.draw(currentFrame,characterX - 14,characterY + 2);
                    batch.draw(stayLeft,characterX ,characterY);
                }
                break;
        }

    }

    public Rectangle getBoundingBox(){
        return new Rectangle(characterX, characterY, 11, 16);
    }

    public void dispose(){
        //this.texture.dispose();

    }


}
