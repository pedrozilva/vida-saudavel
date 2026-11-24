package com.vidasaudavel.persistence;

import com.google.gson.*;
import com.vidasaudavel.model.Nutricionista;
import com.vidasaudavel.model.PersonalTrainer;
import com.vidasaudavel.model.Profissional;

import java.lang.reflect.Type;

/** Serialização polimórfica de Profissional (PersonalTrainer / Nutricionista) via campo "tipo". */
public class ProfissionalAdapter implements JsonSerializer<Profissional>, JsonDeserializer<Profissional> {

    @Override
    public JsonElement serialize(Profissional src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", src.getId());
        obj.addProperty("nome", src.getNome());
        obj.addProperty("cpf", src.getCpf());
        obj.addProperty("telefone", src.getTelefone());
        obj.addProperty("email", src.getEmail());

        if (src instanceof PersonalTrainer pt) {
            obj.addProperty("tipo", "PersonalTrainer");
            obj.addProperty("modalidade", pt.getModalidade());
        } else if (src instanceof Nutricionista nutri) {
            obj.addProperty("tipo", "Nutricionista");
            obj.addProperty("registroProfissional", nutri.getRegistroProfissional());
        }
        return obj;
    }

    @Override
    public Profissional deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        int id = obj.get("id").getAsInt();
        String nome = obj.get("nome").getAsString();
        String cpf = obj.get("cpf").getAsString();
        String telefone = obj.has("telefone") && !obj.get("telefone").isJsonNull() ? obj.get("telefone").getAsString() : null;
        String email = obj.has("email") && !obj.get("email").isJsonNull() ? obj.get("email").getAsString() : null;
        String tipo = obj.get("tipo").getAsString();

        if ("Nutricionista".equals(tipo)) {
            String registro = obj.has("registroProfissional") ? obj.get("registroProfissional").getAsString() : "";
            return new Nutricionista(id, nome, cpf, telefone, email, registro);
        } else {
            String modalidade = obj.has("modalidade") ? obj.get("modalidade").getAsString() : "";
            return new PersonalTrainer(id, nome, cpf, telefone, email, modalidade);
        }
    }
}
