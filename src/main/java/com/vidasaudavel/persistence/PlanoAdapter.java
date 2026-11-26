package com.vidasaudavel.persistence;

import com.google.gson.*;
import com.vidasaudavel.model.Objetivo;
import com.vidasaudavel.model.Plano;
import com.vidasaudavel.model.PlanoNutricao;
import com.vidasaudavel.model.PlanoTreino;

import java.lang.reflect.Type;

/** Serialização polimórfica de Plano (PlanoTreino / PlanoNutricao) via campo "tipo". */
public class PlanoAdapter implements JsonSerializer<Plano>, JsonDeserializer<Plano> {

    @Override
    public JsonElement serialize(Plano src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("tipo", src.getTipo());
        obj.addProperty("objetivo", src.getObjetivo().name());

        if (src instanceof PlanoTreino treino) {
            JsonArray exercicios = new JsonArray();
            treino.getExercicios().forEach(exercicios::add);
            obj.add("exercicios", exercicios);
        } else if (src instanceof PlanoNutricao nutricao) {
            obj.addProperty("dietaDiaria", nutricao.getDietaDiaria());
        }
        return obj;
    }

    @Override
    public Plano deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String tipo = obj.get("tipo").getAsString();
        Objetivo objetivo = Objetivo.valueOf(obj.get("objetivo").getAsString());

        if ("PlanoNutricao".equals(tipo)) {
            PlanoNutricao plano = new PlanoNutricao(objetivo);
            if (obj.has("dietaDiaria")) {
                plano.setDietaDiaria(obj.get("dietaDiaria").getAsString());
            }
            return plano;
        } else {
            PlanoTreino plano = new PlanoTreino(objetivo);
            if (obj.has("exercicios")) {
                obj.getAsJsonArray("exercicios").forEach(e -> plano.adicionarExercicio(e.getAsString()));
            }
            return plano;
        }
    }
}
