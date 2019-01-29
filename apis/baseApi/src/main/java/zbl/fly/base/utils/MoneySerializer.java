package zbl.fly.base.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneySerializer extends JsonSerializer<BigDecimal> {

    private static final DecimalFormat MONEY_DECIMAL_FORMAT = new DecimalFormat("0.00");

    @SuppressWarnings("DuplicateThrows")
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(MONEY_DECIMAL_FORMAT.format(value));
    }
}
