package com.superchat.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class DrawerUpdated implements Serializable {

	private boolean isDrawerUpdated = false;

	public boolean isDrawerUpdated() {
		return isDrawerUpdated;
	}

	public void setDrawerUpdated(boolean drawerUpdated) {
		isDrawerUpdated = drawerUpdated;
	}
}