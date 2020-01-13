package io.fcmchannel.sdk.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AttachmentHelper {

    private static final String REGEX_IMAGE = "(http|https)://.*\\.(jpg|jpeg|png|gif|tif|tiff|bmp)";
    private static final String REGEX_VIDEO = "(http|https)://.*\\.(mp4|mkv|wmv|avi|flv)";

    public static String getUrlFileExtension(final String url) {
        final int index = url.lastIndexOf(".") + 1;
        return index <= url.length() - 1 ? url.substring(index) : "";
    }

    public static boolean isImageUrl(final String url) {
        final Pattern pattern = Pattern.compile(REGEX_IMAGE);
        return pattern.matcher(url).matches();
    }

    public static boolean isVideoUrl(final String url) {
        final Pattern pattern = Pattern.compile(REGEX_VIDEO);
        return pattern.matcher(url).matches();
    }

    public static List<String> extractMediaUrls(final String text) {
        final Pattern pattern = Pattern.compile(String.format("(%s)|(%s)", REGEX_IMAGE, REGEX_VIDEO));
        final Matcher matcher = pattern.matcher(text);
        final List<String> urls = new ArrayList<>();

        while (matcher.find()) {
            urls.add(matcher.group());
        }
        return urls;
    }

}
