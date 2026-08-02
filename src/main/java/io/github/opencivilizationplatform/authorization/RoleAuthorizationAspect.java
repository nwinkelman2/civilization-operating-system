package io.github.opencivilizationplatform.authorization;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class RoleAuthorizationAspect {

    private static final Logger log = LoggerFactory.getLogger(RoleAuthorizationAspect.class);

    @Around("@annotation(requiresRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequiresRole requiresRole) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String userRole = (String) request.getAttribute("X-User-Role");
        if (userRole == null) userRole = Role.CITIZEN.name();

        List<String> allowed = Arrays.stream(requiresRole.value()).map(Enum::name).toList();
        if (allowed.contains(userRole) || allowed.contains(Role.ADMIN.name())) {
            return joinPoint.proceed();
        }
        log.warn("Access denied for role {} to {}", userRole, joinPoint.getSignature());
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, requiresRole.message());
    }
}
