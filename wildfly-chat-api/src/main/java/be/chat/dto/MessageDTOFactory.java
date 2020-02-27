package be.chat.dto;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Stateless
@LocalBean
public class MessageDTOFactory {

    private static final String WILDFLY_PRINCIPAL_NAME = "WildFly";

    @Resource
    private SessionContext sessionContext;

    @RolesAllowed({"Admin"})
    public MessageDTO create(String message) {
        return MessageDTO.builder()
                .message(message)
                .time(LocalDateTime.now())
                .owner(Optional.ofNullable(sessionContext)
                        .map(SessionContext::getCallerPrincipal)
                        .map(Principal::getName)
                        .map(String::toLowerCase)
                        .filter(s -> !s.equals("anonymous"))
                        .orElse(WILDFLY_PRINCIPAL_NAME)
                )
                .build();
    }
}
