package mindpath.core.utility.annotation;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_ADMIN') or @groupSecurityServiceImpl.isUserAllowedToEnterGroup(#groupId, principal.username)")
public @interface IsUserAllowedToEnterGroup {
}
