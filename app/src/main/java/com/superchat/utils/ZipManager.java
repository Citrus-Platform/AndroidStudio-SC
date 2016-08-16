package com.superchat.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
 
 
public class ZipManager { 
  private static final int BUFFER = 2048; 
 
  private String[] _files; 
  private String _zipFile; 
 
  public ZipManager(String[] files, String zipFile) { 
    _files = files; 
    _zipFile = zipFile; 
  } 
 
  public void zip() { 
    try  { 
      BufferedInputStream origin = null; 
      FileOutputStream dest = new FileOutputStream(_zipFile); 
 
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 
 
      byte data[] = new byte[BUFFER]; 
 
      for(int i=0; i < _files.length; i++) { 
        Log.v("Compress", "Adding: " + _files[i]); 
        FileInputStream fi = new FileInputStream(_files[i]); 
        origin = new BufferedInputStream(fi, BUFFER); 
        ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1)); 
        out.putNextEntry(entry); 
        int count; 
        while ((count = origin.read(data, 0, BUFFER)) != -1) { 
          out.write(data, 0, count); 
        } 
        origin.close(); 
      } 
 
      out.close(); 
    } catch(Exception e) { 
      e.printStackTrace(); 
    } 
  } 
 //============================================
  public Vector<String> unzip(String _zipFile, String _targetLocation) {
		//create target location folder if not exist
	  Vector<String> files = new Vector<String>();
	  createDirIfNotExists(_targetLocation, _zipFile);
		try {
			FileInputStream fin = new FileInputStream(_zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				//create dir if required while unzipping
				if (ze.isDirectory()) {
//					dirChecker(ze.getName());
					createDirIfNotExists(_targetLocation, _zipFile);
				} else {
					files.add(ze.getName());
					FileOutputStream fout = new FileOutputStream(_targetLocation + ze.getName());
					for (int c = zin.read(); c != -1; c = zin.read()) {
						fout.write(c);
					}
					zin.closeEntry();
					fout.close();
				}
			}
			zin.close();
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return files;
  	}
  public boolean createDirIfNotExists(String path, String folder_name) {
	    boolean ret = true;
	    File file = new File(path, folder_name);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            Log.e("TravellerLog :: ", "Problem creating folder");
	            ret = false;
	        }
	    }
	    return ret;
	}
} 
