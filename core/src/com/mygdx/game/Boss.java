package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
public class Boss extends Enemies{
    boolean isCollisionLeft;
    boolean isCollisionRight;
    boolean isCollisionTop;
    boolean isCollisionBottom;
    Animation walkLeftAni;
    Animation walkRightAni;
    Animation idleLeftAni;
    Animation idleRightAni;
    Animation attackLeft;
    Animation attackRight;
    Animation hurtLeft;
    Animation hurtRight;
    Animation deathLeft;
    Animation deathRight;
    Animation wakeupLeft;
    Animation wakeupRight;
    Animation hitEffect;
    Array<TextureRegion> walkLeftFrames = new Array<>();
    Array<TextureRegion> walkRightFrames = new Array<>();
    Array<TextureRegion> idleLeftFrames = new Array<>();
    Array<TextureRegion> idleRightFrames = new Array<>();
    Array<TextureRegion> attackLeftFrames = new Array<>();
    Array<TextureRegion> attackRightFrames = new Array<>();
    Array<TextureRegion> hurtLeftFrames = new Array<>();
    Array<TextureRegion> hurtRightFrames = new Array<>();
    Array<TextureRegion> deathLeftFrames = new Array<>();
    Array<TextureRegion> deathRightFrames = new Array<>();
    Array<TextureRegion> wakeupLeftFrames = new Array<>();
    Array<TextureRegion> wakeupRightFrames = new Array<>();
    Array<TextureRegion> hitEffectFrames = new Array<>();
    TextureRegion currentFrame;
    float hurtTime;
    float deathTime;
    float stateTime;
    float enemySpeed = 30;
    boolean isWeak = false;
    float renderX;
    float renderY;
    public enum MoveState
    {
        IDLE_LEFT,
        IDLE_RIGHT,
        RUN_LEFT,
        RUN_RIGHT
    }

