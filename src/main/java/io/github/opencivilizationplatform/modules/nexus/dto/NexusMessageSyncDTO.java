package io.github.opencivilizationplatform.modules.nexus.dto;

import io.github.opencivilizationplatform.modules.nexus.domain.NexusMessageType;
import java.io.Serializable;

public class NexusMessageSyncDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sourceNodeId;
    private String sourceNodeName;
    private Long targetNodeId;
    private String targetNodeName;
    private NexusMessageType messageType;
    private String content;
    private Integer hopCount;

    public NexusMessageSyncDTO() {}

    public NexusMessageSyncDTO(Long id, Long sourceNodeId, String sourceNodeName,
                                Long targetNodeId, String targetNodeName,
                                NexusMessageType messageType, String content,
                                Integer hopCount) {
        this.id = id;
        this.sourceNodeId = sourceNodeId;
        this.sourceNodeName = sourceNodeName;
        this.targetNodeId = targetNodeId;
        this.targetNodeName = targetNodeName;
        this.messageType = messageType;
        this.content = content;
        this.hopCount = hopCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNodeId(Long sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public String getSourceNodeName() {
        return sourceNodeName;
    }

    public void setSourceNodeName(String sourceNodeName) {
        this.sourceNodeName = sourceNodeName;
    }

    public Long getTargetNodeId() {
        return targetNodeId;
    }

    public void setTargetNodeId(Long targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public String getTargetNodeName() {
        return targetNodeName;
    }

    public void setTargetNodeName(String targetNodeName) {
        this.targetNodeName = targetNodeName;
    }

    public NexusMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(NexusMessageType messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getHopCount() {
        return hopCount;
    }

    public void setHopCount(Integer hopCount) {
        this.hopCount = hopCount;
    }
}

