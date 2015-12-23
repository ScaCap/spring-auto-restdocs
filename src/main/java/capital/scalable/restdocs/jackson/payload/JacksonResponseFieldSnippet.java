package capital.scalable.restdocs.jackson.payload;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;

public class JacksonResponseFieldSnippet extends AbstractJacksonFieldSnippet {

    protected JacksonResponseFieldSnippet() {
        super("response");
    }

    @Override
    protected Type getType(final HandlerMethod method) {
        Class<?> returnType = method.getReturnType().getParameterType();
        if (returnType == ResponseEntity.class) {
            return firstGenericType(method.getReturnType());
        } else if (returnType == Page.class) {
            return firstGenericType(method.getReturnType());
        } else if (returnType == List.class) {
            return new GenericArrayType() {

                @Override
                public Type getGenericComponentType() {
                    return firstGenericType(method.getReturnType());
                }
            };
        } else {
            return returnType;
        }
    }

    @Override
    protected void enrichModel(MvcResult result, Map<String, Object> model) {
        Object handler = result.getHandler();
        final String infoText;
        if (handler != null && handler instanceof HandlerMethod
                && ((HandlerMethod) handler).getReturnType().getParameterType() == Page.class) {
            infoText = "Standard <<overview-pagination,Paging>> response where `content` field"
                    + " is list of following objects:";
        } else {
            infoText = "";
        }
        model.put("paginationInfo", infoText);
    }
}
