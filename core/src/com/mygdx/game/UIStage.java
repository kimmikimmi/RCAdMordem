package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

/**
 * Create the separate stage for the UI components.
 */
public class UIStage extends Stage {




    private int health = 3;

    private CompositeActor retryActor;
    private CompositeActor healthActor;
    private CompositeActor leftbuttonActor;
    private CompositeActor rightbuttonActor;
    private CompositeActor abuttonActor;
    private CompositeActor bbuttonActor;
    private CompositeActor blackActor;
    private CompositeActor winActor;

    public UIStage(IResourceRetriever ir){

        Gdx.input.setInputProcessor(this);
        ProjectInfoVO projectInfo = ir.getProjectVO();

        //create button data
        CompositeItemVO leftbuttondata = projectInfo.libraryItems.get("leftbutton");
        CompositeItemVO rightbuttondata = projectInfo.libraryItems.get("rightbutton");
        CompositeItemVO abuttondata = projectInfo.libraryItems.get("abutton");
        CompositeItemVO bbuttondata = projectInfo.libraryItems.get("bbutton");
        CompositeItemVO health = projectInfo.libraryItems.get("health");
        CompositeItemVO retry = projectInfo.libraryItems.get("retrybutton");
        CompositeItemVO black = projectInfo.libraryItems.get("black");
        CompositeItemVO win = projectInfo.libraryItems.get("winscreen");

        black.width = 256;   // resolution.
        black.height = 160;



        //create actors from button data
        leftbuttonActor = new CompositeActor(leftbuttondata, ir);
        rightbuttonActor = new CompositeActor(rightbuttondata, ir);
        abuttonActor = new CompositeActor(abuttondata, ir);
        bbuttonActor = new CompositeActor(bbuttondata, ir);
        healthActor = new CompositeActor(health, ir);
        retryActor = new CompositeActor(retry, ir);
        blackActor = new CompositeActor(black, ir);
        winActor = new CompositeActor(win, ir);

        leftbuttonActor.setLayerVisibility("Default", false);
        rightbuttonActor.setLayerVisibility("Default", false);
        abuttonActor.setLayerVisibility("Default", false);
        bbuttonActor.setLayerVisibility("Default", false);


        //add buttons to screen
        addActor(leftbuttonActor);
        addActor(rightbuttonActor);
        addActor(abuttonActor);
        addActor(bbuttonActor);
        addActor(healthActor);

        //set actor positions
        leftbuttonActor.setX(0);
        leftbuttonActor.setY(0);
        rightbuttonActor.setX(200);
        rightbuttonActor.setY(2);
        bbuttonActor.setX(400);
        bbuttonActor.setY(3);
        abuttonActor.setX(599);
        abuttonActor.setY(4);
        healthActor.setX(-50);
        healthActor.setY(700);

        //set listeners for buttons
        leftbuttonActor.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Player.moveLeft(true);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    Player.moveLeft(false);
            }
        });

        rightbuttonActor.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Player.moveRight(true);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Player.moveRight(false);

            }
        });
        abuttonActor.addListener(new ClickListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Player.dojump(true);
                return true;
            }
        });
        bbuttonActor.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Player.doshoot(true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Player.doshoot(false);
            }
        });
    }


    public void damagePlayer(int incomingDamage)
    {
        health -= incomingDamage;
        if(health <= 2){
            healthActor.setLayerVisibility("maxHealth", false);
            if(health <= 1) {
                healthActor.setLayerVisibility("twoHealth", false);
                if (health == 0){
                    healthActor.setLayerVisibility("oneHealth", false);
                    gameOver();
                }
            }
        }
    }
    public void gameOver(){
        //clear all buttons
        abuttonActor.clear();
        bbuttonActor.clear();
        rightbuttonActor.clear();
        leftbuttonActor.clear();

        //create retry button
        addActor(retryActor);
        retryActor.setX(275);
        retryActor.setY(300);
        PlatformerTutorial.dead = true;

        retryActor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //reset health
                healthActor.setLayerVisibility("oneHealth", true);
                healthActor.setLayerVisibility("twoHealth", true);
                healthActor.setLayerVisibility("maxHealth", true);

                blackActor.clear();
                retryActor.clear();

                PlatformerTutorial.level(NullConstants.MAIN_SCENE);
                retryActor.clear();
            }
        });
    }
    public void win() {
        addActor(blackActor);
        addActor(winActor);
        winActor.setX(200);
        winActor.setY(-50); 
        gameOver();
    }

    public CompositeActor getHealthActor() {
        return healthActor;
    }
}
