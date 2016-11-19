package com.example.dorin.viaconnect.WebClient.Print;

// Lists all MediaTypes supported by printing service
public class MediaType {
    // TODO test remaining Office types
    public final static String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public final static String DOC = "application/msword";
    public final static String XLS = "application/vnd.ms-excel";
    public final static String PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public final static String PPT = "application/vnd.ms-powerpoint";

    // TODO test remaining OpenOffice types
    public final static String SXW = "application/vnd.sun.xml.writer";
    public final static String SXG = "application/vnd.sun.xml.writer.global";

    // TODO test .epub, .zip ...
    public final static String PDF = "application/pdf";
    public final static String JPG = "image/jpeg";
    public final static String GIF = "image/gif";
    public final static String PNG = "image/png";
    public final static String TIF = "image/tiff";
    public final static String BMP = "image/bmp";
    public final static String TXT = "text/plain";

    public static String[] getAllTypes() {
        return new String[]{ DOCX, DOC, XLS, PPTX, PPT, SXW,
                SXG, PDF, JPG, GIF, PNG, TIF, BMP, TXT };
    }
}