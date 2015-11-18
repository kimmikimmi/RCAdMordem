package com.mygdx.game;

/**
 */
public class NullConstants {
    protected static final String MAIN_SCENE = "MainScene";
    protected static final String TITLE_SCREEN = "TitleScreen";
    protected static final String PLAYER = "player" ;
    protected static final String PLATFORM = "platform";
    protected static final String GAME_OVER = "GameOver";
    protected static final String ENEMY = "enemy";
    protected static final String HOW_TO_PLAY = "howtoplay";
    protected static final String BEGIN_BUTTON = "beginbutton";
    protected static final String BUTTON = "button";
    protected static final String BULLET = "bullet";


    protected static final int GROUND_LEVEL = -15;
    protected static final int DEATH_ZONE = -100;
    protected static final int NUMBER_JUMPS = 2;
    protected static final int VIEWPORT_X = 256;
    protected static final int VIEWPORT_Y = 160;
    protected static final int LEVEL_1_END = 1867;

    protected static final int HEALTH_POINT = 3;

    enum HealthPoints {
        ONE_HEALTH_POINT("maxHealth", 1), TWO_HEALTH_POINT("twoHealth", 2), THREE_HEALTH_POINT("threeHealth", 3);
        private int currentHealth;
        private String uiStateTag;

        HealthPoints(String uiStateTag, int currentHealth){
            this.currentHealth = currentHealth;
            this.uiStateTag = uiStateTag;
        }

        public int getCurrentHealth() {
            return currentHealth;
        }

        public String getUiStateTag() {
            return uiStateTag;
        }
    }

    protected static final float GRAVITY = -120f;
    protected static final float PLAYER_JUMP = 66f;


}
