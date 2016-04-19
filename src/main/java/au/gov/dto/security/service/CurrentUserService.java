package au.gov.dto.security.service;

import au.gov.dto.security.domain.CurrentUser;

public interface CurrentUserService {

    boolean canAccessUser(CurrentUser currentUser, Long userId);

}
