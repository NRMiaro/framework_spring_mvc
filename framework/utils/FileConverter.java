package framework.utils;

import java.io.InputStream;

import framework.core.MapFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class FileConverter {
    
    public static MapFile convert(String parameterName, HttpServletRequest req) throws Exception {

        Part filePart = req.getPart(parameterName);
        if (filePart == null) return null;

        String fileName = filePart.getSubmittedFileName();
        if (fileName == null || fileName.isEmpty()) return null;

        MapFile file = new MapFile();
        int dotIndex = fileName.indexOf(".");
        file.setFileName(fileName.substring(0, dotIndex));
        file.setExtension(fileName.substring(dotIndex + 1));
        try (InputStream in = filePart.getInputStream()) {
            file.setBytes(in.readAllBytes());
            return file;
        } catch (Exception e) {
            return null;
        }

    }

}
