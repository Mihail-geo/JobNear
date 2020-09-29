package com.example.demo.aop;

import com.example.demo.model.enumeration.Module;
import com.example.demo.model.enumeration.ModuleScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Protected {
    Module module() default Module.CARRIERS;

    ModuleScope scope() default ModuleScope.READ;
}
