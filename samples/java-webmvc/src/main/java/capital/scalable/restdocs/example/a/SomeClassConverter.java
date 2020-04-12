package capital.scalable.restdocs.example.a;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SomeClassConverter implements Converter<String, SomeClass> {

    @Override
    public SomeClass convert(String source) {
        return new SomeClass(source);
    }
}
