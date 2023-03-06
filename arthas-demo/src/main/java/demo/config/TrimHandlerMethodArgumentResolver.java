//package demo.config;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.core.MethodParameter;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.http.HttpInputMessage;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.GenericHttpMessageConverter;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.util.Assert;
//import org.springframework.web.HttpMediaTypeNotSupportedException;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.context.request.ServletWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class TrimHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        return parameter.hasParameterAnnotation(RequestBody.class);
//    }
//
//    @Override
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//        Object obj = readWithMessageConverters(webRequest, parameter, parameter.getNestedGenericParameterType());
//        if (obj instanceof String) {
//            return ((String) obj).trim();
//        }
//        if (obj instanceof Map) {
//            Map<String, Object> map = (Map<String, Object>) obj;
//            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                Object value = entry.getValue();
//                if (value instanceof String) {
//                    entry.setValue(((String) value).trim());
//                }
//            }
//        }
//        return obj;
//    }
//
//    private Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter methodParam, Type paramType) throws IOException, HttpMediaTypeNotSupportedException {
//        ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
//        HttpServletRequest servletRequest = servletWebRequest.getRequest();
//        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(servletRequest);
//        Object arg = readWithMessageConverters(inputMessage, methodParam, paramType);
//        return arg;
//    }
//
//    private Object readWithMessageConverters(HttpInputMessage inputMessage, MethodParameter methodParam, Type paramType) throws IOException, HttpMediaTypeNotSupportedException {
//        MediaType contentType = inputMessage.getHeaders().getContentType();
//        Class<?> contextClass = methodParam.getContainingClass();
//        Assert.state(contextClass != null, "No context class");
//        List<HttpMessageConverter<?>> converters = new ArrayList<>(new RestTemplate().getMessageConverters());
//        for (HttpMessageConverter<?> converter : converters) {
//            if (converter instanceof AbstractJackson2HttpMessageConverter) {
//                ((AbstractJackson2HttpMessageConverter) converter).setObjectMapper(new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL));
//            }
//        }
//        GenericHttpMessageConverter<Object> messageConverter = null;
//        if (paramType instanceof Class) {
//            messageConverter = (GenericHttpMessageConverter<Object>) getMessageConverter(converters, (Class<?>) paramType);
//        }
//        if (messageConverter == null) {
//            messageConverter = (GenericHttpMessageConverter<Object>) getMessageConverter(converters, null, contentType);
//        }
//        if (messageConverter == null) {
//            throw new HttpMediaTypeNotSupportedException(contentType, converters);
//        }
//        return messageConverter.read(methodParam.getNestedGenericParameterType(), contextClass, inputMessage);
//    }
//
//    private HttpMessageConverter<?> getMessageConverter(List<HttpMessageConverter<?>> converters, Class<?> clazz, MediaType mediaType) {
//        for (HttpMessageConverter<?> converter : converters) {
//            if (clazz != null) {
//                if (converter.canRead(clazz, mediaType)) {
//                    return converter;
//                }
//            } else {
//                if (converter.canRead(Map.class, mediaType)) {
//                    return converter;
//                }
//            }
//        }
//        return null;
//    }
//
//
//}