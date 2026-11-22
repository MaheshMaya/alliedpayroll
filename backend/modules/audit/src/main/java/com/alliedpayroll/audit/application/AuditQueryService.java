package com.alliedpayroll.audit.application;

import com.alliedpayroll.audit.domain.AuditEvent;
import com.alliedpayroll.audit.domain.AuditEventRepository;
import com.alliedpayroll.audit.domain.AuditPermissions;
import com.alliedpayroll.audit.domain.AuditQuery;
import com.alliedpayroll.identity.api.UserDirectory;
import com.alliedpayroll.platform.core.audit.ActorType;
import com.alliedpayroll.platform.security.CurrentUserProvider;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuditQueryService {

  private final AuditEventRepository events;
  private final UserDirectory users;
  private final CurrentUserProvider currentUser;

  AuditQueryService(
      AuditEventRepository events, UserDirectory users, CurrentUserProvider currentUser) {
    this.events = events;
    this.users = users;
    this.currentUser = currentUser;
  }

  public record Listing(List<AuditEvent> items, Map<UUID, String> actorNames, boolean hasMore) {}

  /** One page newer-first; the caller sees security rows only with the security permission. */
  public Listing list(AuditQuery requested) {
    boolean security = currentUser.require().has(AuditPermissions.SECURITY_READ);
    AuditQuery query =
        new AuditQuery(
            requested.action(),
            requested.actorId(),
            requested.entityType(),
            requested.entityId(),
            requested.from(),
            requested.to(),
            security,
            requested.after(),
            requested.size() + 1);
    List<AuditEvent> found = events.find(query);
    boolean hasMore = found.size() > requested.size();
    List<AuditEvent> page = hasMore ? found.subList(0, requested.size()) : found;
    Set<UUID> actorIds =
        page.stream()
            .filter(event -> event.actorType() == ActorType.USER)
            .map(AuditEvent::actorId)
            .collect(Collectors.toSet());
    return new Listing(page, users.displayNames(actorIds), hasMore);
  }
}
