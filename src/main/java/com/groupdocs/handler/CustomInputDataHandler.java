package com.groupdocs.handler;

import com.groupdocs.viewer.config.ServiceConfiguration;
import com.groupdocs.viewer.domain.FileType;
import com.groupdocs.viewer.handlers.InputDataHandler;
import com.groupdocs.viewer.resources.Utils;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author liosha, imy
 */
public class CustomInputDataHandler extends InputDataHandler {
    private HashMap<String, String> fileId2FilePath = new HashMap<String, String>();
    private HashMap<String, String> fileId2FileName = new HashMap<String, String>();
    private String basePath = null;

    public CustomInputDataHandler(ServiceConfiguration serviceConfiguration) {
        basePath = serviceConfiguration.getBasePath() + "/files/";
    }

    @Override
    public HashMap<String, String> getFileList(String directory) {
        File[] files = new File(basePath + directory).listFiles();
        for (File file : files) {
            String fileId = new String(Base64.encodeBase64(file.getName().getBytes())) + Long.toString(file.length());
            fileId2FilePath.put(fileId, file.getAbsolutePath());
            fileId2FileName.put(fileId, file.getName());
        }
        return fileId2FileName;
    }

    @Override
    public InputStream getFile(String guid) {
        try {
            // check if guid is a path to the temporary export file
            return new FileInputStream(guid.contains(".") ? guid : fileId2FilePath.get(guid));
        } catch (FileNotFoundException e){
            return null;
        }
    }

    @Override
    public FileType getFileType(String guid) {
        String fileName = new File(fileId2FileName.get(guid)).getName();
        if (fileName.contains(".")) {
            return FileType.valueOf(fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase());
        }
        return FileType.DIRECTORY;
    }

    @Override
    public String saveFile(InputStream inputStream, String fileName, Integer value) {
        File dest = new File(basePath + fileName);
        OutputStream os = null;
        try {
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            return Utils.encodeData(basePath + fileName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CustomInputDataHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CustomInputDataHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(CustomInputDataHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                if(os != null){
                    os.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(CustomInputDataHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
