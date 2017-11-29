package lando.systems.ld40.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lando.systems.ld40.LudumDare40;
import lando.systems.ld40.utils.Config;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Config.gameWidth;
		config.height = Config.gameHeight;
		config.resizable = Config.resizable;
		new LwjglApplication(new LudumDare40(), config);
	}
}
