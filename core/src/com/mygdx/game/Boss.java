package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
public class Boss extends Enemies{
    Animation walkLeftAni;
    Animation walkRightAni;
    Animation idleLeftAni;
    Animation idleRightAni;
    Animation attackLeft;
    Animation attackRight;
    Animation hitLeft;
    Animation hitRight;
    Animation deathLeft;
    Animation deathRight;
    Animation wakeupLeft;
    Animation wakeupRight;
    Array<TextureRegion> walkLeftFrames = new Array<>();
    Array<TextureRegion> walkRightFrames = new Array<>();
    Array<TextureRegion> idleLeftFrames = new Array<>();
    Array<TextureRegion> idleRightFrames = new Array<>();
    Array<TextureRegion> attackLeftFrames = new Array<>();
    Array<TextureRegion> attackRightFrames = new Array<>();
    Array<TextureRegion> hitLeftFrames = new Array<>();
    Array<TextureRegion> hitRightFrames = new Array<>();
    Array<TextureRegion> deathLeftFrames = new Array<>();
    Array<TextureRegion> deathRightFrames = new Array<>();
    Array<TextureRegion> wakeupLeftFrames = new Array<>();
    Array<TextureRegion> wakeupRightFrames = new Array<>();
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
    Rectangle truePos;
    MoveState moveState;
    boolean canAttack;
    float attackFrequency = 0.5f;
    float attackCD;
    float animeTime;
    Rectangle AttackBound;
    boolean isHit;

    public Boss()
    {
        moveState = MoveState.IDLE_RIGHT;
        this.currentState = STATE.PATROLLING;
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
        stateTime = 0.0f;
        //enemy move animation
        walkLeftAni = new Animation(0.25f, walkLeftFrames);
        walkRightAni = new Animation(0.25f, walkRightFrames);
        idleLeftAni = new Animation(0.5f, idleLeftFrames);
        idleRightAni = new Animation(0.5f, idleRightFrames);
        //attack effect
        attackLeft =  new Animation(0.2f, attackLeftFrames);
        attackRight = new Animation(0.2f, attackRightFrames);


        isRight = true;
        canAttack = false;

        //attack rectangle
        AttackBound = new Rectangle();
        truePos = new Rectangle(140,120,16,16);
    }

    public Vector2 getBossPosition() {
        float currentX = truePos.x;
        float currentY = truePos.y;
        return new Vector2(currentX, currentY);
    }

