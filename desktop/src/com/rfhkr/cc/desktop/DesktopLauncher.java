package com.rfhkr.cc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rfhkr.cc.CCMain;

public class DesktopLauncher {
	public static void main (String... arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title        = "Circle Clocker";
		config.width        = 800;
		config.height       = 600;
		config.resizable    = false;
		config.vSyncEnabled = true;
		new LwjglApplication(new CCMain(), config);
	}
}
