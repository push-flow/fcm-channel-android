package io.fcmchannel.sdk.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AttachmentHelper {

    private static final String REGEX_IMAGE = "(http|https)://.*\\.(jpg|jpeg|png|gif|tif|tiff|bmp)";

    public static boolean isImageUrl(final String url) {
        final Pattern pattern = Pattern.compile(REGEX_IMAGE);
        return pattern.matcher(url).matches();
    }

    public static String firstImageUrl(final String text) {
        final Pattern pattern = Pattern.compile(REGEX_IMAGE);
        final Matcher matcher = pattern.matcher(text);

        return matcher.find() ? matcher.group() : "";
    }

    public static String getFileExtension(final String url) {
        final int index = url.lastIndexOf(".") + 1;
        return index <= url.length() - 1 ? url.substring(index) : "";
    }

}
