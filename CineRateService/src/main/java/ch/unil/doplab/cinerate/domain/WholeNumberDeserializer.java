package ch.unil.doplab.cinerate.domain;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Personne 3 : lecture stricte d'un nombre entier dans le corps JSON (releaseYear, duration).
 *
 * Sans lui, JSON-B tronque 2000.5 en 2000 et fait deborder 99999999999 en 1215752191 sans
 * erreur. Ici une valeur qui n'est pas un entier de type int est refusee : le service
 * repond alors 400 (voir GlobalExceptionMapper).
 */
public class WholeNumberDeserializer implements JsonbDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser parser, DeserializationContext context, Type type) {
        BigDecimal value = context.deserialize(BigDecimal.class, parser);
        return toInt(value);
    }

    static Integer toInt(BigDecimal value) {
        if (value == null) {
            return null;
        }
        try {
            // refuse les decimales (2000.5) et les valeurs hors de l'intervalle d'un int
            return value.stripTrailingZeros().intValueExact();
        } catch (ArithmeticException e) {
            throw new JsonbException("Expected a whole number, got " + value.toPlainString());
        }
    }
}
