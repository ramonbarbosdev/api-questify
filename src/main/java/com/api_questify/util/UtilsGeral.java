package com.api_questify.util;

public class UtilsGeral {

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public String normalizarTexto(String s) {
        return s == null ? null : s.trim();
    }

}
