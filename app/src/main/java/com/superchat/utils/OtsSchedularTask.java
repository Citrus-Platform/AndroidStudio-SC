package com.superchat.utils;

import java.util.TimerTask;

public class OtsSchedularTask extends TimerTask {

	private OnSchedularListener iListener;
	private Object iParam;
	private byte mRequestObjNo;
	public void run() {
		if (iListener != null) {
			iListener.onTaskCallback(iParam,mRequestObjNo);
		}
	}

	public OtsSchedularTask(OnSchedularListener aListener, Object aParam, byte mRequestObjNo) {
		this.iListener = aListener;
		this.iParam = aParam;
		this.mRequestObjNo = mRequestObjNo;
	}
}
