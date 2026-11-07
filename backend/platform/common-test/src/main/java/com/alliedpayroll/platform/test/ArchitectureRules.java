package com.alliedpayroll.platform.test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.lang.ArchRule;

/** The layer rules every module obeys; the application's architecture test evaluates them. */
public final class ArchitectureRules {

  private ArchitectureRules() {}

  /** Domain code stays free of Spring, JPA and messaging so it can be tested as plain Java. */
  public static final ArchRule DOMAIN_HAS_NO_FRAMEWORK_DEPENDENCIES =
      noClasses()
          .that()
          .resideInAPackage("com.alliedpayroll..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(
              "org.springframework..",
              "jakarta.persistence..",
              "org.hibernate..",
              "jakarta.servlet..")
          .as("domain packages depend on no framework");

  /** Channel modules shape data; they own no tables and no entities. */
  public static final ArchRule CHANNELS_OWN_NO_DATA =
      noClasses()
          .that()
          .resideInAnyPackage("com.alliedpayroll.webchannel..", "com.alliedpayroll.mobilechannel..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("jakarta.persistence..", "org.springframework.data..")
          .as("channel modules own no persistence");

  /** Only the web layer and channel modules talk HTTP. */
  public static final ArchRule ONLY_WEB_LAYER_USES_MVC =
      noClasses()
          .that()
          .resideInAnyPackage("com.alliedpayroll..application..", "com.alliedpayroll..domain..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.springframework.web..", "jakarta.servlet..")
          .as("use cases and domain never see HTTP");
}
