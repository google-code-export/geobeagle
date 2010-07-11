package com.google.code.geobeagle.bcaching;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class BCachingAnnotations {
    private BCachingAnnotations() {
        
    }
    @BindingAnnotation @Target( { FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface DetailsReaderAnnotation { }

    @BindingAnnotation @Target( { FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
    public static @interface CacheListAnnotation { }

}
