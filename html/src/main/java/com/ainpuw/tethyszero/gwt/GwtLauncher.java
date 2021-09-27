package com.ainpuw.tethyszero.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.ainpuw.tethyszero.Main;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
		@Override
		public GwtApplicationConfiguration getConfig () {
			// Resizable application, uses available space in browser
			// return new GwtApplicationConfiguration(true);
			// Fixed size application:
			// return new GwtApplicationConfiguration(1000, 1000, true);
			return new GwtApplicationConfiguration(800, 800);

		}

		@Override
		public ApplicationListener createApplicationListener () { 
			return new Main();
		}
}
