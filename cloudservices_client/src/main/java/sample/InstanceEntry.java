package sample;

public class InstanceEntry {
    public String instanceId;
    public String instanceKeyName;
    public String instanceType;
    public String instanceState;
    public String instanceMonitoringState;

    public InstanceEntry(String instanceId, String instanceKeyName, String instanceType, String instanceState, String instanceMonitoringState){
        this.instanceId = instanceId;
        this.instanceKeyName = instanceKeyName;
        this.instanceType = instanceType;
        this.instanceState = instanceState;
        this.instanceMonitoringState = instanceMonitoringState;
    }

    @Override
    public String toString()
    {
        String instanceEntryString = this.instanceId + "   " + this.instanceKeyName + "   " + this.instanceType + "   " + instanceState + "   " + instanceMonitoringState;
        return instanceEntryString;
    }

    public String getInstanceId() {
        return instanceId;
    }
}
