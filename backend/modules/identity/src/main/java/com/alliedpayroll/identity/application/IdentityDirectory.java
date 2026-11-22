package com.alliedpayroll.identity.application;

/**
 * Port to the identity provider, which owns credentials and organisation membership. Calls are made
 * inside the use case's transaction, so a failed call rolls the tenant's change back; every call is
 * safe to repeat.
 */
public interface IdentityDirectory {

  /**
   * Creates the account, or finds it by username, and makes it a member of the organisation. An
   * account that already belongs to another organisation is refused with {@code
   * identity.username_taken}, so one tenant can never disable another tenant's person.
   */
  void invite(String organizationAlias, NewAccount account);

  /** Switches the account off and ends every session it has. */
  void disable(String username);

  void enable(String username);

  /**
   * An account to create.
   *
   * @param lastName empty for a person with one name
   */
  record NewAccount(String username, String email, String firstName, String lastName) {}
}
