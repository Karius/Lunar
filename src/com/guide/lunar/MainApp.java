package com.guide.lunar;

import android.app.Application;

public class MainApp extends Application {
	private ViolationResult vResult;
	
	public ViolationResult getViolationResult () {
		return vResult;
	}
	public void setViolationResult (ViolationResult vr) {
		vResult = vr;
	}

}
