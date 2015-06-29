package com.rfhkr.cc.desktop;

import com.badlogic.gdx.backends.lwjgl.*;
import com.rfhkr.cc.*;

public class DesktopLauncher {
	public static void main (String... argv) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title         = "Circle Clocker";
		config.width         = 800;
		config.height        = 600;
		config.resizable     = false;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		//config.vSyncEnabled = true;
		new LwjglApplication(new CCMain(), config);
	}
}
