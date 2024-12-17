package org.studyeasy.SpringRestDemo.Util.AppUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AppUtil {
    public static String getPhotoUploadPath(String fileName, long album_id) throws IOException{
        String albumPath = "src\\main\\resources\\static\\uploads\\" + album_id;
        Files.createDirectories(Paths.get(albumPath));
        return new File(albumPath).getAbsolutePath() + "\\" + fileName;
    }
}
