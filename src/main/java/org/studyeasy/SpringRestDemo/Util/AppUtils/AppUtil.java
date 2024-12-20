package org.studyeasy.SpringRestDemo.Util.AppUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

public class AppUtil {
    public static String getPhotoUploadPath(String fileName, String folderName, long album_id) throws IOException{
        String albumPath = "src\\main\\resources\\static\\uploads\\" + album_id + "\\" + folderName;
        Files.createDirectories(Paths.get(albumPath));
        return new File(albumPath).getAbsolutePath() + "\\" + fileName;
    }

    public static BufferedImage getThumbnail(MultipartFile originalFile, int width) throws IOException{
        BufferedImage thumbImg = null;
        BufferedImage img = ImageIO.read(originalFile.getInputStream());

        thumbImg = Scalr.resize(img,Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, width, Scalr.OP_ANTIALIAS);
        return thumbImg;
    }
}
