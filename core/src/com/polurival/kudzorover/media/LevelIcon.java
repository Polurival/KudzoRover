package com.polurival.kudzorover.media;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.polurival.kudzorover.LunarRover;

/**
 * Класс для описания иконок уровней
 * <p>
 * Created by Polurival
 * on 08.03.2017.
 */

public class LevelIcon extends Group {

    private int id;
    private Label label;

    private Image lockImg; // изображение замка для закрытых уровней
    private Image bg; // фон для кнопки
    private Image bgDown; // фон нажатой кнопки
    private Image highlightImg; // для подсветки кнопки текущего уровня

    private boolean isHighlighted = false;
    private boolean alphaUp = false;

    public LevelIcon(int id) {
        this.id = id;

        highlightImg = new Image(LunarRover.atlas.findRegion("level_icon_hilite"));
        addActor(highlightImg);
        highlightImg.setVisible(false);

        bg = new Image(LunarRover.atlas.findRegion("level_icon_bg"));
        addActor(bg);
        setSize(bg.getWidth(), bg.getHeight());

        highlightImg.setX((getWidth() - highlightImg.getWidth()) / 2);
        highlightImg.setY((getHeight() - highlightImg.getHeight()) / 2);

        bgDown = new Image(LunarRover.atlas.findRegion("level_icon_bg_down"));
        addActor(bgDown);

        bgDown.setX(bg.getX() + (bg.getWidth() - bgDown.getWidth()) / 2);
        bgDown.setY(bg.getY() + (bg.getHeight() - bgDown.getHeight()) / 2);
        bgDown.setVisible(false);

        // взаимодействия с залоченными уровнями не будет, поэтому актера не добавляем
        lockImg = new Image(LunarRover.atlas.findRegion("level_icon_lock"));
        lockImg.setX((getWidth() - lockImg.getWidth()) / 2);
        lockImg.setY((getHeight() - lockImg.getHeight()) / 2);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = LunarRover.font40;
        style.fontColor = new Color(0x000000ff);

        label = new Label(id + "", style);
        label.setX((getWidth() - label.getWidth()) / 2);
        label.setY((getHeight() - label.getHeight()) / 2);

        setLock(true);

        // слушатель событий корневого элемента, отключая его для дочерних элементов
        addCaptureListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                event.setTarget(LevelIcon.this);
                return true;
            }
        });

        addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                bgDown.setVisible(true);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                bgDown.setVisible(false);
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    public int getId() {
        return id;
    }

    public void setLock(boolean lock) {
        if (lock) {
            label.remove();
            addActor(lockImg);
            setTouchable(Touchable.disabled);
        } else {
            lockImg.remove();
            addActor(label);
            setTouchable(Touchable.enabled);
        }
    }

    public void setHighlight() {
        highlightImg.setVisible(true);
        isHighlighted = true;
    }

    @Override
    public void act(float delta) {
        // анимация мигания подсветки иконки текущего уровня
        if (isHighlighted) {
            float alpha = highlightImg.getColor().a;
            if (alphaUp) {
                alpha += delta * 4;
                if (alpha >= 1) {
                    alpha = 1;
                    alphaUp = false;
                }
            } else {
                alpha -= delta * 4;
                if (alpha < 0) {
                    alpha = 0;
                    alphaUp = true;
                }
            }
            highlightImg.setColor(1, 1, 1, alpha);
        }
        super.act(delta);
    }
}
