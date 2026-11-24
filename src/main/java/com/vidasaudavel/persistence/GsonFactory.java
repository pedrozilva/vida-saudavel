package com.vidasaudavel.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.vidasaudavel.model.Assinatura;
import com.vidasaudavel.model.Plano;
import com.vidasaudavel.model.Profissional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Monta a instância de Gson usada por todos os repositórios, com os adapters necessários. */
public final class GsonFactory {

    private GsonFactory() {
    }

    public static Gson criar() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class,
                        (com.google.gson.JsonSerializer<LocalDate>) (src, t, c) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class,
                        (com.google.gson.JsonDeserializer<LocalDate>) (json, t, c) -> LocalDate.parse(json.getAsString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (com.google.gson.JsonSerializer<LocalDateTime>) (src, t, c) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class,
                        (com.google.gson.JsonDeserializer<LocalDateTime>) (json, t, c) -> LocalDateTime.parse(json.getAsString()))
                .registerTypeHierarchyAdapter(Assinatura.class, new AssinaturaAdapter())
                .registerTypeHierarchyAdapter(Plano.class, new PlanoAdapter())
                .registerTypeHierarchyAdapter(Profissional.class, new ProfissionalAdapter())
                .create();
    }
}
