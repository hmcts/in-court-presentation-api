package uk.gov.hmcts.reform.rpa.incourtpres.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import uk.gov.hmcts.reform.rpa.incourtpres.domain.SubscriptionStatus;
import uk.gov.hmcts.reform.rpa.incourtpres.services.ParticipantsStatusService;

import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;

@Component
public class StompSubscribedEvent implements ApplicationListener<SessionSubscribeEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(StompSubscribedEvent.class);

    private final ParticipantsStatusService participantsStatusService;

    @Autowired
    public StompSubscribedEvent(ParticipantsStatusService participantsStatusService) {
        this.participantsStatusService = participantsStatusService;
    }

    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        LOG.info(sessionId);
        String destinationTopic = sha.getHeader(DESTINATION_HEADER).toString();
        String hearingSessionId = destinationTopic.replaceAll("\\/.*\\/.*\\/", "");
        participantsStatusService.updateStatus(hearingSessionId, sessionId, SubscriptionStatus.FOLLOWING);
    }
}
