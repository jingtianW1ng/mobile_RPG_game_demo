package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class DoorAnimation {
    private Animation<TextureRegion> doorAnimation;
    private float stateTime;

    public DoorAnimation() {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i <= 14; i++) {
            String fileName = "Background/tiles/wall/door_anim_opening_f" + i + ".png";
            TextureRegion frame = new TextureRegion(new Texture(fileName));
            frames.add(frame);
        }
        doorAnimation = new Animation<>(0.1f, frames);
        stateTime = 0f;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    public TextureRegion getCurrentFrame() {
        return doorAnimation.getKeyFrame(stateTime, false);
    }


    public void dispose() {
        for (TextureRegion region : doorAnimation.getKeyFrames()) {
            region.getTexture().dispose();
        }
    }
}

