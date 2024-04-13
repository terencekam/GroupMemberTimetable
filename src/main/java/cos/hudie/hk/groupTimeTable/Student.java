package cos.hudie.hk.groupTimeTable;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cos.hudie.hk.groupTimeTable.GroupManager.GroupMemberManager.printTime;

public class Student {
    public List<List<Integer>> TimeTable = new ArrayList<>();
    private String StudentName;
    private final String StudentID;
    private boolean isGroupLeader;

    public Student(String studentName, String studentID, boolean isGroupLeader) {
        StudentName = studentName;
        StudentID = studentID;
        this.isGroupLeader = isGroupLeader;
    }

    public Student(String studentName, String studentID) {
        StudentName = studentName;
        StudentID = studentID;
    }

    public boolean isGroupLeader() {
        return isGroupLeader;
    }

    public void setGroupLeader() {
        isGroupLeader = true;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getStudentID() {
        return StudentID;
    }

    @Override
    public String toString() {
        StringBuilder TimeSetString = new StringBuilder();

        for (int i3 = 0; i3 < DayOfWeek.values().length; i3++) {
            Collections.sort(TimeTable.get(i3));
            TimeSetString.append(DayOfWeek.values()[i3]).append(": \n");
            for (int i = 0, i2 = 1; i < TimeTable.get(i3).size(); i++, i2++) {
                TimeSetString.append("Session ").append(i2).append(": ").append(printTime(TimeTable.get(i3).get(i++))).append("-").append(printTime(TimeTable.get(i3).get(i))).append("\n");
            }
            if (TimeTable.get(i3).isEmpty()) {
                TimeSetString.append("FREE\n");
            }
        }


        return "StudentName=" + StudentName +
                ", \nStudentID=" + StudentID +
                ", \nTimeTable:\n" + TimeSetString;
    }

//    @Override
//    public String toString() {
//        return "Student{" +
//                "TimeTable=" + TimeTable +
//                ", StudentName='" + StudentName + '\'' +
//                ", StudentID='" + StudentID + '\'' +
//                ", isGroupLeader=" + isGroupLeader +
//                '}';
//    }

    public void printStudent(String student) {
        if (student.equalsIgnoreCase(getStudentID()) || student.equalsIgnoreCase(getStudentName())) {
            System.out.println(this);
        }
    }

    public boolean hasStudent(String student) {
        return student.equalsIgnoreCase(getStudentID()) || student.equalsIgnoreCase(getStudentName());
    }
}
