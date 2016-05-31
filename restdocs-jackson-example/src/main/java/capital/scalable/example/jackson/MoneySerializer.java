package capital.scalable.example.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.javamoney.moneta.Money;

public class MoneySerializer extends StdSerializer<Money> {
    public MoneySerializer() {
        super(Money.class);
    }

    @Override
    public void serialize(Money value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeString(value.getNumberStripped() + " " + value.getCurrency().getCurrencyCode());
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException {
        if (visitor != null) {
            visitor.expectStringFormat(typeHint);
        }
    }
}