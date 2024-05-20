package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
public class Goblin extends Enemies{
    Animation walkLeftAni;
    Animation walkRightAni;
    Animation idleLeftAni;
    Animation idleRightAni;
    Animation slashLeft;
    Animation slashRight;
    Animation hitEffect;
    Array<TextureRegion> walkLeftFrames = new Array<>();
    Array<TextureRegion> walkRightFrames = new Array<>();
    Array<TextureRegion> idleLeftFrames = new Array<>();
    Array<TextureRegion> idleRightFrames = new Array<>();
    Array<TextureRegion> slashLeftFrames = new Array<>();
    Array<TextureRegion> slashRightFrames = new Array<>();
    Array<TextureRegion> hitEffectFrames = new Array<>();
    TextureRegion currentFrame;
    float patrolTime = 2;
    float moveCD;
    float stateTime;
    float enemySpeed = 30;
    public enum MoveState
    {
        IDLE_LEFT,
        IDLE_RIGHT,
        RUN_LEFT,
        RUN_RIGHT
    }
    Texture knife_L;
    Texture knife_R;
    MoveState moveState;
    boolean canAttack;
    float attackFrequency = 0.5f;
    float attackCD;
    float animeTime;
    float hitTime;
    Rectangle AttackBound;
    Rectangle enemyBound;
    boolean isHitPlayer;
    boolean isHit;
    float goblinHeath = 2;
    boolean isCollision;
    boolean isCollisionLeft;
    boolean isCollisionRight;
    boolean isCollisionBottom;
    boolean isCollisionTop;

    public Goblin(float x, float y)
    {
        isHit = false;
        this.x = x;
        this.y = y;
        moveState = MoveState.IDLE_RIGHT;
        this.currentState = STATE.PATROLLING;
        //animation
        for(int i = 0; i < 6 ; i++)
        {
            walkLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/goblin/run_left/goblin_run_L" + i + ".png"))));
        }
        for(int i = 0; i < 6; i++)
        {
            walkRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/goblin/run_right/goblin_run_R" + i + ".png"))));
        }
        for(int i = 0; i < 6 ; i++)
        {
            idleLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/goblin/idle_left/goblin_idle_L" + i + ".png"))));
        }
        for(int i = 0; i < 6; i++)
        {
            idleRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Enemies/enemies/goblin/idle_right/goblin_idle_R" + i + ".png"))));
        }
        for(int i = 0; i < 3; i++)
        {
            slashLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Effects/slash_left/slash_effect_L" + i + ".png"))));
        }
        for(int i = 0; i < 3; i++)
        {
            slashRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Effects/slash_right/slash_effect_R" + i + ".png"))));
        }
        for(int i = 0; i < 3; i++)
        {
            hitEffectFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Effects/hitEffects/hit_effect_anim_f" + i + ".png"))));
        }
        stateTime = 0.0f;
        hitTime = 0.0f;
        //enemy move animation
        walkLeftAni = new Animation(0.25f, walkLeftFrames);
        walkRightAni = new Animation(0.25f, walkRightFrames);
        idleLeftAni = new Animation(0.5f, idleLeftFrames);
        idleRightAni = new Animation(0.5f, idleRightFrames);
        //attack effect
        slashLeft =  new Animation(0.2f, slashLeftFrames);
        slashRight = new Animation(0.2f, slashRightFrames);
        //hit effect
        hitEffect = new Animation(0.19f, hitEffectFrames);


        isRight = true;
        canAttack = false;

        //create knife
        knife_L = new Texture(Gdx.files.internal("Enemies/enemies/goblin/goblin_knife_L.png"));
        knife_R = new Texture(Gdx.files.internal("Enemies/enemies/goblin/goblin_knife_R.png"));

        //attack rectangle
        AttackBound = new Rectangle();

        enemyBound = new Rectangle(x,y,16,16);
    }

    public Rectangle getBoundingBox(){
        return new Rectangle(x, y, 11, 16);
    }

    public Rectangle createAttackBound()
    {
        if(isRight)
        {
            return new Rectangle(x + 17, y, 11, 16);
        }
        else
        {
            return new Rectangle(x - 17, y, 11, 16);
        }
    }

