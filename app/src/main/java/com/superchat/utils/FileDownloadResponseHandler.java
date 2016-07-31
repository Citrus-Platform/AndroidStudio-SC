package com.superchat.utils;

import android.view.View;

/**
 * @author maheshsonker
 *
 */
public interface FileDownloadResponseHandler {
	void onFileDownloadResposne(View view, int type, byte[] data);
	void onFileDownloadResposne(View view, int[] type, String[] file_urls, String[] file_paths);
}
