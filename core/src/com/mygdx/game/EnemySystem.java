package com.mygdx.game;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

/**
 * Created by Jaden on 13/11/2015.
 */
public class EnemySystem extends EntitySystem { // this class is in charge of Artificial intelligence of Enemy.

    private Player player;
    private EnemyComponent enemyComponent;
    private Engine engine;
    private ImmutableArray<Entity> entities;
    private World world;
    private boolean grounded = false;
    private Vector2 speed;
    private float gravity = -320f;


    public EnemySystem(SceneLoader sceneLoader, Player player){

        this.player = player;
        speed = new Vector2(22,0);
        this.engine = sceneLoader.getEngine();
        this.world = sceneLoader.world;
        entities = engine.getEntitiesFor(Family.all(EnemyComponent.class).get());

    }
    //We must create a component mapper for this system that maps all components of a type to it for
    //management.
   // private ComponentMapper<EnemyComponent> enemyComponentMapper = ComponentMapper.getFor(EnemyComponent.class);
    private ComponentMapper<EnemyComponent> enemyComponentMapper = ComponentMapper.getFor(EnemyComponent.class);

    //Retrieves all platform components that match a type for management by the system.

    //Iterates through entities managed by this system, and processes them in some way.

    @Override
    public void update(float deltaTime) {

        for(Entity entity : entities) {
            enemyComponent = enemyComponentMapper.get(entity);
            TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
            //System.out.println(transformComponent.x);
            if(enemyComponent.originalPosition == null) {
                enemyComponent.originalPosition = new Vector2(transformComponent.x, transformComponent.y);

            }

            float dist = transformComponent.x - player.getX();
            if(Math.abs(dist) <= 30) {
                if(Math.abs(dist) < 5) {
                    //timePassed = 0;

                } else if(dist > 0) {
                    transformComponent.x -= speed.x * deltaTime;
                } else {
                    transformComponent.x += speed.x * deltaTime;
                }
                enemyComponent.originalPosition.x = transformComponent.x;

                enemyComponent.timePassed = 0;
            } else {
                //manage scaleX later.
                enemyComponent.timePassed += deltaTime;
                transformComponent.x = (enemyComponent.originalPosition.x +
                        MathUtils.sin(enemyComponent.timePassed * MathUtils.degreesToRadians * 20f) * 20f);
            }
            speed.y += gravity * deltaTime;
            transformComponent.y += speed.y * deltaTime;

            rayCast(entity);
            checkForBodyCollision(entity);

        }


    }

    private void rayCast(Entity entity) {
        final TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        final DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);
        float rayGap = (dimensionsComponent.height) / 2;

        float raySize = -(speed.y+ Gdx.graphics.getDeltaTime())*Gdx.graphics.getDeltaTime();

        System.out.println(speed.y);
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

    private void checkForBodyCollision(Entity entity){
        final TransformComponent transformComponent = ComponentRetriever.get(entity, TransformComponent.class);
        final DimensionsComponent dimensionsComponent = ComponentRetriever.get(entity, DimensionsComponent.class);

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

}
