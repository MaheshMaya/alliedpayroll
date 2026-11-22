package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.api.UserDirectory;
import com.alliedpayroll.identity.domain.UserRepository;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
class UserDirectoryService implements UserDirectory {

  private final UserRepository users;

  UserDirectoryService(UserRepository users) {
    this.users = users;
  }

  @Override
  public Map<UUID, String> displayNames(Collection<UUID> userIds) {
    return users.displayNames(userIds);
  }
}
