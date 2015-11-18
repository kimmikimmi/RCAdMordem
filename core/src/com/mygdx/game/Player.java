package com.mygdx.game;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationComponent;
import com.uwsoft.editor.renderer.components.sprite.SpriteAnimationStateComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.security.Key;

/**
 * Initialization logic
 * Iteration logic and disposal logic.
 */
public class Player implements IScript {
    private boolean grounded = false;
    private boolean stopJumpAnimation = true;
    private boolean stopRunAnimation = true;
    private boolean stopDeathAnimation = true;
    private Entity player;
    private static TransformComponent transformComponent;
    private static DimensionsComponent dimensionsComponent;
    private SpriteAnimationComponent spriteAnimationComponent;
    private SpriteAnimationStateComponent spriteAnimationStateComponent;

    private static boolean left = false;
    private static boolean right = false;
    private static boolean jump = false;
    private static boolean shoot = false;
    private static boolean dead = false;
    private  Health health;
    private int jumps = 0;

    private World world;
    public Player(World world) {
        this.world = world;
    }

    private final float gravity = NullConstants.GRAVITY;
    private static Vector2 speed;

    private final float jumpSpeed = NullConstants.PLAYER_JUMP;

    @Override
    public void init(Entity entity) {
        player = entity;
        transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        spriteAnimationComponent = ComponentRetriever.get(entity, SpriteAnimationComponent.class);
        spriteAnimationStateComponent = ComponentRetriever.get(entity, SpriteAnimationStateComponent.class);
        ImmutableArray<Component> allComponents = entity.getComponents();
        speed = new Vector2(33, 0);
    }

    //animations for various states
    private void singleAnimationState(String frameName){
        spriteAnimationStateComponent.set(spriteAnimationComponent.frameRangeMap.get(frameName), 0, Animation.PlayMode.NORMAL);
    }
    private void loopAnimationState(String frameName, int fps){
        spriteAnimationStateComponent.set(spriteAnimationComponent.frameRangeMap.get(frameName), fps, Animation.PlayMode.LOOP);
    }

    @Override
    public void act(float delta) {
        if(!PlatformerTutorial.dead) {
            if (left || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                transformComponent.x -= speed.x * delta;
                transformComponent.scaleX = -1f;
            }
            if (right || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                transformComponent.x += speed.x * delta;
                transformComponent.scaleX = 1f;
            }
            if (jump || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (!(jumps >= NullConstants.NUMBER_JUMPS)) {
                    singleAnimationState("jumping");
                    speed.y = jumpSpeed;
                    grounded = false;
                    jumps++;
                }
                dojump(false);
            } else if (!landed()) {
                stopJumpAnimation = false;
            } else
                jumps = 0;
            if ((Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || shoot) && !landed()) {
                singleAnimationState("jumpshooting");
                stopJumpAnimation = false;
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
                shoot = true;

            } else {
                shoot = false;
            }
            if (((right || left) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT))) && landed() && !stopRunAnimation && (Gdx.input.isKeyPressed(Input.Keys.S) || shoot)) {
                loopAnimationState("runshoot", 13);
                stopRunAnimation = true;
                stopJumpAnimation = false;
            } else if (((right || left) || (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT))) && landed() && !stopJumpAnimation && !(Gdx.input.isKeyPressed(Input.Keys.S) || shoot)) {
                loopAnimationState("walking", 13);
                stopJumpAnimation = true;
                stopRunAnimation = false;
            } else if (!(left || right) && !(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) && landed()) {
                singleAnimationState("standing");
                stopJumpAnimation = false;
                stopRunAnimation = false;
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || shoot)
                    singleAnimationState("standshooting");
            }
        }
        if(PlatformerTutorial.dead) {
            if (stopDeathAnimation)
                singleAnimationState("death");
            stopDeathAnimation = false;
        }
        speed.y += gravity*delta;
        transformComponent.y += speed.y * delta;
        rayCast();
        checkForBodyCollision();
    }

    //called when buttons in uiStage are pressed
    public static void moveLeft(boolean yes) {left = yes;}
    public static void moveRight(boolean yes) {right = yes;}
    public static void dojump(boolean yes) {jump = yes;}
    public static void doshoot(boolean yes) {shoot = yes;}
    public boolean getShoot(){
        return shoot;
    }


    private void checkForBodyCollision(){
        float rayGap = (dimensionsComponent.width) / 2;
        float raySize = 2;

        if(speed.x > 0) return;

        Vector2 rayFrom = new Vector2((transformComponent.y + (dimensionsComponent.height/2)) * PhysicsBodyLoader.getScale(),
                (transformComponent.y + rayGap) * PhysicsBodyLoader.getScale());

        Vector2 rayTo = new Vector2((transformComponent.y + dimensionsComponent.height/2) * PhysicsBodyLoader.getScale(),
                (transformComponent.y - raySize)* PhysicsBodyLoader.getScale());

        world.rayCast(new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                speed.x = 0;
                transformComponent.x = point.x / PhysicsBodyLoader.getScale() + 0.01f;
                return 0;
            }
        }, rayFrom, rayTo);

    }


    private void rayCast() {
        float rayGap = (dimensionsComponent.height) / 2;

        float raySize = -(speed.y+Gdx.graphics.getDeltaTime())*Gdx.graphics.getDeltaTime();


        if(speed.y > 0) return;

        Vector2 rayFrom = new Vector2((transformComponent.x + (dimensionsComponent.width/2)) * PhysicsBodyLoader.getScale(),
                (transformComponent.y + rayGap) * PhysicsBodyLoader.getScale());

        Vector2 rayTo = new Vector2((transformComponent.x + dimensionsComponent.width/2) * PhysicsBodyLoader.getScale(),
                (transformComponent.y - raySize)* PhysicsBodyLoader.getScale());

        world.rayCast(new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                speed.y = 0;
                transformComponent.y = point.y / PhysicsBodyLoader.getScale() + 0.01f;
                grounded = true;
                return 0;
            }
        }, rayFrom, rayTo);
    }
    public boolean landed(){
        return grounded;
    }
    public float getX() {
        return transformComponent.x;
    }
    public float getY() {
        return transformComponent.y;
    }
    public float getWidth() {
        return dimensionsComponent.width;
    }
    public float getHeight() {
        return dimensionsComponent.height;
    }
    public BulletComponent.PLAYER_DIRECTION facingDirection(){
        switch ((int) transformComponent.scaleX){
            case 1 :
                return BulletComponent.PLAYER_DIRECTION.RIGHT_DIRECTION;
            case -1 :
                return BulletComponent.PLAYER_DIRECTION.LEFT_DIRECTION;
            default :
                throw new AssertionError("Invalid direction for player");
        }
    }
    @Override
    public void dispose() {

    }

    public Health getHealthManager(){
        if(this.health == null){
            throw new UnsupportedOperationException("Player does not have a health component.");
        }
        return this.health;
    }

    public void setHealthManager(Health health){
        this.health = health;
    }


}