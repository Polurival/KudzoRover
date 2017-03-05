package com.polurival.kudzorover.player;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Содержит данные Box2d тела
 *
 * Created by Polurival
 * on 05.03.2017.
 */

public class UserData {

    public Actor actor;
    public String name = "";

    public UserData() {
    }

    public UserData(Actor actor, String name) {
        this.actor = actor;
        this.name = name;
    }
}
