package com.alliedpayroll.platform.web;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JavaType;

/**
 * Swagger-core still models types with Jackson 2 while the application runs Jackson 3, so both
 * {@code JavaType}s are accepted.
 *
 * <p>The code's nullness contract becomes the API's: every record component is required unless it
 * is annotated {@code @Nullable} or is an {@code Optional}. Generated clients then get non-optional
 * fields instead of a question mark on everything.
 */
@Component
public class JspecifyRequiredModelConverter implements ModelConverter {

  @Override
  @SuppressWarnings("rawtypes")
  public @Nullable Schema resolve(
      AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
    if (!chain.hasNext()) {
      return null;
    }
    Schema resolved = chain.next().resolve(type, context, chain);
    Class<?> raw = rawClass(type.getType());
    if (resolved == null || raw == null || !raw.isRecord()) {
      return resolved;
    }
    Schema target = definition(resolved, context);
    if (target == null) {
      return resolved;
    }
    for (RecordComponent component : raw.getRecordComponents()) {
      if (isRequired(component) && hasProperty(target, component.getName())) {
        target.addRequiredItem(component.getName());
      }
    }
    return resolved;
  }

  private static boolean isRequired(RecordComponent component) {
    return !component.getAnnotatedType().isAnnotationPresent(Nullable.class)
        && component.getType() != Optional.class;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static boolean hasProperty(Schema schema, String name) {
    Map<String, Schema> properties = schema.getProperties();
    return properties != null && properties.containsKey(name);
  }

  /** Springdoc returns a reference for named models; the properties live in the context. */
  @SuppressWarnings("rawtypes")
  private static @Nullable Schema definition(Schema resolved, ModelConverterContext context) {
    String ref = resolved.get$ref();
    if (ref == null) {
      return resolved;
    }
    String name = ref.substring(ref.lastIndexOf('/') + 1);
    return context.getDefinedModels().get(name);
  }

  private static @Nullable Class<?> rawClass(@Nullable Type type) {
    return switch (type) {
      case JavaType javaType -> javaType.getRawClass();
      case com.fasterxml.jackson.databind.JavaType javaType -> javaType.getRawClass();
      case Class<?> clazz -> clazz;
      case ParameterizedType parameterized when parameterized.getRawType() instanceof Class<?> c ->
          c;
      case null, default -> null;
    };
  }
}
