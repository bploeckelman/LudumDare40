package lando.systems.ld40.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Utils {

    public static Color hsvToRgb(float hue, float saturation, float value, Color outColor) {
        if (outColor == null) outColor = new Color();

        int h = (int) (hue * 6);
        h = h % 6;
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0: outColor.set(value, t, p, 1f); break;
            case 1: outColor.set(q, value, p, 1f); break;
            case 2: outColor.set(p, value, t, 1f); break;
            case 3: outColor.set(p, q, value, 1f); break;
            case 4: outColor.set(t, p, value, 1f); break;
            case 5: outColor.set(value, p, q, 1f); break;
            default: throw new GdxRuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
        return outColor;
    }
}
