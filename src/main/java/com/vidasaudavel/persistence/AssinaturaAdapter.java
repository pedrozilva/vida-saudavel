package com.vidasaudavel.persistence;

import com.google.gson.*;
import com.vidasaudavel.model.Assinatura;
import com.vidasaudavel.model.AssinaturaBasica;
import com.vidasaudavel.model.AssinaturaVIP;

import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * Serialização polimórfica de Assinatura (classe abstrata) a partir
 * de um campo "tipo" gravado no JSON, já que o Gson não resolve
 * sozinho qual subclasse (AssinaturaBasica/AssinaturaVIP) instanciar.
 */
public class AssinaturaAdapter implements JsonSerializer<Assinatura>, JsonDeserializer<Assinatura> {

    @Override
    public JsonElement serialize(Assinatura src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("tipo", src.getTipo());
        obj.addProperty("valor", src.getValor());
        obj.addProperty("dataInicio", src.getDataInicio().toString());
        obj.addProperty("mensalidadesAtrasadas", src.getMensalidadesAtrasadas());
        return obj;
    }

    @Override
    public Assinatura deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String tipo = obj.get("tipo").getAsString();
        LocalDate dataInicio = LocalDate.parse(obj.get("dataInicio").getAsString());

        Assinatura assinatura = "VIP".equalsIgnoreCase(tipo)
                ? new AssinaturaVIP(dataInicio)
                : new AssinaturaBasica(dataInicio);

        int atrasos = obj.has("mensalidadesAtrasadas") ? obj.get("mensalidadesAtrasadas").getAsInt() : 0;
        for (int i = 0; i < atrasos; i++) {
            assinatura.registrarAtraso();
        }
        return assinatura;
    }
}
