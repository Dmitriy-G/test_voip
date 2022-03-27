package server.model;

public class Command {
    private String sourceGuid;
    private ClientEvent eventType;
    private String targetGuid;
    private String body;

    public Command(String sourceGuid, ClientEvent eventType, String targetGuid, String body) {
        this.sourceGuid = sourceGuid;
        this.eventType = eventType;
        this.targetGuid = targetGuid;
        this.body = body;
    }

    public String getSourceGuid() {
        return sourceGuid;
    }

    public ClientEvent getEventType() {
        return eventType;
    }

    public String getTargetGuid() {
        return targetGuid;
    }

    public String getBody() {
        return body;
    }
}
