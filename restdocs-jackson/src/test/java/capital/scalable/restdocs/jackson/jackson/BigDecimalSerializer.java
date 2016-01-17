package capital.scalable.restdocs.jackson.jackson;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * Serializer for an external type that cannot be extended.
 */
public class BigDecimalSerializer extends StdScalarSerializer<BigDecimal> {

    public BigDecimalSerializer() {
        super(BigDecimal.class, false);
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeNumber(englishDecimalFormat().format(value));
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException {
        if (visitor != null) {
            visitor.expectNumberFormat(typeHint);
        }
    }

    private DecimalFormat englishDecimalFormat() {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.applyPattern("#,##0.00");
        return df;
    }
}
