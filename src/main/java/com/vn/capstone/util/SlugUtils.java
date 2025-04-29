package com.vn.capstone.util;

import java.text.Normalizer;
import java.util.Locale;

public class SlugUtils {

    public static String toSlug(String input) {
        String nowhitespace = input.trim().replaceAll("\\s+", "-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[^\\w-]", "").toLowerCase(Locale.ENGLISH);
        return slug;
    }
}
