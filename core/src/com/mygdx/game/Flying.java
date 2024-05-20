package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
public class Flying extends Enemies{

    Animation walkLeftAni;
    Animation walkRightAni;
    Animation chargeLeft;
    Animation chargeRight;
    Animation hitEffect;
    Array<TextureRegion> walkLeftFrames = new Array<>();
    Array<TextureRegion> walkRightFrames = new Array<>();
    Array<TextureRegion> chargeLeftFrames = new Array<>();
    Array<TextureRegion> chargeRightFrames = new Array<>();
    Array<TextureRegion> hitEffectFrames = new Array<>();
    Array<Missile> missiles = new Array<>();
    TextureRegion currentFrame;
    float patrolTime = 2;
    float moveCD;
    float stateTime;
    float flyingSpeed = 30;
    float attackCD;
    Rectangle enemyBound;
    float flyingHealth = 2;
    float hitTime;
    boolean alreadyHit;

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
    float angle;
    float xDegree;
    float yDegree;
    float waitCD;
    boolean isHit;
    boolean isCollision;
    public Flying(float x, float y)
    {
        alreadyHit = false;
        isHit = false;
        this.x = x;
        this.y = y;
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
        for(int i = 0; i < 3; i++)
        {
            hitEffectFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Effects/hitEffects/hit_effect_anim_f" + i + ".png"))));
        }

        stateTime = 0.0f;
        hitTime = 0.0f;
        walkLeftAni = new Animation(0.25f, walkLeftFrames);
        walkRightAni = new Animation(0.25f, walkRightFrames);
        chargeLeft = new Animation(0.25f, chargeLeftFrames);
        chargeRight = new Animation(0.25f, chargeRightFrames);
        //hit effect
        hitEffect = new Animation(0.19f, hitEffectFrames);

        isRight = true;
        isAttacking = false;
        canFire = false;
        missileFired = false;
        attackCD = 4;
        enemyBound = new Rectangle(x,y,16,16);
    }

    public void collisionCheck(Rectangle tileRectangle, TiledMapTileLayer tileLayer)
    {
        int right = (int) Math.ceil(x + 16);
        int top = (int) Math.ceil(y + 16);

        // Find bottom-left corner tile
        int left = (int) Math.floor(x);
        int bottom = (int) Math.floor(y);

        // Divide bounds by tile sizes to retrieve tile indices
        right /= tileLayer.getTileWidth();
        top /= tileLayer.getTileHeight();
        left /= tileLayer.getTileWidth();
        bottom /= tileLayer.getTileHeight();

        //TODO Loop through selected tiles and correct by each axis
        //EXTRA: Try counting down if moving left or down instead of counting up
        for (int y = bottom; y <= top; y++) {
            for (int x = left; x <= right; x++) {
                TiledMapTileLayer.Cell targetCell = tileLayer.getCell(x, y);
                // If the cell is empty, ignore it
                if (targetCell == null) continue;
                // correct against tested squares
                tileRectangle.x = x * tileLayer.getTileWidth();
                tileRectangle.y = y * tileLayer.getTileHeight();
                //check if enemy overlap with tilemap
                isCollision = tileRectangle.overlaps(enemyBound);
            }
        }
    }
    public boolean canMove()
    {
        return moveCD >= patrolTime;
    }
    public void update(Player player){
        float dt = Gdx.graphics.getDeltaTime();
        //set bound pos
        enemyBound.setPosition(x,y );
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
                canFire = false;
                if (distanceFrom(player) > 70) {
                    this.currentState = STATE.PATROLLING;
                } else {
                    //try to move near the player
                    if (distanceFrom(player) < 40) {
                        if (this.getPosition().x < player.getPosition().x) {
                            isRight = true;
                        }
                        if (this.getPosition().x > player.getPosition().x) {
                            isRight = false;
                        }
                        //state change to attacking
                        attackCD += dt;
                        if(attackCD >= 4)
                        {
                            attackCD = 0;
                            this.currentState = STATE.ATTACKING;
                        }
                    }
                    else
                    {
                        angle = getAngle(player.getPosition());
                        xDegree = MathUtils.cosDeg(angle);
                        yDegree = MathUtils.sinDeg(angle);
                        x += flyingSpeed * xDegree * dt;
                        y += flyingSpeed * yDegree * dt;
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
                    canFire = false;
                    attackingTime = 0;
                    if(x < player.characterX)
                    {
                        isRight = true;
                    }
                    if(x > player.characterX)
                    {
                        isRight = false;
                    }
                    this.currentState = STATE.IDLE;
                }
                else
                {
                    attackingTime += dt;
                    if(isRight)
                    {
                        if(chargeRight.isAnimationFinished(attackingTime))
                        {
                            canFire = true;
                        }
                    }
                    else
                    {
                        //chargeLeft.isAnimationFinished(attackingTime)
                        if(chargeLeft.isAnimationFinished(attackingTime))
                        {
                            canFire = true;
                        }
                    }
                }
                break;
            case IDLE:
                waitCD += dt;
                if(waitCD >= 1)
                {
                    this.currentState = STATE.CHASING;
                    waitCD = 0;
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
            case PATROLLING: case CHASING: case IDLE:
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

            case ATTACKING:
                if(isRight)
                {
                    currentFrame = (TextureRegion)(chargeRight.getKeyFrame(attackingTime, true));
                    batch.draw(currentFrame,this.x,this.y);
                }
                else
                {
                    currentFrame = (TextureRegion)(chargeLeft.getKeyFrame(attackingTime, true));
                    batch.draw(currentFrame,this.x,this.y);
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
}


