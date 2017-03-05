package com.polurival.kudzorover.levels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.boontaran.games.StageGame;

/**
 * Created by Polurival
 * on 05.03.2017.
 */

public class Level extends StageGame {

    public static final float WORLD_SCALE = 30.0f;

    private String directory;

    public Level(String directory) {
        this.directory = directory;
    }

    /**
     * добавляет актера на 2d сцену
     * @param actor
     */
    public void addChild(Actor actor) {
        stage.addActor(actor);
    }

    public void addChild(Actor actor, float x, float y) {
        addChild(actor);
        actor.setX(x);
        actor.setY(y);
    }
}
