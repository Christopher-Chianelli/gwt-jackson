package com.fasterxml.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;

/**
 * Shared base class used for anything on which annotations (included
 * within a {@link AnnotationMap}).
 */
public abstract class Annotated {

    protected Annotated() {
    }

    public abstract <A extends Annotation> A getAnnotation(Class<A> acls);

    public abstract boolean hasAnnotation(Class<?> acls);

    /**
     * @since 2.7
     */
    public abstract boolean hasOneOf(Class<? extends Annotation>[] annoClasses);

    /**
     * Fluent factory method that will construct a new instance that uses specified
     * instance annotations instead of currently configured ones.
     */
    public abstract Annotated withAnnotations(AnnotationMap fallback);

    /**
     * Fluent factory method that will construct a new instance that uses
     * annotations from specified {@link Annotated} as fallback annotations
     */
    public final Annotated withFallBackAnnotationsFrom(Annotated annotated) {
        return withAnnotations(AnnotationMap.merge(getAllAnnotations(), annotated.getAllAnnotations()));
    }

    public abstract String getName();

    /**
     * Full generic type of the annotated element; definition
     * of what exactly this means depends on sub-class.
     *
     * @since 2.7
     */
    public abstract JavaType getType();

    /**
     * @deprecated Since 2.7 Use {@link #getType()} instead. To be removed from 2.9
     */
    @Deprecated
    public final JavaType getType(TypeBindings bogus) {
        return getType();
    }

    /**
     * JDK declared generic type of the annotated element; definition
     * of what exactly this means depends on sub-class. Note that such type
     * can not be reliably resolved without {@link TypeResolutionContext}, and
     * as a result use of this method was deprecated in Jackson 2.7: see
     * {@link #getType} for replacement.
     *
     * @deprecated Since 2.7 should instead use {@link #getType()}. To be removed from 2.9
     */
    @Deprecated
    public Type getGenericType() {
        return getRawType();
    }

    /**
     * "Raw" type (type-erased class) of the annotated element; definition
     * of what exactly this means depends on sub-class.
     */
    public abstract Class<?> getRawType();

    /**
     * Accessor that can be used to iterate over all the annotations
     * associated with annotated component.
     * 
     * @since 2.3
     */
    public abstract Iterable<Annotation> annotations();

    /**
     * Internal helper method used to access annotation information;
     * not exposed to developers since instances are mutable.
     */
    protected abstract AnnotationMap getAllAnnotations();

    // Also: ensure we can use #equals, #hashCode

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}