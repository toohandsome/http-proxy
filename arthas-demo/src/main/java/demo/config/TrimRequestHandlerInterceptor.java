package demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TrimRequestHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(PostMapping.class) || method.isAnnotationPresent(PutMapping.class) || method.isAnnotationPresent(PatchMapping.class)) {
                final MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
                for (Object arg : ((HandlerMethod) handler).getMethodParameters()) {
                    if (arg instanceof ServletWebRequest) {
                        ServletWebRequest webRequest = (ServletWebRequest) arg;
                        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
                        Map<String, String[]> modifiedParameters = new HashMap<>(servletRequest.getParameterMap().size());
                        for (Map.Entry<String, String[]> entry : servletRequest.getParameterMap().entrySet()) {
                            String[] values = entry.getValue();
                            if (values != null && values.length > 0) {
                                String[] modifiedValues = new String[values.length];
                                for (int i = 0; i < values.length; i++) {
                                    modifiedValues[i] = values[i].trim();
                                }
                                modifiedParameters.put(entry.getKey(), modifiedValues);
                            } else {
                                modifiedParameters.put(entry.getKey(), values);
                            }
                        }
                        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(servletRequest) {
                            @Override
                            public String getParameter(String name) {
                                String[] values = getParameterValues(name);
                                if (values != null && values.length > 0) {
                                    return values[0];
                                }
                                return null;
                            }

                            @Override
                            public String[] getParameterValues(String name) {
                                return modifiedParameters.get(name);
                            }

                            @Override
                            public Map<String, String[]> getParameterMap() {
                                return modifiedParameters;
                            }
                        };
                        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(requestWrapper);
                        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
                    } else if (arg != null && arg.getClass().isAnnotationPresent(RequestBody.class)) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        String json = objectMapper.writeValueAsString(arg);
                        arg = objectMapper.readValue(json.trim(), arg.getClass());
                    }
                }
            }
        }
        return true;
    }
}