package com.fgan.azure.fogbowmock.util;

import org.apache.log4j.Logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericBuilder<T> {

    private static final Logger LOGGER = Logger.getLogger(GenericBuilder.class);

    private final Supplier<T> instantiator;

    private List<Consumer<T>> instanceModifiers = new ArrayList<>();

    protected GenericBuilder(Supplier<T> instantiator) {
        this.instantiator = instantiator;
    }

    protected static <T> GenericBuilder<T> of(Supplier<T> instantiator) {
        return new GenericBuilder<T>(instantiator);
    }

    protected <U> GenericBuilder<T> with(BiConsumer<T, U> consumer, U value) {
        Consumer<T> c = instance -> consumer.accept(instance, value);
        this.instanceModifiers.add(c);
        return this;
    }

    public T checkAndBuild() throws GenericBuilderException {
        T object = build();
        checkParametersRequired(object);
        return object;
    }

    private void checkParametersRequired(T object) throws GenericBuilderException {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Required.class)) {
                Object value = getFieldValue(object, field);
                if (value == null) {
                    String message = String.format(GenericBuilderException.FIELD_REQUIRED_MESSAGE,
                            field.getName(), object.getClass().getSimpleName());
                    throw new GenericBuilderException(message);
                }
            }
        }
    }

    private Object getFieldValue(T object, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            LOGGER.warn(String.format("There is an illegal access on field %s", field.getName()), e);
            return null;
        }
    }

    public T build() {
        T value = this.instantiator.get();
        this.instanceModifiers.forEach(modifier -> modifier.accept(value));
        this.instanceModifiers.clear();
        return value;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Required {}

}
