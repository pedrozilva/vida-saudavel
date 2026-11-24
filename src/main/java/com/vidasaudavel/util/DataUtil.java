package com.vidasaudavel.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/** Formatação e parsing de datas/moeda usados pela interface e pelos relatórios. */
public final class DataUtil {

    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DataUtil() {
    }

    public static String formatarData(LocalDate data) {
        return data == null ? "-" : data.format(FORMATO_DATA);
    }

    public static String formatarDataHora(LocalDateTime dataHora) {
        return dataHora == null ? "-" : dataHora.format(FORMATO_DATA_HORA);
    }

    public static String formatarMoeda(double valor) {
        return String.format(new Locale("pt", "BR"), "R$ %.2f", valor);
    }

    /** @throws java.time.format.DateTimeParseException se o texto não estiver em dd/MM/yyyy */
    public static LocalDate parseData(String texto) {
        return LocalDate.parse(texto.trim(), FORMATO_DATA);
    }

    public static boolean dataValida(String texto) {
        try {
            parseData(texto);
            return true;
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }
}
