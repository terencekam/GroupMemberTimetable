package cos.hudie.hk.groupTimeTable.GroupManager;

import java.util.ArrayList;
import java.util.List;

public class Group extends GroupMemberManager {
    public List<Memo> MemoList = new ArrayList<>();
    private String LectureID;
    private String LectureName;
    private String TutorGroup;

    public Group(String lectureID, String lectureName, String tutorGroup) {
        LectureID = lectureID;
        LectureName = lectureName;
        TutorGroup = tutorGroup;
    }

    public Group(String lectureID, String lectureName, String tutorGroup, int groupID) {
        LectureID = lectureID;
        LectureName = lectureName;
        TutorGroup = tutorGroup;
        GroupID = groupID;
    }

    @Override
    public String toString() {
        return "Group{" +
                "LectureID='" + LectureID + '\'' +
                ", LectureName='" + LectureName + '\'' +
                ", TutorGroup(representing)='" + TutorGroup + '\'' +
                ", GroupID=" + GroupID +
                ", MemoList=\n" + MemoList +
                "} " + super.toString();
    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public String getLectureID() {
        return LectureID;
    }

    public void setLectureID(String lectureID) {
        LectureID = lectureID;
    }

    public String getLectureName() {
        return LectureName;
    }

    public void setLectureName(String lectureName) {
        LectureName = lectureName;
    }

    public String getTutorGroup() {
        return TutorGroup;
    }

    public void setTutorGroup(String tutorGroup) {
        TutorGroup = tutorGroup;
    }
}
