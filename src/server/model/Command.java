package server.model;

public class Command {
    private String sourceGuid;
    private ClientEvent eventType;
    private String targetGuid;
    private Object body;

    public Command(String sourceGuid, ClientEvent eventType, String targetGuid, Object body) {
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

    public Object getBody() {
        return body;
    }

    public void setSourceGuid(String sourceGuid) {
        this.sourceGuid = sourceGuid;
    }

    public void setEventType(ClientEvent eventType) {
        this.eventType = eventType;
    }

    public void setTargetGuid(String targetGuid) {
        this.targetGuid = targetGuid;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
