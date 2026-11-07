package com.alliedpayroll;

import com.alliedpayroll.platform.test.ArchitectureRules;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.alliedpayroll")
class ArchitectureTest {

  @ArchTest static final ArchRule domain = ArchitectureRules.DOMAIN_HAS_NO_FRAMEWORK_DEPENDENCIES;

  @ArchTest static final ArchRule channels = ArchitectureRules.CHANNELS_OWN_NO_DATA;

  @ArchTest static final ArchRule http = ArchitectureRules.ONLY_WEB_LAYER_USES_MVC;
}
