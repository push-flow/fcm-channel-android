package io.fcmchannel.sdk.util;

import java.util.regex.Pattern;

public abstract class AttachmentHelper {

    private static final String PREFIX_IMAGE = "image";
    private static final String PREFIX_VIDEO = "video";
    private static final String PREFIX_FILE  = "file";
    private static final String PREFIX_LINK  = "link";

    public static String getContentType(final String url) {
        if (isLink(url)) {
            return PREFIX_LINK;
        }
        final String ext = getExtension(url);
        final String typeFormat = "%s/%s";

        if (isImageUrl(url)) {
            return String.format(typeFormat, PREFIX_IMAGE, ext);
        } else if (isVideoUrl(url)) {
            return String.format(typeFormat, PREFIX_VIDEO, ext);
        } else {
            return String.format(typeFormat, PREFIX_FILE, ext);
        }
    }

    public static boolean isContentTypeLink(final String contentType) {
        return contentType.startsWith(PREFIX_LINK);
    }

    public static boolean isContentTypeFile(final String contentType) {
        return contentType.startsWith(PREFIX_FILE);
    }

    public static boolean isContentTypeImage(final String contentType) {
        return contentType.startsWith(PREFIX_IMAGE);
    }

    public static boolean isContentTypeVideo(final String contentType) {
        return contentType.startsWith(PREFIX_VIDEO);
    }

    public static String getExtension(final String url) {
        final int index = url.lastIndexOf(".") + 1;
        return index <= url.length() - 1 ? url.substring(index) : "";
    }

    public static boolean isLink(final String url) {
        return !isFileUrl(url);
    }

    public static boolean isFileUrl(final String url) {
        final String regex = ".*\\.\\w+";
        final Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(url).matches();
    }

    public static boolean isImageUrl(final String url) {
        final String regex = ".*\\.(jpg|jpeg|png|gif|tif|tiff|bmp)";
        final Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(url).matches();
    }

    public static boolean isVideoUrl(final String url) {
        final String regex = ".*\\.(mp4|m4p|m4v|avi|wmv|ogg)";
        final Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(url).matches();
    }

}
