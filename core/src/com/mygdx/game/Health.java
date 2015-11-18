package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

/**
 * Created by Jaden on 17/11/2015.
 */
public class Health {
    final int healthPoint = NullConstants.HEALTH_POINT;
    int currentHealth = healthPoint;
    CompositeActor healthActor;
    Player player;
    Engine engine;
    UIStage uiStage;

    public Health(CompositeActor healthActor, Player player, SceneLoader sceneLoader, UIStage uiStage) {
        this.healthActor =  healthActor;
        this.player = player;
        this.engine = sceneLoader.getEngine();
        this.uiStage = uiStage;
    }


    public void getDamaged(int incomingDamage) {

        uiStage.damagePlayer(incomingDamage);

    }

}
