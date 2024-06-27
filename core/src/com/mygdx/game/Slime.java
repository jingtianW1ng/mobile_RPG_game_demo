package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
public class Slime extends Enemies{
    boolean isCollisionLeft;
    boolean isCollisionRight;
    boolean isCollisionTop;
    boolean isCollisionBottom;
    Animation walkLeftAni;
    Animation walkRightAni;
    Animation idleLeftAni;
    Animation idleRightAni;
    Animation hitEffect;
    Array<TextureRegion> walkLeftFrames = new Array<>();
    Array<TextureRegion> walkRightFrames = new Array<>();
    Array<TextureRegion> idleLeftFrames = new Array<>();
    Array<TextureRegion> idleRightFrames = new Array<>();
    Array<TextureRegion> hitEffectFrames = new Array<>();
    TextureRegion currentFrame;
    float patrolTime = 2;
    float moveCD;
    float stateTime;
    float hitTime;
    float deathTime;
    Animation deathLeft;
    Animation deathRight;
    Array<TextureRegion> deathLeftFrames = new Array<>();
    Array<TextureRegion> deathRightFrames = new Array<>();
    float enemySpeed = 30;
    public enum MoveState
    {
        IDLE_LEFT,
        IDLE_RIGHT,
        RUN_LEFT,
        RUN_RIGHT
    }
    Texture boosting_L;
    Texture boosting_R;
    MoveState moveState;
    Vector2 boostingTarget;
    boolean isBoosting;
    float boostingCD;
    float boostingTime = 2f;
    float weakingCD;
    float weakingTime = 3f;
    Rectangle AttackBound;
    Rectangle enemyBound;
    float slimeHeath = 2;
    boolean isHit;
    boolean boosted;

    float angle;
    float xDegree;
    float yDegree;

    public Slime(float x, float y)
    {
        isHit = false;
        boosted = false;
        this.x = x;
        this.y = y;
        moveState = MoveState.IDLE_RIGHT;
        this.currentState = STATE.PATROLLING;
        //animation
        for(int i = 0; i < 6 ; i++)
        {
            walkLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/slime/run_left/slime_run_L" + i + ".png"))));
        }
        for(int i = 0; i < 6; i++)
        {
            walkRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/slime/run_right/slime_run_R" + i + ".png"))));
        }
        for(int i = 0; i < 6 ; i++)
        {
            idleLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/slime/idle_left/slime_idle_L" + i + ".png"))));
        }
        for(int i = 0; i < 6; i++)
        {
            idleRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/slime/idle_right/slime_idle_R" + i + ".png"))));
        }
        for(int i = 0; i < 3; i++)
        {
            hitEffectFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Effects/hitEffects/hit_effect_anim_f" + i + ".png"))));
        }
        for(int i = 0; i < 3; i++)
        {
            deathLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/slime/death_L/death" + i + ".png"))));
        }
        for(int i = 0; i < 3; i++)
        {
            deathRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/slime/death_R/death" + i + ".png"))));
        }
        stateTime = 0.0f;
        hitTime = 0.0f;
        //enemy move animation
        walkLeftAni = new Animation(0.25f, walkLeftFrames);
        walkRightAni = new Animation(0.25f, walkRightFrames);
        idleLeftAni = new Animation(0.25f, idleLeftFrames);
        idleRightAni = new Animation(0.25f, idleRightFrames);
        //boosting effect
        boosting_L = new Texture(Gdx.files.internal("Enemies/enemies/slime/idle_left/slime_idle_L5.png"));
        boosting_R = new Texture(Gdx.files.internal("Enemies/enemies/slime/idle_right/slime_idle_R5.png"));;

        //death
        deathLeft =  new Animation(0.2f, deathLeftFrames);
        deathRight = new Animation(0.2f, deathRightFrames);

        isRight = true;
        isBoosting = false;

        //attack rectangle
        AttackBound = new Rectangle();
        enemyBound = new Rectangle(x,y,16,13);
        angle = 0;
        xDegree = 0;
        yDegree = 0;

        //hit effect
        hitEffect = new Animation(0.19f, hitEffectFrames);
    }

