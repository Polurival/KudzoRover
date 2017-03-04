package com.polurival.kudzorover.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.polurival.kudzorover.GameCallback;
import com.polurival.kudzorover.LunarRover;

public class DesktopLauncher {

	public DesktopLauncher() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new LunarRover(callback), config);
	}

	public static void main (String[] arg) {
		new DesktopLauncher();
	}

	private static GameCallback callback = new GameCallback() {
		@Override
		public void sendMessage(int message) {
			System.out.println("DesctopLauncher sendMessage: " + message);
		}
	};
}
