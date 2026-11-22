package com.alliedpayroll.tenant.infrastructure.profiles;

import com.alliedpayroll.tenant.application.ProfileTemplates;
import com.alliedpayroll.tenant.domain.ProfileTemplate;
import com.alliedpayroll.tenant.domain.TenantModule;
import com.alliedpayroll.tenant.domain.TerminologyPack;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * The profile packs shipped in {@code profiles/}: one {@code CODE.vN.json} per published version,
 * plus {@code base-terminology.json}. Read once at startup; a malformed pack stops the application
 * rather than provisioning tenants from it.
 */
@Component
class ClasspathProfileTemplates implements ProfileTemplates {

  static final String TEMPLATES = "classpath:profiles/*.v*.json";
  static final String BASE_TERMINOLOGY = "classpath:profiles/base-terminology.json";

  /** A pack as written in the file, before it becomes a {@link ProfileTemplate}. */
  record TemplateFile(
      String code,
      int version,
      String name,
      Map<String, Boolean> modules,
      Map<String, Map<String, String>> terminology) {

    ProfileTemplate toTemplate() {
      Map<TenantModule, Boolean> byModule = new EnumMap<>(TenantModule.class);
      modules.forEach((module, enabled) -> byModule.put(TenantModule.valueOf(module), enabled));
      return new ProfileTemplate(code, version, name, byModule, new TerminologyPack(terminology));
    }
  }

  /** A published pack with the JSON it was read from, which the reconciler stores. */
  record LoadedTemplate(ProfileTemplate template, JsonNode payload) {}

  private static final TypeReference<Map<String, String>> STRING_MAP = new TypeReference<>() {};

  private final List<LoadedTemplate> loaded;
  private final TerminologyPack base;

  ClasspathProfileTemplates(ObjectMapper json) {
    var resolver = new PathMatchingResourcePatternResolver();
    try {
      this.loaded =
          Arrays.stream(resolver.getResources(TEMPLATES))
              .map(resource -> load(json, resource))
              .toList();
      this.base =
          new TerminologyPack(
              read(json, resolver.getResource(BASE_TERMINOLOGY))
                  .propertyStream()
                  .collect(
                      Collectors.toMap(
                          Map.Entry::getKey,
                          entry -> json.convertValue(entry.getValue(), STRING_MAP))));
    } catch (IOException e) {
      throw new UncheckedIOException("could not list profile packs", e);
    }
  }

  @Override
  public Optional<ProfileTemplate> latest(String code) {
    return templates().stream()
        .filter(template -> template.code().equals(code))
        .max(Comparator.comparingInt(ProfileTemplate::version));
  }

  @Override
  public Optional<ProfileTemplate> find(String code, int version) {
    return templates().stream()
        .filter(template -> template.code().equals(code) && template.version() == version)
        .findFirst();
  }

  @Override
  public TerminologyPack baseTerminology() {
    return base;
  }

  List<LoadedTemplate> loaded() {
    return loaded;
  }

  private List<ProfileTemplate> templates() {
    return loaded.stream().map(LoadedTemplate::template).toList();
  }

  private static LoadedTemplate load(ObjectMapper json, Resource resource) {
    JsonNode payload = read(json, resource);
    TemplateFile file = json.treeToValue(payload, TemplateFile.class);
    return new LoadedTemplate(file.toTemplate(), payload);
  }

  private static JsonNode read(ObjectMapper json, Resource resource) {
    try (InputStream in = resource.getInputStream()) {
      return json.readTree(in);
    } catch (IOException e) {
      throw new UncheckedIOException("could not read profile pack " + resource, e);
    }
  }
}