    public void update(Player player){
        Gdx.app.log("dota: ", "left: " + isCollisionLeft);
        Gdx.app.log("dota: ", "right: " + isCollisionRight);
        Gdx.app.log("dota: ", "top: " + isCollisionTop);
        Gdx.app.log("dota: ", "bot: " + isCollisionBottom);

        if(slimeHeath <= 0)
        {
            this.currentState = STATE.DEATH;
        }
        //set bound pos
        enemyBound.setPosition(x,y);
        updatePrevPos();
        if(this.currentState != STATE.ATTACKING)
        {
            AttackBound.set(0,0,0,0);
        }
        Gdx.app.log("checki: ", "cd: " + boostingCD);
        float dt = Gdx.graphics.getDeltaTime();
        switch(this.currentState) {
            case PATROLLING:
                if(isCollisionRight)
                {
                    isRight = false;
                }
                if(isCollisionLeft)
                {
                    isRight = true;
                }
                //version 1
                moveCD += dt;
                //Gdx.app.log("sp: ","attack: "  + canAttack(player));
                if (moveCD >= patrolTime) {
                    //set idle state
                    idleCD += dt;
                    if (isRight) {
                        moveState = MoveState.IDLE_RIGHT;
                    } else {
                        moveState = MoveState.IDLE_LEFT;
                    }
                    if (idleCD >= idleTime) {
                        if (isRight) {
                            moveCD = 0;
                            isRight = false;
                        } else {
                            moveCD = 0;
                            isRight = true;
                        }
                        idleCD = 0;
                    }
                } else {
                    //set run state
                    if (isRight) {
                        this.x += enemySpeed * dt;
                        moveState = MoveState.RUN_RIGHT;
                    } else {
                        this.x -= enemySpeed * dt;
                        moveState = MoveState.RUN_LEFT;
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
                    if (isCollisionRight) {
                        x -= enemySpeed * dt;
                    } else if (isCollisionLeft) {
                        x += enemySpeed * dt;
                    } else if (isCollisionTop) {
                        y -= enemySpeed * dt;
                    } else if (isCollisionBottom) {
                        y += enemySpeed * dt;
                    } else {
                        //try to move near the player
                        if (distanceFrom(player) < 40) {
                            //state change to boosting
                            isBoosting = true;
                            this.currentState = STATE.BOOSTING;
                        } else {
                            if (this.getPosition().x < player.getPosition().x) {
                                this.x += enemySpeed * dt;
                                moveState = MoveState.RUN_RIGHT;
                                isRight = true;
                            }
                            if (this.getPosition().x > player.getPosition().x) {
                                this.x -= enemySpeed * dt;
                                moveState = MoveState.RUN_LEFT;
                                isRight = false;
                            }
                        }
                        if (this.getPosition().y < player.getPosition().y) {
                            this.y += enemySpeed * dt;
                        }
                        if (this.getPosition().y > player.getPosition().y) {
                            this.y -= enemySpeed * dt;
                        }
                    }
                }


                break;
            case BOOSTING:
                boostingCD += dt;
                if (isBoosting)
                {
                    angle = getAngle(player.getPosition());
                    xDegree = MathUtils.cosDeg(angle);
                    yDegree = MathUtils.sinDeg(angle);
                    //boostingTarget = new Vector2((30) * xDegree,(30) * yDegree);
                    isBoosting = false;
                }
                if(boostingCD < boostingTime)
                {
                    if(isCollisionLeft || isCollisionBottom || isCollisionTop || isCollisionRight)
                    {
                        boostingCD = 0;
                        boosted = false;
                        this.currentState = STATE.WEAKING;
                    }
                    else
                    {
                        this.x += enemySpeed * xDegree * dt * 2;
                        this.y += enemySpeed * yDegree * dt * 2;
                    }

                    if(enemyBound.overlaps(player.getBoundingBox()))
                    {
                        if(!boosted)
                        {
                            player.playerHealth -= 1;
                            boosted = true;
                        }
                    }
                }
                else
                {
                    boostingCD = 0;
                    boosted = false;
                    this.currentState = STATE.WEAKING;
                }
                break;
            case WEAKING:
                weakingCD += dt;
                if(weakingCD > weakingTime)
                {
                    weakingCD = 0;
                    this.currentState = STATE.PATROLLING;
                }
                break;
            case DEATH:
                enemyBound.set(10000,10000,0,0);
                deathTime += dt;
                if(isRight)
                {
                    if(deathRight.isAnimationFinished(deathTime))
                    {
                        this.currentState = STATE.REMOVE;
                    }
                }
                else {
                    if(deathLeft.isAnimationFinished(deathTime))
                    {
                        this.currentState = STATE.REMOVE;
                    }
                }
                break;
            case REMOVE:
                break;
            default:
        }
    }
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        //render
        switch(this.currentState) {
            case PATROLLING: case CHASING:
                //use movestate switch render animation
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
                    case IDLE_LEFT:
                        currentFrame = (TextureRegion)(idleLeftAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,this.x,this.y);
                        break;
                    case IDLE_RIGHT:
                        currentFrame = (TextureRegion)(idleRightAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,this.x,this.y);
                        break;
                }
                break;
            case BOOSTING:
                if(isRight)
                {
                    batch.draw(boosting_R, x,y);
                }
                else
                {
                    batch.draw(boosting_L, x,y);
                }
                break;
            case WEAKING:
                if(isRight)
                {
                    currentFrame = (TextureRegion)(idleRightAni.getKeyFrame(stateTime, true));
                    batch.draw(currentFrame,this.x,this.y);
                }
                else
                {
                    currentFrame = (TextureRegion)(idleLeftAni.getKeyFrame(stateTime, true));
                    batch.draw(currentFrame,this.x,this.y);
                }
                break;
            case DEATH:
                if(isRight)
                {
                    currentFrame = (TextureRegion) (deathRight.getKeyFrame(deathTime, true));
                    batch.draw(currentFrame, this.x, this.y);
                }
                else
                {
                    currentFrame = (TextureRegion) (deathLeft.getKeyFrame(deathTime, true));
                    batch.draw(currentFrame, this.x, this.y);
                }
                break;
            default:
        }
        //render hit
        if(isHit )
        {
            hitTime += Gdx.graphics.getDeltaTime();
            currentFrame = (TextureRegion)(hitEffect.getKeyFrame(hitTime, true));
            batch.draw(currentFrame,this.x + 4,this.y + 4);
        }
        else
        {
            hitTime = 0;
        }
    }

    public void dispose() {

    }

    public void collisionCheckLeft(Rectangle tileRectangle, TiledMapTileLayer tileLayer) {
        isCollisionLeft = false;
        // Create a rectangle representing the left side of the enemy
        Rectangle leftBound = new Rectangle(x - 1, y + 2, 1, 12);

        // Define the bounds of the tileRectangle to check
        int right = (int) Math.ceil(x);
        int top = (int) Math.ceil(y + 16);
        int left = (int) Math.floor(x) - 1;
        int bottom = (int) Math.floor(y);

        right /= tileLayer.getTileWidth();
        top /= tileLayer.getTileHeight();
        left /= tileLayer.getTileWidth();
        bottom /= tileLayer.getTileHeight();

        // Iterate over all tileRectangles in the left side
        for (int y = bottom; y <= top; y++) {
            for (int x = left; x <= right; x++) {
                TiledMapTileLayer.Cell targetCell = tileLayer.getCell(x, y);
                if (targetCell != null) {
                    // Calculate the position of the tileRectangle
                    tileRectangle.x = x * tileLayer.getTileWidth();
                    tileRectangle.y = y * tileLayer.getTileHeight();
                    isCollisionLeft = leftBound.overlaps(tileRectangle);
                }
            }
        }
    }
    public void collisionCheckRight(Rectangle tileRectangle, TiledMapTileLayer tileLayer) {
        isCollisionRight = false;
        // Create a rectangle representing the right side of the enemy
        Rectangle rightBound = new Rectangle(x + 16, y + 2, 1, 12);

        // Define the bounds of the tileRectangle to check
        int right = (int) Math.ceil(x + 16);
        int top = (int) Math.ceil(y + 16);
        int left = (int) Math.floor(x);
        int bottom = (int) Math.floor(y);

        right /= tileLayer.getTileWidth();
        top /= tileLayer.getTileHeight();
        left /= tileLayer.getTileWidth();
        bottom /= tileLayer.getTileHeight();

        // Iterate over all tileRectangles in the right side
        for (int y = bottom; y <= top; y++) {
            for (int x = left; x <= right; x++) {
                TiledMapTileLayer.Cell targetCell = tileLayer.getCell(x, y);
                if (targetCell != null) {
                    // Calculate the position of the tileRectangle
                    tileRectangle.x = x * tileLayer.getTileWidth();
                    tileRectangle.y = y * tileLayer.getTileHeight();
                    isCollisionRight = rightBound.overlaps(tileRectangle);
                }
            }
        }
    }

    public void collisionCheckBottom(Rectangle tileRectangle, TiledMapTileLayer tileLayer) {
        isCollisionBottom = false;
        // Create a rectangle representing the bottom side of the enemy
        Rectangle bottomBound = new Rectangle(x + 2, y - 1, 12, 1);

        // Define the bounds of the tileRectangle to check
        int right = (int) Math.ceil(x + 16);
        int top = (int) Math.ceil(y + 16);
        int left = (int) Math.floor(x);
        int bottom = (int) Math.floor(y);

        right /= tileLayer.getTileWidth();
        top /= tileLayer.getTileHeight();
        left /= tileLayer.getTileWidth();
        bottom /= tileLayer.getTileHeight();

        // Iterate over all tileRectangles at the bottom side
        for (int y = bottom; y <= top; y++) {
            for (int x = left; x <= right; x++) {
                TiledMapTileLayer.Cell targetCell = tileLayer.getCell(x, y);
                if (targetCell != null) {
                    // Calculate the position of the tileRectangle
                    tileRectangle.x = x * tileLayer.getTileWidth();
                    tileRectangle.y = y * tileLayer.getTileHeight();
                    isCollisionBottom = (bottomBound.overlaps(tileRectangle));
                }
            }
        }
    }

    public void collisionCheckTop(Rectangle tileRectangle, TiledMapTileLayer tileLayer)
    {
        isCollisionTop = false;
        // Create a rectangle representing the top side of the enemy
        Rectangle topBound = new Rectangle(x + 2, y + 16, 12, 1);

        // Define the bounds of the tileRectangle to check
        int right = (int) Math.ceil(x + 16);
        int top = (int) Math.ceil(y + 16);
        int left = (int) Math.floor(x);
        int bottom = (int) Math.floor(y);

        right /= tileLayer.getTileWidth();
        top /= tileLayer.getTileHeight();
        left /= tileLayer.getTileWidth();
        bottom /= tileLayer.getTileHeight();

        // Iterate over all tileRectangles at the top side
        for (int y = bottom; y <= top; y++) {
            for (int x = left; x <= right; x++) {
                TiledMapTileLayer.Cell targetCell = tileLayer.getCell(x, y);
                if (targetCell != null) {
                    // Calculate the position of the tileRectangle
                    tileRectangle.x = x * tileLayer.getTileWidth();
                    tileRectangle.y = y * tileLayer.getTileHeight();
                    isCollisionTop = (topBound.overlaps(tileRectangle));
                }
            }
        }
    }
}
