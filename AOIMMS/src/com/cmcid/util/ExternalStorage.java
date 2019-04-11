package com.cmcid.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import android.os.Environment;

public class ExternalStorage {

    public static final String SD_CARD = "sdCard";
    public static final String EXTERNAL_SD_CARD = "externalSdCard";

//Example usage:    
//    Map<String, File> externalLocations = ExternalStorage.getAllStorageLocations();
//    File sdCard = externalLocations.get(ExternalStorage.SD_CARD);
//    File externalSdCard = externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);

    /**
     * @return True if the external storage is available. False otherwise.
     */
    public static boolean isAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/";
    }

    /**
     * @return True if the external storage is writable. False otherwise.
     */
    public static boolean isWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;

    }

    /**
     * @return A map of all storage locations available
     */
    public static Map<String, File> getAllStorageLocations() {
        Map<String, File> map = new HashMap<String, File>(10);

        List<String> mMounts = new ArrayList<String>(10);
        List<String> mVold = new ArrayList<String>(10);
        mMounts.add("/mnt/sdcard");
        mVold.add("/mnt/sdcard");

        try {
            File mountFile = new File("/proc/mounts");
            if(mountFile.exists()){
                Scanner scanner = new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("/dev/block/vold/")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[1];

                        // don't add the default mount path
                        // it's already in the list.
                        if (!element.equals("/mnt/sdcard"))
                            mMounts.add(element);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File voldFile = new File("/system/etc/vold.fstab");
            if(voldFile.exists()){
                Scanner scanner = new Scanner(voldFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("dev_mount")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[2];

                        if (element.contains(":"))
                            element = element.substring(0, element.indexOf(":"));
                        if (!element.equals("/mnt/sdcard"))
                            mVold.add(element);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < mMounts.size(); i++) {
            String mount = mMounts.get(i);
            if (!mVold.contains(mount))
                mMounts.remove(i--);
        }
        mVold.clear();

        List<String> mountHash = new ArrayList<String>(10);

        for(String mount : mMounts){
            File root = new File(mount);
            if (root.exists() && root.isDirectory() && root.canWrite()) {
                File[] list = root.listFiles();
                String hash = "[";
                if(list!=null){
                    for(File f : list){
                        hash += f.getName().hashCode()+":"+f.length()+", ";
                    }
                }
                hash += "]";
                if(!mountHash.contains(hash)){
                    String key = SD_CARD + "_" + map.size();
                    if (map.size() == 0) {
                        key = SD_CARD;
                    } else if (map.size() == 1) {
                        key = EXTERNAL_SD_CARD;
                    }
                    mountHash.add(hash);
                    map.put(key, root);
                }
            }
        }

        mMounts.clear();

        if(map.isEmpty()){
                 map.put(SD_CARD, Environment.getExternalStorageDirectory());
        }
        return map;
    }

	/**
	 * * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息 * @return
	 */
	private static ArrayList<String> getDevMountList() {
		// String[] toSearch = FileUtils.readFile("/etc/vold.fstab").split(" ");
		ArrayList<String> out = new ArrayList<String>();
		File voldFile = new File("/system/etc/vold.fstab");
		try {
			if (voldFile.exists()) {
				Scanner scanner = new Scanner(voldFile);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line.startsWith("dev_mount")) {
						String[] lineElements = line.split(" ");
						String element = lineElements[2];

						if (element.contains(":"))
							element = element
									.substring(0, element.indexOf(":"));
						if (!element.equals("/mnt/sdcard"))
							out.add(element);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	/**
	 * 获取扩展SD卡存储目录 * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录 否则：返回内置SD卡目录 *
	 * 
	 * @return
	 */
	public static String getExternalSdCardPath() {
		// 判断外置SD卡是否挂载：
		// if(Environment.getStorageState(android.os.Environment.STORAGE_PATH_SD2).equals(Environment.MEDIA_MOUNTED))
		// {
		// 为true的话，外置sd卡存在
		// }

		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) { // 判断sd卡是否存在) {
			File sdCardFile = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath());
			return sdCardFile.getAbsolutePath();
		}
		String path = null;
		File sdCardFile = null;
		ArrayList<String> devMountList = getDevMountList();
		for (String devMount : devMountList) {
			File file = new File(devMount);
			if (file.isDirectory() && file.canWrite()) {
				path = file.getAbsolutePath();
				String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss")
						.format(new Date());
				File testWritable = new File(path, "test_" + timeStamp);
				if (testWritable.mkdirs()) {
					testWritable.delete();
				} else {
					path = null;
				}
			}
		}
		if (path != null) {
			sdCardFile = new File(path);
			return sdCardFile.getAbsolutePath();
		}
		return null;
	}

}
// Map<String, File> externalLocations =
// ExternalStorage.getAllStorageLocations();
// File sdCard = externalLocations.get(ExternalStorage.SD_CARD);
// File externalSdCard =
// externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);
// sSavePathFileName = AdvertUpdater.getExternalSdCardPath();
