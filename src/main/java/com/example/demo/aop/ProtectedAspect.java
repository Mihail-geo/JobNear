package com.example.demo.aop;

import com.example.demo.constant.SecurityConstant;
import com.example.demo.model.entity.ModuleAccessEntity;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.util.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.example.demo.util.ModuleUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Aspect
@Component
public class ProtectedAspect {
    @Before("@annotation(com.example.demo.aop.Protected)")
    public void checkAccess(JoinPoint joinPoint) {
        Protected annotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Protected.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) throw new AccessDeniedException();

        Map<String, Object> claims = HttpUtil.getMapClaimsFromTokenInAuthentication(authentication);
        boolean admin = Boolean.parseBoolean(claims.get(SecurityConstant.ADMIN_FIELD_IN_TOKEN).toString());
        if (!admin) {
            Set<ModuleAccessEntity> access = new HashSet<>(
                    new ObjectMapper().convertValue(claims.get(SecurityConstant.ACCESS_FIELD_IN_TOKEN),
                            new TypeReference<List<ModuleAccessEntity>>() {
                            })
            );
            if (!ModuleUtil.searchModuleAndScopeInAccessModules(annotation.module(), annotation.scope(), access))
                throw new AccessDeniedException();
        }
    }
}
