package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
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
    Animation hitEffect;
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
    Array<TextureRegion> hitEffectFrames = new Array<>();
    TextureRegion currentFrame;
    float patrolTime = 2;
    float moveCD;
    float stateTime;
    float enemySpeed = 30;
    boolean isWeak = false;
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
    public Boss()
    {
        isHit = false;
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
            hitLeftFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/hit_left/golemSteel - hit" + i + ".png"))));
        }
        for(int i = 1; i < 8; i++)
        {
            hitRightFrames.add(new TextureRegion(new Texture(Gdx.files.internal("Boss/golem_steel/hit_right/golemSteel - hit" + i + ".png"))));
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
        hitLeft = new Animation(0.2f, hitLeftFrames);
        hitRight = new Animation(0.2f, hitRightFrames);
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
        Gdx.app.log("posb: ", "state is: " + player.getBoundingBox().overlaps(bossBound));

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
                else
                {
                    //finish attack back to chasing
                    if(attackLeft.isAnimationFinished(animeTime))
                    {
                        attackCD = 0;
                        animeTime = 0;
                        this.currentState = STATE.CHASING;
                    }
                    AttackBound.set(x - 32,y,32,32);
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
                break;
            default:
        }
    }
    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
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
                        batch.draw(currentFrame,this.x - 40,this.y - 32);
                        break;
                    case RUN_RIGHT:
                        currentFrame = (TextureRegion)(walkRightAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,this.x - 40,this.y - 32);
                        break;
                    case IDLE_LEFT:
                        currentFrame = (TextureRegion)(idleLeftAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,this.x - 40,this.y - 32);
                        break;
                    case IDLE_RIGHT:
                        currentFrame = (TextureRegion)(idleRightAni.getKeyFrame(stateTime, true));
                        batch.draw(currentFrame,this.x - 40,this.y - 32);
                        break;
                }
                break;
            case ATTACKING:
                if(isRight)
                {
                    currentFrame = (TextureRegion) (attackRight.getKeyFrame(animeTime, true));
                    batch.draw(currentFrame,this.x - 40,this.y - 32);
                }
                else
                {
                    currentFrame = (TextureRegion) (attackLeft.getKeyFrame(animeTime, true));
                    batch.draw(currentFrame,this.x - 40,this.y - 32);
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
}
