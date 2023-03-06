package demo.config;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class Config {

//    @InitBinder
//    public void binder3(WebDataBinder webDataBinder) {
//        webDataBinder.registerCustomEditor(String.class,
//                new StringTrimmerEditor(true));
//    }
}

