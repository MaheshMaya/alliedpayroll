package com.alliedpayroll.identity.application;

import com.alliedpayroll.identity.domain.AppUser;
import com.alliedpayroll.identity.domain.Role;
import com.alliedpayroll.identity.domain.RoleRepository;
import com.alliedpayroll.identity.domain.UserPage;
import com.alliedpayroll.identity.domain.UserRepository;
import com.alliedpayroll.identity.domain.UserSearch;
import com.alliedpayroll.platform.core.error.NotFoundException;
import com.alliedpayroll.platform.core.ids.UserId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserQueryService {

  private final UserRepository users;
  private final RoleRepository roles;

  UserQueryService(UserRepository users, RoleRepository roles) {
    this.users = users;
    this.roles = roles;
  }

  public record Listing(List<UserView> items, long total) {}

  public Listing list(UserSearch search, int page, int size) {
    UserPage found = users.search(search, page, size);
    return new Listing(withRoles(found.items()), found.total());
  }

  public UserView get(UserId id) {
    AppUser user = users.findById(id).orElseThrow(() -> NotFoundException.of("user", id));
    return withRoles(List.of(user)).getFirst();
  }

  private List<UserView> withRoles(List<AppUser> found) {
    Map<UUID, Role> byId =
        roles.findAll().stream().collect(Collectors.toMap(Role::id, Function.identity()));
    Map<UserId, Set<UUID>> assigned = users.roleIdsOf(found.stream().map(AppUser::id).toList());
    List<UserView> views = new ArrayList<>(found.size());
    for (AppUser user : found) {
      List<Role> own =
          assigned.getOrDefault(user.id(), Set.of()).stream()
              .map(byId::get)
              .filter(role -> role != null)
              .sorted(java.util.Comparator.comparing(Role::name))
              .toList();
      views.add(new UserView(user, own));
    }
    return views;
  }
}
