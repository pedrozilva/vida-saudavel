package com.vidasaudavel.util;

/** RF02 — cálculo e classificação de IMC. */
public final class ImcUtil {

    private ImcUtil() {
    }

    public static double calcularImc(double pesoKg, double alturaM) {
        if (pesoKg <= 0 || alturaM <= 0) {
            throw new IllegalArgumentException("Peso e altura devem ser maiores que zero.");
        }
        return pesoKg / (alturaM * alturaM);
    }

    public static String classificar(double imc) {
        if (imc < 18.5) {
            return "Abaixo do peso";
        } else if (imc < 25.0) {
            return "Normal";
        } else if (imc < 30.0) {
            return "Sobrepeso";
        } else {
            return "Obesidade";
        }
    }
}