    MoveState moveState;
    boolean canAttack;
    float attackFrequency = 0.5f;
    float attackCD;
    float animeTime;
    Rectangle AttackBound;
    Rectangle bossBound;
    boolean isHitPlayer;
    boolean isHit;
    float wakeupTime;
    float hitTime;
    float bossHealth = 10;
    float hurtCounter;
    public Boss()
    {
        isHit = false;
        hurtCounter = 0;
        moveState = MoveState.IDLE_RIGHT;
        this.currentState = STATE.WAKEUP;
        //animation
        for(int i = 1; i < 14 ; i++)
        {
            walkLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/walk_left/golemSteel - walk" + i + ".png"))));
        }
        for(int i = 1; i < 14 ; i++)
        {
            walkRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/walk_right/golemSteel - walk" + i + ".png"))));
        }
        for(int i = 1; i < 10 ; i++)
        {
            idleLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/idle_left/golemSteel - idle" + i + ".png"))));
        }
        for(int i = 1; i < 10 ; i++)
        {
            idleRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/idle_right/golemSteel - idle" + i + ".png"))));
        }
        for(int i = 1; i < 22; i++)
        {
            attackLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/attack_left/golemSteel - attack" + i + ".png"))));
        }
        for(int i = 1; i < 22; i++)
        {
            attackRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/attack_right/golemSteel - attack" + i + ".png"))));
        }
        for(int i = 1; i < 8; i++)
        {
            hurtLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/hit_left/golemSteel - hit" + i + ".png"))));
        }
        for(int i = 1; i < 8; i++)
        {
            hurtRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/hit_right/golemSteel - hit" + i + ".png"))));
        }
        for(int i = 1; i < 15; i++)
        {
            deathLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/death_left/golemSteel - death" + i + ".png"))));
        }
        for(int i = 1; i < 15; i++)
        {
            deathRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/death_right/golemSteel - death" + i + ".png"))));
        }
        for(int i = 1; i < 14; i++)
        {
            wakeupLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/wakeup_left/golemSteel - wakeup" + i + ".png"))));
        }
        for(int i = 1; i < 14; i++)
        {
            wakeupRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/wakeup_right/golemSteel - wakeup" + i + ".png"))));
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
        attackLeft =  new Animation(0.1f, attackLeftFrames);
        attackRight = new Animation(0.1f, attackRightFrames);
        //hit
        hurtLeft = new Animation(0.2f, hurtLeftFrames);
        hurtRight = new Animation(0.2f, hurtRightFrames);
        //death
        deathLeft = new Animation(0.2f, deathLeftFrames);
        deathRight = new Animation(0.2f, deathRightFrames);
        //wakeUp
        wakeupLeft = new Animation(0.2f, wakeupLeftFrames);
        wakeupRight = new Animation(0.2f, wakeupRightFrames);

        //hit effect
        hitEffect = new Animation(0.19f, hitEffectFrames);

        isRight = true;
        canAttack = false;

        //attack rectangle
        AttackBound = new Rectangle();

        bossBound = new Rectangle(x,y,26,28);
    }


    public void update(Player player){
        bossBound.setPosition(x,y);
        Gdx.app.log("dota: ", "left: " + isCollisionLeft);
        Gdx.app.log("dota: ", "right: " + isCollisionRight);
        Gdx.app.log("dota: ", "top: " + isCollisionTop);
        Gdx.app.log("dota: ", "bot: " + isCollisionBottom);
        if(isCollisionLeft || isCollisionRight)
        {
            this.currentState = STATE.REMOVE;
        }
        if(this.currentState != STATE.ATTACKING)
        {
            AttackBound.set(0,0,0,0);
            isHitPlayer = false;
        }
        Gdx.app.log("checki: ", "cd: " + attackCD);
        float dt = Gdx.graphics.getDeltaTime();
        switch(this.currentState) {
            case WAKEUP:
                if(distanceFrom(player) <= 60)
                {
                    isWeak = true;
                }
                if(isWeak)
                {
                    wakeupTime += dt;
                    if(wakeupLeft.isAnimationFinished(wakeupTime))
                    {
                        this.currentState = STATE.CHASING;
                    }
                }
                break;
            case CHASING:
                if(bossHealth <= 0)
                {
                    this.currentState = STATE.DEATH;
                }
                //check hurt counter
                if(hurtCounter >= 5)
                {
                    this.currentState = STATE.HURTING;
                    hurtCounter = 0;
                }
                //try to move near the player
                if(distanceFrom(player) < 20)
                {
                    Gdx.app.log("rs: ", "player y: " + Math.round(player.getPosition().y));
                    Gdx.app.log("rs: ", "goblin y: " + Math.round(this.y));
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
                break;
            case ATTACKING:
                animeTime += dt;
                if(bossHealth <= 0)
                {
                    this.currentState = STATE.DEATH;
                }

                if(hurtCounter >= 5)
                {
                    this.currentState = STATE.HURTING;
                    hurtCounter = 0;
                }

                if(isRight)
                {
                    //finish attack back to chasing
                    if(attackRight.isAnimationFinished(animeTime))
                    {
                        attackCD = 0;
                        animeTime = 0;
                        this.currentState = STATE.CHASING;
                    }
                    AttackBound.set(x + 16 ,y,32,32);
                    //check if overlap with player when enemy attacking
                    if(player.getBoundingBox().overlaps(AttackBound) && animeTime > 1.3)
                    {
                        if(!isHitPlayer)
                        {
                            Gdx.app.log("attack: ","isOverlap: " + player.getBoundingBox().overlaps(AttackBound));
                            player.playerHealth -= 1;
                            isHitPlayer = true;
                        }
                    }
                }
                else {
                    //finish attack back to chasing
                    if (attackLeft.isAnimationFinished(animeTime)) {
                        attackCD = 0;
                        animeTime = 0;
                        this.currentState = STATE.CHASING;
                    }
                    AttackBound.set(x - 32, y, 32, 32);
                    if (player.getBoundingBox().overlaps(AttackBound) && animeTime > 1.3) {
                        if (!isHitPlayer) {
                            Gdx.app.log("attack: ", "isOverlap: " + player.getBoundingBox().overlaps(AttackBound));
                            player.playerHealth -= 1;
                            isHitPlayer = true;
                        }
                    }
                }
                break;
            case HURTING:
                hurtTime += dt;
                if(isRight)
                {
                    if(hurtRight.isAnimationFinished(hurtTime))
                    {
                        hurtTime = 0;
                        this.currentState = STATE.CHASING;
                    }
                }
                else
                {
                    if(hurtLeft.isAnimationFinished(hurtTime))
                    {
                        hurtTime = 0;
                        this.currentState = STATE.CHASING;
                    }
                }
                break;
            case DEATH:
                deathTime += dt;
                if(isRight)
                {
                    if(deathRight.isAnimationFinished(deathTime))
                    {
                        deathTime = 0;
                        this.currentState = STATE.REMOVE;
                    }
                }
                else
                {
                    if(deathLeft.isAnimationFinished(deathTime))
                    {
                        hurtTime = 0;
                        this.currentState = STATE.REMOVE;
                    }
                }
                break;
            case REMOVE:
                dispose();
                bossBound.set(0,0,0,0);
                break;
            default:
        }
    }
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        renderX = this.x - 40;
        renderY = this.y - 32;
        //render
        switch(this.currentState) {
            case WAKEUP:
                currentFrame = (TextureRegion)(wakeupLeft.getKeyFrame(wakeupTime, true));
                batch.draw(currentFrame,this.x - 40,this.y - 32);
                break;

            case CHASING:
                //use movestate switch render animation
                switch(moveState)
                {
                    case RUN_LEFT:
                        currentFrame = (TextureRegion)(walkLeftAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,renderX,renderY);
                        break;
                    case RUN_RIGHT:
                        currentFrame = (TextureRegion)(walkRightAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,renderX,renderY);
                        break;
                    case IDLE_LEFT:
                        currentFrame = (TextureRegion)(idleLeftAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,renderX,renderY);
                        break;
                    case IDLE_RIGHT:
                        currentFrame = (TextureRegion)(idleRightAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,renderX,renderY);
                        break;
                }
                break;
            case ATTACKING:
                if(isRight)
                {
                    currentFrame = (TextureRegion) (attackRight.getKeyFrame(animeTime, true));
                    batch.draw(currentFrame,renderX,renderY);
                }
                else
                {
                    currentFrame = (TextureRegion) (attackLeft.getKeyFrame(animeTime, true));
                    batch.draw(currentFrame,renderX,renderY);
                }
                break;
            case HURTING:
                if(isRight)
                {
                    currentFrame = (TextureRegion) (hurtRight.getKeyFrame(hurtTime, true));
                    batch.draw(currentFrame,renderX,renderY);
                }
                else
                {
                    currentFrame = (TextureRegion) (hurtLeft.getKeyFrame(hurtTime, true));
                    batch.draw(currentFrame,renderX,renderY);
                }
                break;
            case DEATH:
                if(isRight)
                {
                    currentFrame = (TextureRegion) (deathRight.getKeyFrame(deathTime, true));
                    batch.draw(currentFrame,renderX,renderY);
                }
                else
                {
                    currentFrame = (TextureRegion) (deathLeft.getKeyFrame(deathTime, true));
                    batch.draw(currentFrame,renderX,renderY);
                }
                break;
            default:
        }

        //render hit
        if(isHit)
        {
            hitTime += Gdx.graphics.getDeltaTime();
            currentFrame = (TextureRegion)(hitEffect.getKeyFrame(hitTime, true));
            batch.draw(currentFrame,this.x + 4,this.y + 8);
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
