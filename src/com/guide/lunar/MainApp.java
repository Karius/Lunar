package com.guide.lunar;

import android.app.Application;

public class MainApp extends Application {
	private ViolationManager vManager;
	
	public ViolationManager getViolationManager () {
		return vManager;
	}
	public void setViolationManager (ViolationManager vm) {
		vManager = vm;
	}

}
