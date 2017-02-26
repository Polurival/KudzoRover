package com.polurival.kudzorover.controls;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Polurival
 * on 26.02.2017.
 */

public class CButton extends Group {

    private Image up;
    private Image down;

    private boolean isPressed;

    public CButton(Image up, Image down, float minHeight) {
        this.up = up;
        this.down = down;

        if (up.getHeight() < minHeight) {
            up.setSize(minHeight, minHeight);
            down.setSize(minHeight, minHeight);
        }

        addActor(up);
        addActor(down);

        setSize(down.getWidth(), down.getHeight());

        up.setVisible(true);
        down.setVisible(false);

        addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isPressed = true;
                CButton.this.up.setVisible(false);
                CButton.this.down.setVisible(true);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                CButton.this.up.setVisible(true);
                CButton.this.down.setVisible(false);
                isPressed = false;
            }
        });
    }

    public boolean isPressed() {
        return isPressed;
    }
}
