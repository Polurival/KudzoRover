package com.polurival.kudzorover.player;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Polurival
 * on 05.03.2017.
 *
 * Для создания обеъкта Box2d
 */

public interface IBody {
    Body createBody(World world);
}
