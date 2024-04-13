package cos.hudie.hk.groupTimeTable.Database;

public record ImportGroup(String GroupName, int GroupID) {
    @Override
    public String toString() {
        return "%-15s %4s".formatted(GroupName, GroupID);
    }
}
