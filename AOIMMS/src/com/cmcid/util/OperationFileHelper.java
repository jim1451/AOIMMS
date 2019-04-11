package com.cmcid.util;

import java.io.File;

/**
 * �����ļ�������
 * @author sRoger.
 */
public final class OperationFileHelper {
    /**
     * �ݹ�ɾ���ļ����ļ���
     * @param file    Ҫɾ���ĸ�Ŀ¼
     */
    public static void RecursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }
}
