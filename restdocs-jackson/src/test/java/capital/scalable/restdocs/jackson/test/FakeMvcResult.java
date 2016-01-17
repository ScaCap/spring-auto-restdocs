package capital.scalable.restdocs.jackson.test;

import java.lang.reflect.Method;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class FakeMvcResult implements MvcResult {

    private final HandlerMethod handlerMethod;

    FakeMvcResult(HandlerMethod handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

    public static MvcResult build(Object bean, Method method) {
        return new FakeMvcResult(new HandlerMethod(bean, method));
    }

    @Override
    public MockHttpServletRequest getRequest() {
        return null;
    }

    @Override
    public MockHttpServletResponse getResponse() {
        return null;
    }

    @Override
    public Object getHandler() {
        return handlerMethod;
    }

    @Override
    public HandlerInterceptor[] getInterceptors() {
        return new HandlerInterceptor[0];
    }

    @Override
    public ModelAndView getModelAndView() {
        return null;
    }

    @Override
    public Exception getResolvedException() {
        return null;
    }

    @Override
    public FlashMap getFlashMap() {
        return null;
    }

    @Override
    public Object getAsyncResult() {
        return null;
    }

    @Override
    public Object getAsyncResult(long l) {
        return null;
    }
}
