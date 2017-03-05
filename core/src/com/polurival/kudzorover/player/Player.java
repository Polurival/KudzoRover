package com.polurival.kudzorover.player;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.boontaran.games.ActorClip;
import com.boontaran.marchingSquare.MarchingSquare;
import com.polurival.kudzorover.LunarRover;
import com.polurival.kudzorover.Settings;
import com.polurival.kudzorover.levels.Level;

import java.util.ArrayList;

/**
 * Created by Polurival
 * on 05.03.2017.
 */

public class Player extends ActorClip implements IBody {

    private Image roverImg;
    private Image astronautImg;
    private Image astronautFallImg;
    private Image frontWheelImg;
    private Image rearWheelImg;

    private Group frontWheelCont;
    private Group rearWheelCont;
    private Group astronautFallCont;

    public Body rover;
    public Body frontWheel;
    public Body rearWheel;
    public Body astronaut;

    private Joint frontWheelJoint; // соединение колес и астронавта
    private Joint rearWheelJoint;
    private Joint astroJoint;

    private World world;

    private boolean hasDestroyed = false;
    private boolean destroyOnNextUpdate = false;

    private boolean isTouchGround = true;

    private float jumpImpulse = Settings.JUMP_IMPULSE;
    private float jumpWait = 0;

    private Level level;

    public Player(Level level) {
        this.level = level;

        // актеры добавленные позже будут находиться поверх актеров добавленных ранее
        roverImg = new Image(LunarRover.atlas.findRegion("rover"));
        childs.addActor(roverImg);
        roverImg.setX(-roverImg.getWidth() / 2); // задаем положение текстуры относительно объекта
        roverImg.setY(-15);

        astronautImg = new Image(LunarRover.atlas.findRegion("astronaut"));
        childs.addActor(astronautImg);
        astronautImg.setX(-35);
        astronautImg.setY(20);

        // при ударе о луну астронавт выпадет из лунохода
        astronautFallCont = new Group();
        astronautFallImg = new Image(LunarRover.atlas.findRegion("astronaut_fall"));
        astronautFallCont.addActor(astronautFallImg);
        astronautFallImg.setX(-astronautFallImg.getWidth() / 2);
        astronautFallImg.setY(-astronautFallImg.getHeight() / 2);
    }


    /**
     * Кузов лунохода
     * @param world
     * @return объект rover класса Body библиотеки Box2d, описывает твердое физическое тело в игровом мире
     */
    @Override
    public Body createBody(World world) {
        this.world = world;

        // Кузов
        BodyDef def = new BodyDef(); // Определение тела. BodyDef содержит все данные для построения твердого тела
        def.type = BodyDef.BodyType.DynamicBody;
        def.linearDamping = 0; // линейное затухание. Используется для уменьшения линейной скорости

        // Колеса
        frontWheelCont = new Group();
        frontWheelImg = new Image(LunarRover.atlas.findRegion("front_wheel"));
        frontWheelCont.addActor(frontWheelImg);
        frontWheelImg.setX(-frontWheelImg.getWidth() / 2);
        frontWheelImg.setY(-frontWheelImg.getHeight() / 2);

        // добавляем актера в родительскую группу, где также будут кузов лунохода, заднее колесо и астронавт
        getParent().addActor(frontWheelCont);

        UserData data = new UserData();
        data.actor = frontWheelCont;
        frontWheel.setUserData(data);

        // вращательное сочленение, которое требует определения точек привязки там где тела соединены
        RevoluteJointDef rDef = new RevoluteJointDef();
        // инициализация тела, точек привязки и опорного угла
        rDef.initialize(rover, frontWheel, new Vector2(frontWheel.getPosition()));
        frontWheelJoint = world.createJoint(rDef); // создание сочленения, чтобы связать тела вместе

        // аналогично создаем заднее колесо и сочленение для него
        rearWheelCont = new Group();
        rearWheelImg = new Image(LunarRover.atlas.findRegion("rear_wheel"));
        rearWheelCont.addActor(rearWheelImg);
        rearWheelImg.setX(-rearWheelImg.getWidth() / 2);
        rearWheelImg.setY(-rearWheelImg.getHeight() / 2);

        getParent().addActor(rearWheelCont);

        data = new UserData();
        data.actor = rearWheelCont;
        rearWheel.setUserData(data);

        rDef.initialize(rover, rearWheel, new Vector2(rearWheel.getPosition()));
        rearWheelJoint = world.createJoint(rDef);

        return rover;
    }

    public void touchGround() {
        isTouchGround = true;
    }

    public boolean isTouchedGround() {
        if (jumpWait > 0) {
            return false;
        } else {
            return isTouchGround;
        }
    }

    private Body createWheel(World world, float rad) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.linearDamping = 0;
        def.angularDamping = 1f; // угловое затухание, для снижения угловой скорости

        Body body = world.createBody(def);

        // Определяем некоторое присбособление/конструкцию с набором физических свойств
        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape(); // определяем форму на основании которой будет создана конструкция
        shape.setRadius(rad);

        fDef.shape = shape;
        fDef.restitution = 0.5f; // эластичность
        fDef.friction = 0.4f; // коэффициент трения
        fDef.density = 1; // плотность

        body.createFixture(fDef); // создаем конструкцию и прикрепляем ее к телу body
        shape.dispose();

