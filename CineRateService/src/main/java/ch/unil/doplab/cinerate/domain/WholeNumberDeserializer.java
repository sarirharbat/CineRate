package ch.unil.doplab.cinerate.domain;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.math.BigDecimal;

// Klisman Kodra
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
            return value.stripTrailingZeros().intValueExact();
        } catch (ArithmeticException e) {
            throw new JsonbException("Expected a whole number, got " + value.toPlainString());
        }
    }
}
