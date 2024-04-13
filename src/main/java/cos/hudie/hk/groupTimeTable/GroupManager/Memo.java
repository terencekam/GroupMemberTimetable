package cos.hudie.hk.groupTimeTable.GroupManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Memo {
    private String name;
    private LocalDateTime StartTime;
    private LocalDateTime EndTime;
    private String description;
    private int MemoID;

    public Memo(String name, LocalDateTime startTime, LocalDateTime endTime, String description) {
        this.name = name;
        StartTime = startTime;
        EndTime = endTime;
        this.description = description;
    }

    public Memo(String name, LocalDateTime startTime, LocalDateTime endTime, String description, int MemoID) {
        this.name = name;
        StartTime = startTime;
        EndTime = endTime;
        this.description = description;
        this.MemoID = MemoID;
    }

    public int getMemoID() {
        return MemoID;
    }

    public void setMemoID(int memoID) {
        MemoID = memoID;
    }

    @Override
    public String toString() {
        return "%s %11s %38s %9s\n"
                .formatted(MemoID, name,
                        StartTime.equals(EndTime) ? StartTime.toLocalDate() + " " + StartTime.toLocalTime() : StartTime.toLocalDate() + " " + StartTime.toLocalTime() + "-" + EndTime.toLocalDate() + " " + EndTime.toLocalTime()
                        , description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartTime() {
        return StartTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        StartTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return EndTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        EndTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartingTime_DATE() {
        return StartTime.toLocalDate();
    }

    public LocalDate getEndingTime_DATE() {
        return EndTime.toLocalDate();
    }

    public LocalTime getStartingTime_TIME() {
        return StartTime.toLocalTime();
    }

    public LocalTime getEndingTime_TIME() {
        return EndTime.toLocalTime();
    }
}

//                "Memo{" +
//                "name='" + name + '\'' +
//                ", Date=" + Date +
//                ", StartingTime=" + StartingTime +
//                ", EndingTime=" + EndingTime +
//                '}';
