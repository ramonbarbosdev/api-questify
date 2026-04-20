package com.api_questify.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.HexFormat;
import java.util.Locale;

public class UtilsGeral {

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public String normalizarTexto(String s) {
        return s == null ? null : s.trim();
    }

    public static String gerarHashPergunta(String pergunta) {
        if (isBlank(pergunta)) {
            return null;
        }

        String textoNormalizado = Normalizer.normalize(pergunta.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^\\p{Alnum}\\s]", " ")
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT)
                .trim();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(textoNormalizado.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponivel", ex);
        }
    }

}
