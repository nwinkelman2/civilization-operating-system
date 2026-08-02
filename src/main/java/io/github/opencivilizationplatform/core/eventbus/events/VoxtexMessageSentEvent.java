package io.github.opencivilizationplatform.core.eventbus.events;

import io.github.opencivilizationplatform.core.eventbus.BaseDomainEvent;

public class VoxtexMessageSentEvent extends BaseDomainEvent {
    private final Long messageId;
    private final Long sourceNodeId;
    private final Long targetNodeId;
    private final String messageType;
    private final String content;

    public VoxtexMessageSentEvent(String source, Long messageId, Long sourceNodeId,
                                   Long targetNodeId, String messageType, String content) {
        super(source);
        this.messageId = messageId;
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.messageType = messageType;
        this.content = content;
    }

    @Override
    public String getType() { return "VOXTEX_MESSAGE_SENT"; }

    public Long getMessageId() { return messageId; }
    public Long getSourceNodeId() { return sourceNodeId; }
    public Long getTargetNodeId() { return targetNodeId; }
    public String getMessageType() { return messageType; }
    public String getContent() { return content; }
}