        return body;
    }

    /**
     * @param regionName текстура модели
     * @return полигон, созданный по текстуре модели, который используется для расчета столкновений
     */
    private float[] traceOutline(String regionName) {
        // у нас 2 модели, требующие расчета столкновений - луноход и астронавт

        Texture bodyOutline = LunarRover.atlas.findRegion(regionName).getTexture();
        TextureAtlas.AtlasRegion reg = LunarRover.atlas.findRegion(regionName); // описывает область упакованного изображения
        int w = reg.getRegionWidth(); // ширина и высота области изображения
        int h = reg.getRegionHeight();
        int x = reg.getRegionX(); // координаты области изображения
        int y = reg.getRegionY();

        bodyOutline.getTextureData().prepare(); // получение пиксельных данных текстуры
        Pixmap allPixmap = bodyOutline.getTextureData().consumePixmap();

        // строим контур изображения
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(allPixmap, 0, 0, x, y, w, h);

        allPixmap.dispose();

        int pixel;

        w = pixmap.getWidth();
        h = pixmap.getHeight();

        int[][] map = new int[w][h];
        for (x = 0; x < w; x++) {
            for (y = 0; y < h; y++) {
                pixel = pixmap.getPixel(x, y);
                if ((pixel & 0x000000ff) == 0) {
                    map[x][y] = 0; // прозрачный
                } else {
                    map[x][y] = 1; // непрозрачный
                }
            }
        }

        pixmap.dispose();

        // создаем полигон
        MarchingSquare ms = new MarchingSquare(map);
        ms.invertY();
        ArrayList<float[]> traces = ms.traceMap();

        float[] polyVertices = traces.get(0);
        return polyVertices;
    }

    private Body createBodyFromTriangles(World world, Array<Polygon> triangles) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.linearDamping = 0;
        Body body = world.createBody(def);

        for (Polygon triangle : triangles) {
            FixtureDef fDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.set(triangle.getTransformedVertices());

            fDef.shape = shape;
            fDef.restitution = 0.3f;
            fDef.density = 1;

            body.createFixture(fDef);
            shape.dispose();
        }

        return body;
    }

    public void onKey(boolean moveFrontKey, boolean moveBackKey) {
        float torque = Settings.WHEEL_TORQUE;
        float maxAV = 18;

        if (moveFrontKey) {
            if (-rearWheel.getAngularVelocity() < maxAV) {
                rearWheel.applyTorque(-torque, true);
            }
            if (-frontWheel.getAngularVelocity() < maxAV) {
                frontWheel.applyTorque(-torque, true);
            }
        }
        if (moveBackKey) {
            if (rearWheel.getAngularVelocity() < maxAV) {
                rearWheel.applyTorque(torque, true);
            }
            if (frontWheel.getAngularVelocity() < maxAV) {
                frontWheel.applyTorque(torque, true);
            }
        }
    }

    public void jumpBack(float value) {
        if (value < 0.2f) {
            value = 0.2f;
        }
        // регулируем положение х чтобы получить эффект прыжка назад
        rover.applyLinearImpulse(0, jumpImpulse * value,
                rover.getWorldCenter().x + 5 / Level.WORLD_SCALE,
                rover.getWorldCenter().y, true);
        isTouchGround = false;
        jumpWait = 0.3f;
    }

    public void jumpForward(float value) {
        if (value < 0.2f) {
            value = 0.2f;
        }
        // регулируем положение х чтобы получить эффект прыжка назад
        rover.applyLinearImpulse(0, jumpImpulse * value,
                rover.getWorldCenter().x - 4 / Level.WORLD_SCALE,
                rover.getWorldCenter().y, true);
        isTouchGround = false;
        jumpWait = 0.3f;
    }

    @Override
    public void act(float delta) {
        if (jumpWait > 0) {
            jumpWait -= delta;
        }

        if (destroyOnNextUpdate) {
            destroyOnNextUpdate = false;

            world.destroyJoint(frontWheelJoint);
            world.destroyJoint(rearWheelJoint);
            world.destroyJoint(astroJoint);

            world.destroyBody(astronaut);
            astronautImg.remove();

            astronautFall();
        }

        super.act(delta);
    }

    // анимация падения астронавта при переварачивании лунохода
    private void astronautFall() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.linearDamping = 0;
        def.angularDamping = 0;

        def.position.x = astronaut.getPosition().x;
        def.position.y = astronaut.getPosition().y;
        def.angle = getRotation() * 3.1416f / 180;
        def.angularVelocity = astronaut.getAngularVelocity();

        Body body = world.createBody(def);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(10 / Level.WORLD_SCALE);

        fDef.shape = shape;
        fDef.restitution = 0.5f;
        fDef.friction = 0.4f;
        fDef.density = 1;
        fDef.isSensor = true; // собирает информацию о контакте объекта с другими объектами

        body.createFixture(fDef);

        body.setLinearVelocity(astronaut.getLinearVelocity()); // задаем линейную скорость

        shape.dispose();

        level.addChild(astronautFallCont); // добавим в уровень изображение падающего астронавта
        astronautFallCont.setPosition(getX(), getY());

        // прикрепляем актера к Box2d телу
        UserData data = new UserData();
        data.actor = astronautFallCont;
        body.setUserData(data);
    }

    public void destroy() {
        if (hasDestroyed) {
            return;
        }
        hasDestroyed = true;
        destroyOnNextUpdate = true;
    }

    public boolean isHasDestroyed() {
        return hasDestroyed;
    }
}
