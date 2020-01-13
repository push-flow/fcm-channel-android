package io.fcmchannel.sdk.util;

import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AttachmentHelper {

    public static final String URI_VIDEO_THUMBNAIL = "https://i.imgur.com/y5Oth3E.png";

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

    public static Pair<String, List<String>> extractMediaUrls(final String text) {
        final Pattern pattern = Pattern.compile(String.format("\n(%s)|\n(%s)", REGEX_IMAGE, REGEX_VIDEO));
        final Matcher matcher = pattern.matcher(text);
        final List<String> urls = new ArrayList<>();
        String textWithoutUrls = text;

        while (matcher.find()) {
            final String url = matcher.group();
            urls.add(url.replace("\n", ""));
            textWithoutUrls = textWithoutUrls.replace(url, "");
        }
        return new Pair<>(textWithoutUrls, urls);
    }

}
