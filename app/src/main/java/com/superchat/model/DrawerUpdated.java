package com.superchat.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class DrawerUpdated implements Serializable {

	private boolean isDrawerUpdated = false;
	private boolean isDrawerForceRefresh = false;

	public boolean isDrawerUpdated() {
		return isDrawerUpdated;
	}

	public void setDrawerUpdated(boolean drawerUpdated) {
		isDrawerUpdated = drawerUpdated;
	}

	public boolean isDrawerForceRefresh() {
		return isDrawerForceRefresh;
	}

	public void setDrawerForceRefresh(boolean drawerForceRefresh) {
		isDrawerForceRefresh = drawerForceRefresh;
	}
}