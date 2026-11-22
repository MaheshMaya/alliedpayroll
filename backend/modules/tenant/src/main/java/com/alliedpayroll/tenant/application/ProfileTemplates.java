package com.alliedpayroll.tenant.application;

import com.alliedpayroll.tenant.domain.ProfileTemplate;
import com.alliedpayroll.tenant.domain.TerminologyPack;
import java.util.Optional;

/** Port: the published industry profiles and the base words every profile builds on. */
public interface ProfileTemplates {

  /** The newest published version of a profile, which a new tenant is provisioned from. */
  Optional<ProfileTemplate> latest(String code);

  Optional<ProfileTemplate> find(String code, int version);

  TerminologyPack baseTerminology();
}
