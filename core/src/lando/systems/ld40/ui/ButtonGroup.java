package lando.systems.ld40.ui;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Brian on 12/3/2017.
 */
public class ButtonGroup {

    public Button selected;

    public Array<Button> buttons = new Array<Button>();

    public void add(Button button) {
        buttons.add(button);
        button.buttonGroup = this;
    }

    public void select(Button button) {
        if (selected != null) {
            selected.selected = false;
        }

        selected = button;
        if (button != null) {
            button.selected = true;
        }
    }
}