    public void update(Player player){
        Gdx.app.log("col","collision is: " + isCollision);
        //set bound pos
        enemyBound.setPosition(x,y);

        if(this.currentState != STATE.ATTACKING)
        {
            AttackBound.set(0,0,0,0);
            isHitPlayer = false;
        }
        Gdx.app.log("checki: ", "cd: " + attackCD);
        float dt = Gdx.graphics.getDeltaTime();
        switch(this.currentState) {
            case PATROLLING:
                //version 1
                moveCD += dt;
                //Gdx.app.log("sp: ","attack: "  + canAttack(player));
                if(moveCD >= patrolTime)
                {
                    //set idle state
                    idleCD += dt;
                    if(isRight)
                    {
                        moveState = MoveState.IDLE_RIGHT;
                    }
                    else
                    {
                        moveState = MoveState.IDLE_LEFT;
                    }
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
                    //set run state
                    if(isRight)
                    {
                        this.x += enemySpeed * dt;
                        moveState = MoveState.RUN_RIGHT;
                    }
                    else
                    {
                        this.x -= enemySpeed * dt;
                        moveState = MoveState.RUN_LEFT;
                    }
                }
                //check if player close enemy and can see player
                if(distanceFrom(player) <= 70)
                {
                    if(canSeePlayer(player))
                    {
                        this.currentState = STATE.CHASING;
                    }
                }
                break;
            case CHASING:
                if(distanceFrom(player) > 70)
                {
                    this.currentState = STATE.PATROLLING;
                }
                else
                {
                    if (isCollision) {
                        // Determine player's position relative to the enemy
                        boolean playerIsRight = player.getPosition().x > this.getPosition().x;
                        boolean playerIsAbove = player.getPosition().y > this.getPosition().y;

                        // Try to move in the direction where there's no collision
                        if (playerIsRight) {
                            // If player is to the right, try to move left if there's no collision
                            if (!isCollisionLeft) {
                                this.x -= enemySpeed * dt;
                                moveState = MoveState.RUN_LEFT;
                            }
                        } else {
                            // If player is to the left, try to move right if there's no collision
                            if (!isCollisionRight) {
                                this.x += enemySpeed * dt;
                                moveState = MoveState.RUN_RIGHT;
                            }
                        }

                        if (playerIsAbove) {
                            // If player is above, try to move down if there's no collision
                            if (!isCollisionBottom) {
                                this.y -= enemySpeed * dt;
                            }
                        } else {
                            // If player is below, try to move up if there's no collision
                            if (!isCollisionTop) {
                                this.y += enemySpeed * dt;
                            }
                        }
                    }
                    else
                    {
                        //try to move near the player
                        if(distanceFrom(player) < 20)
                        {
                            Gdx.app.log("rs: ", "player y: " + Math.round(player.getPosition().y));
                            Gdx.app.log("cogo: ", "collison right: " + isCollisionRight);
                            //only if player y == enemy y
                            if(Math.round(this.y) >= Math.round(player.getPosition().y - 1)
                                    && Math.round(this.y) <= Math.round(player.getPosition().y + 1))
                            {
                                //Gdx.app.log("bs: ","distance: "  + distanceFrom(player));
                                if (this.getPosition().x < player.getPosition().x) {
                                    if (distanceFrom(player) <= 17) {
                                        this.x -= enemySpeed * dt;
                                        moveState = MoveState.RUN_LEFT;
                                        isRight = false;
                                        attackCD = 0;
                                    } else {
                                        moveState = MoveState.IDLE_RIGHT;
                                        isRight = true;
                                        //attack goes here
                                        attackCD += dt;
                                        if(attackCD >= attackFrequency)
                                        {
                                            this.currentState = STATE.ATTACKING;
                                        }
                                    }
                                }
                                else
                                {
                                    Gdx.app.log("bs: ","here2: "  + distanceFrom(player));
                                    if(distanceFrom(player) <= 17)
                                    {
                                        this.x += enemySpeed * dt;
                                        moveState = MoveState.RUN_RIGHT;
                                        isRight = true;
                                        attackCD = 0;
                                    }
                                    else
                                    {
                                        moveState = MoveState.IDLE_LEFT;
                                        isRight = false;
                                        //attack goes here
                                        attackCD += dt;
                                        if(attackCD >= attackFrequency)
                                        {
                                            this.currentState = STATE.ATTACKING;
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if (this.getPosition().x < player.getPosition().x)
                            {
                                this.x += enemySpeed * dt;
                                moveState = MoveState.RUN_RIGHT;
                                isRight = true;
                                attackCD = 0;
                            }
                            if (this.getPosition().x > player.getPosition().x)
                            {
                                this.x -= enemySpeed * dt;
                                moveState = MoveState.RUN_LEFT;
                                isRight = false;
                                attackCD = 0;
                            }
                        }
                        if (this.getPosition().y < player.getPosition().y)
                        {
                            this.y += enemySpeed * dt;
                        }
                        if (this.getPosition().y > player.getPosition().y)
                        {
                            this.y -= enemySpeed * dt;
                        }
                    }

                    //Gdx.app.log("bs: ","left distance: "  + distanceFrom(player));
                    //back potral goes here
                    Gdx.app.log("at: ","can attack: " + canAttack);
                }
                break;
            case ATTACKING:
                animeTime += dt;
                //Gdx.app.log("bs: ","ready to attack");
                //Gdx.app.log("bs: ","isRight: " + isRight);
                Gdx.app.log("time: ","anime time is: " + animeTime);
                if(isRight)
                {
                    //finish attack back to chasing
                    if(slashRight.isAnimationFinished(animeTime))
                    {
                        attackCD = 0;
                        animeTime = 0;
                        this.currentState = STATE.CHASING;
                    }
                    AttackBound.set(x + 17,y,16,16);
                    //check if overlap with player when enemy attacking
                    if(player.getBoundingBox().overlaps(AttackBound) && animeTime > 0.3)
                    {
                        if(!isHitPlayer)
                        {
                            Gdx.app.log("attack: ","isOverlap: " + player.getBoundingBox().overlaps(AttackBound));
                            player.playerHealth -= 1;
                            isHitPlayer = true;
                        }
                    }
                    moveState = MoveState.IDLE_RIGHT;
                }
                else
                {
                    //finish attack back to chasing
                    if(slashLeft.isAnimationFinished(animeTime))
                    {
                        attackCD = 0;
                        animeTime = 0;
                        this.currentState = STATE.CHASING;
                    }
                    AttackBound.set(x - 17,y,16,16);
                    if(player.getBoundingBox().overlaps(AttackBound) && animeTime > 0.3)
                    {
                        if(!isHitPlayer)
                        {
                            Gdx.app.log("attack: ","isOverlap: " + player.getBoundingBox().overlaps(AttackBound));
                            player.playerHealth -= 1;
                            isHitPlayer = true;
                        }
                    }
                    moveState = MoveState.IDLE_LEFT;
                }

                break;
            default:
        }
    }
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        //render
        switch(this.currentState) {
            case PATROLLING: case CHASING:
                //render knife
                if(isRight)
                {
                    batch.draw(knife_R,x + 12,y + 3);
                }
                else
                {
                    batch.draw(knife_L,x - 12,y + 3);
                }
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
            case ATTACKING:
                switch(moveState) {
                    case IDLE_LEFT:
                        currentFrame = (TextureRegion) (idleLeftAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame, this.x, this.y);
                        break;
                    case IDLE_RIGHT:
                        currentFrame = (TextureRegion) (idleRightAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame, this.x, this.y);
                        break;
                }
                if(isRight)
                {
                    currentFrame = (TextureRegion) (slashRight.getKeyFrame(animeTime, true));
                    batch.draw(currentFrame, this.x + 20, this.y);
                }
                else
                {
                    currentFrame = (TextureRegion) (slashLeft.getKeyFrame(animeTime, true));
                    batch.draw(currentFrame, this.x - 20, this.y);
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

    public void collisionCheck(Rectangle tileRectangle, TiledMapTileLayer tileLayer)
    {
        isCollision = false;
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

    public void collisionCheckLeft(Rectangle tileRectangle, TiledMapTileLayer tileLayer) {
        isCollisionLeft = false;
        // Create a rectangle representing the left side of the enemy
        Rectangle leftBound = new Rectangle(x - 1, y, 1, 16);

        // Define the bounds of the tileRectangle to check
        int right = (int) Math.ceil(x);
        int top = (int) Math.ceil(y + 16);
        int left = (int) Math.floor(x) - 1;
        int bottom = (int) Math.floor(y);

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
        Rectangle rightBound = new Rectangle(x + 16, y, 1, 16);

        // Define the bounds of the tileRectangle to check
        int right = (int) Math.ceil(x + 16);
        int top = (int) Math.ceil(y + 16);
        int left = (int) Math.floor(x);
        int bottom = (int) Math.floor(y);

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
        Rectangle bottomBound = new Rectangle(x, y - 1, 16, 1);

        // Define the bounds of the tileRectangle to check
        int right = (int) Math.ceil(x + 16);
        int top = (int) Math.ceil(y + 16);
        int left = (int) Math.floor(x);
        int bottom = (int) Math.floor(y);

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
        Rectangle topBound = new Rectangle(x, y + 16, 16, 1);

        // Define the bounds of the tileRectangle to check
        int right = (int) Math.ceil(x + 16);
        int top = (int) Math.ceil(y + 16);
        int left = (int) Math.floor(x);
        int bottom = (int) Math.floor(y);

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