    public float getBossAngle(Vector2 target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - this.getBossPosition().y, target.x - this.getBossPosition().x));

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

    public boolean canBossSeePlayer(Player player) {
        float angle = this.getBossAngle(player.getPosition());
        if(isRight)
        {
            if (player.getPosition().x > getBossPosition().x) {
                return angle <= 45 || angle >= 315;
            }
        }
        else
        {
            if (player.getPosition().x < getBossPosition().x) {
                return angle >= 135 && angle <= 225;
            }
        }
        return false;
    }
    public float bossDistanceFrom(Player player) {
        return getBossPosition().dst(player.getPosition());
    }
    public void update(Player player){
        if(this.currentState != STATE.ATTACKING)
        {
            AttackBound.set(0,0,0,0);
            isHit = false;
        }
        Gdx.app.log("pos: ", "player y: " + player.characterY);
        Gdx.app.log("pos: ", "boss y: " + truePos.y);
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
                        truePos.x += enemySpeed * dt;
                        moveState = MoveState.RUN_RIGHT;
                    }
                    else
                    {
                        truePos.x -= enemySpeed * dt;
                        moveState = MoveState.RUN_LEFT;
                    }
                }
                //check if player close enemy and can see player
                if(bossDistanceFrom(player) <= 70)
                {
                    if(canBossSeePlayer(player))
                    {
                        this.currentState = STATE.CHASING;
                    }
                }
                break;
            case CHASING:
                if(bossDistanceFrom(player) > 70)
                {
                    this.currentState = STATE.PATROLLING;
                }
                else
                {
                    //try to move near the player
                    if(bossDistanceFrom(player) < 20)
                    {
                        //only if player y == enemy y
                        if(Math.round(truePos.y) >= Math.round(player.getPosition().y - 1)
                                && Math.round(truePos.y) <= Math.round(player.getPosition().y + 1))
                        {
                            //Gdx.app.log("bs: ","distance: "  + distanceFrom(player));
                            if (this.getPosition().x < player.getPosition().x) {
                                if (bossDistanceFrom(player) <= 17) {
                                    truePos.x -= enemySpeed * dt;
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
                                Gdx.app.log("bs: ","here2: "  + bossDistanceFrom(player));
                                if(bossDistanceFrom(player) <= 17)
                                {
                                    truePos.x += enemySpeed * dt;
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
                            truePos.x += enemySpeed * dt;
                            moveState = MoveState.RUN_RIGHT;
                            isRight = true;
                            attackCD = 0;
                        }
                        if (this.getPosition().x > player.getPosition().x)
                        {
                            truePos.x -= enemySpeed * dt;
                            moveState = MoveState.RUN_LEFT;
                            isRight = false;
                            attackCD = 0;
                        }
                    }
                    if (this.getPosition().y < player.getPosition().y)
                    {
                        truePos.y += enemySpeed * dt;
                    }
                    if (this.getPosition().y > player.getPosition().y)
                    {
                       truePos.y -= enemySpeed * dt;
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
                    if(attackRight.isAnimationFinished(animeTime))
                    {
                        attackCD = 0;
                        animeTime = 0;
                        this.currentState = STATE.CHASING;
                    }
                    AttackBound.set(truePos.x + 17, truePos.y, 16,16);
                    //check if overlap with player when enemy attacking
                    if(player.getBoundingBox().overlaps(AttackBound) && animeTime > 0.3)
                    {
                        if(!isHit)
                        {
                            Gdx.app.log("attack: ","isOverlap: " + player.getBoundingBox().overlaps(AttackBound));
                            //player.playerHealth -= 1;
                            isHit = true;
                        }
                    }
                    moveState = MoveState.IDLE_RIGHT;
                }
                else
                {
                    //finish attack back to chasing
                    if(attackLeft.isAnimationFinished(animeTime))
                    {
                        attackCD = 0;
                        animeTime = 0;
                        this.currentState = STATE.CHASING;
                    }
                    AttackBound.set(truePos.x - 17, truePos.y, 16,16);
                    if(player.getBoundingBox().overlaps(AttackBound) && animeTime > 0.3)
                    {
                        if(!isHit)
                        {
                            Gdx.app.log("attack: ","isOverlap: " + player.getBoundingBox().overlaps(AttackBound));
                            //player.playerHealth -= 1;
                            isHit = true;
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

                //use movestate switch render animation
                switch(moveState)
                {
                    case RUN_LEFT:
                        currentFrame = (TextureRegion)(walkLeftAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,truePos.x,truePos.y);
                        break;
                    case RUN_RIGHT:
                        currentFrame = (TextureRegion)(walkRightAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,truePos.x,truePos.y);
                        break;
                    case IDLE_LEFT:
                        currentFrame = (TextureRegion)(idleLeftAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,truePos.x,truePos.y);
                        break;
                    case IDLE_RIGHT:
                        currentFrame = (TextureRegion)(idleRightAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,truePos.x,truePos.y);
                        break;
                }
                break;
            case ATTACKING:
                switch(moveState) {
                    case IDLE_LEFT:
                        currentFrame = (TextureRegion) (idleLeftAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,truePos.x,truePos.y);
                        break;
                    case IDLE_RIGHT:
                        currentFrame = (TextureRegion) (idleRightAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,truePos.x,truePos.y);
                        break;
                }
                if(isRight)
                {
                    currentFrame = (TextureRegion) (attackRight.getKeyFrame(animeTime, true));
                    batch.draw(currentFrame,truePos.x,truePos.y);
                }
                else
                {
                    currentFrame = (TextureRegion) (attackLeft.getKeyFrame(animeTime, true));
                    batch.draw(currentFrame,truePos.x,truePos.y);
                }
                break;
            default:
        }
    }

    public void dispose() {

    }
}
