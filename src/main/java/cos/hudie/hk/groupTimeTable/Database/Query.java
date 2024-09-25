package cos.hudie.hk.groupTimeTable.Database;

import cos.hudie.hk.groupTimeTable.GroupManager.Group;
import cos.hudie.hk.groupTimeTable.GroupManager.Memo;
import cos.hudie.hk.groupTimeTable.Main;
import cos.hudie.hk.groupTimeTable.Student;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Query {
    public static Connection connection;

    public List<ImportGroup> getGroupList() throws SQLException {
        List<ImportGroup> importGroups = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT Lecture.LectureName , GP.GroupID FROM GP JOIN Lecture ON Lecture.LectureID = GP.LectureID ORDER BY Lecture.LectureName");
            statement.execute("SELECT Lecture.LectureName , GP.GroupID FROM GP JOIN Lecture ON Lecture.LectureID = GP.LectureID ORDER BY Lecture.LectureName");
            

            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                importGroups.add(new ImportGroup(resultSet.getString("Lecture.LectureName"), resultSet.getInt("GP.GroupID")));
            }
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return importGroups;
    }


    public int CreateGroup(Group group) throws SQLException {
        try {
            Statement statement = connection.createStatement();

            if (!hasLecture(group.getLectureID())) {
                

                Main.AddINFO("SQL: INSERT INTO Lecture(LectureID, LectureName) VALUES('%s' , '%s')".formatted(group.getLectureID(), group.getLectureName()));
                statement.execute("INSERT INTO Lecture(LectureID, LectureName) VALUES('%s' , '%s')".formatted(group.getLectureID(), group.getLectureName()));
            }

            

            Main.AddINFO("SQL: INSERT INTO GP(LectureID , TutorGroup) VALUES('%s' , '%s')".formatted(group.getLectureID() , group.getTutorGroup()));
            statement.execute("INSERT INTO GP(LectureID , TutorGroup) VALUES('%s' , '%s')".formatted(group.getLectureID() , group.getTutorGroup()));


            Main.AddINFO("SQL: SELECT MAX(GroupID) FROM GP");
            statement.execute("SELECT MAX(GroupID) FROM GP");

            

            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next()) {
                group.setGroupID(resultSet.getInt("MAX(GroupID)"));
                group.StudentList.forEach(e -> {
                    try {
                        Main.AddINFO("SQL: INSERT INTO GroupMember VALUES('%s' , '%s' , '%s')".formatted(group.getGroupID(), e.getStudentID(), "FALSE"));
                        statement.execute("INSERT INTO GroupMember VALUES('%s' , '%s' , '%s')".formatted(group.getGroupID(), e.getStudentID(), "FALSE"));
                        

                        Main.AddINFO("SQL: INSERT INTO Student VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')".formatted(e.getStudentID(), e.getStudentName()));
                        statement.execute("INSERT INTO Student VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')".formatted(e.getStudentID(), e.getStudentName()));
                        

                        for (int  i = 0 ; i < e.TimeTable.size() ; i++){
                            for (int i2 = 0 ; i< e.TimeTable.get(i).size() ; i2++){
                                Main.AddINFO("SQL: INSERT INTO TimeTable(StudentID , StartingTime , EndingTime , DayOfWeek) VALUES ('%s' , '%s' , '%s' , '%s')".formatted(e.getStudentID() , e.TimeTable.get(i).get(i2++) , e.TimeTable.get(i).get(i2) , DayOfWeek.of(i+1)));
                                statement.execute("INSERT INTO TimeTable(StudentID , StartingTime , EndingTime , DayOfWeek) VALUES ('%s' , '%s' , '%s' , '%s')".formatted(e.getStudentID() , e.TimeTable.get(i).get(i2++) , e.TimeTable.get(i).get(i2) , DayOfWeek.of(i+1)));
                                

                            }
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                for (Memo memo : group.MemoList) {
                    Main.AddINFO("SQL: INSERT INTO Memo(GroupID, MemoName , StartingTime , EndingTime , Description) VALUES ('%s' ,'%s' ,'%s' ,'%s' ,'%s')".formatted(group.getGroupID(), memo.getName(), memo.getStartTime(), memo.getEndTime(), memo.getDescription()));
                    statement.execute("INSERT INTO Memo(GroupID, MemoName , StartingTime , EndingTime , Description) VALUES ('%s' ,'%s' ,'%s' ,'%s' ,'%s')".formatted(group.getGroupID(), memo.getName(), memo.getStartTime(), memo.getEndTime(), memo.getDescription()));
                }
            }

            return group.getGroupID();
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return -1;
    }


    public boolean UpdateGroup(Group group) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            

            Main.AddINFO("SQL: UPDATE Lecture SET LectureName =  '%s' WHERE LectureID = '%s'".formatted(group.getLectureName(), group.getLectureID()));
            statement.execute("UPDATE Lecture SET LectureName =  '%s' WHERE LectureID = '%s'".formatted(group.getLectureName(), group.getLectureID()));
            

            Main.AddINFO("SQL: UPDATE GP SET TutorGroup = '%s' WHERE GroupID = %d".formatted(group.getTutorGroup() , group.getGroupID()));
            statement.execute("UPDATE GP SET TutorGroup = '%s' WHERE GroupID = %d".formatted(group.getTutorGroup() , group.getGroupID()));

            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean hasGroup(int index) {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT GroupID FROM GP WHERE GroupID=%d".formatted(index));
            statement.execute("SELECT GroupID FROM GP WHERE GroupID=%d".formatted(index));
            

            ResultSet resultSet = statement.getResultSet();

            return resultSet.next();
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean hasStudent(String StudentID) {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT * FROM Student WHERE StudentID = '%s'".formatted(StudentID));
            statement.execute("SELECT * FROM Student WHERE StudentID = '%s'".formatted(StudentID));
            

            ResultSet resultSet = statement.getResultSet();

            return resultSet.next();
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public Student getStudent(String StudentID) {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT * FROM Student WHERE StudentID = '%s'".formatted(StudentID));
            statement.execute("SELECT * FROM Student WHERE StudentID = '%s'".formatted(StudentID));
            

            ResultSet resultSet = statement.getResultSet();
            Statement statement1 = connection.createStatement();
            while (resultSet.next()) {
                Student student = new Student(resultSet.getString("StudentName"), resultSet.getString("StudentID"));
                for ( int i = 0 ; i < 7 ; i++) {
                    student.TimeTable.add(new ArrayList<>());
                }
                for (int i = 0 ; i < DayOfWeek.values().length ; i++) {
                    statement1.execute("SELECT StartingTime , EndingTime FROM TimeTable WHERE StudentID = '%s' AND DayOFWeek = '%s'".formatted(StudentID , DayOfWeek.values()[i]));
                    

                    ResultSet resultSet1 = statement1.getResultSet();
                    while (resultSet1.next()) {
                        student.TimeTable.get(i).add(resultSet1.getInt("StartingTime"));
                        student.TimeTable.get(i).add(resultSet1.getInt("EndingTime"));
                    }

                }
                return student;
            }



        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return null;
    }


    public boolean addStudent(Student student) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            

            Main.AddINFO("SQL: INSERT INTO Student VALUES( '%s' , '%s')".formatted(student.getStudentID() , student.getStudentName()));
            statement.execute("INSERT INTO Student VALUES( '%s' , '%s')".formatted(student.getStudentID() , student.getStudentName()));
            System.out.println(student);
            for (int  i = 0 ; i < student.TimeTable.size() ; i++){
                for (int i2 = 0 ; i2< student.TimeTable.get(i).size() ; i2+=2){
                    Main.AddINFO("SQL: INSERT INTO TimeTable(StudentID , StartingTime , EndingTime , DayOfWeek) VALUES ('%s' , '%s' , '%s' , '%s')".formatted(student.getStudentID() , student.TimeTable.get(i).get(i2) , student.TimeTable.get(i).get(i2+1) , DayOfWeek.of(i+1)));
                    statement.execute("INSERT INTO TimeTable(StudentID , StartingTime , EndingTime , DayOfWeek) VALUES ('%s' , '%s' , '%s' , '%s')".formatted(student.getStudentID() , student.TimeTable.get(i).get(i2) , student.TimeTable.get(i).get(i2+1) , DayOfWeek.of(i+1)));
                    

                }
            }
            // session time usage table

            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean updateStudent(Student student) throws SQLException {
        try {
            Statement statement = connection.createStatement();

            Main.AddINFO("SQL: UPDATE Student SET StudentName = '%s' WHERE StudentID = '%s'".formatted(student.getStudentName(), student.getStudentID()));
            statement.execute("UPDATE Student SET StudentName = '%s' WHERE StudentID = '%s'".formatted(student.getStudentName(), student.getStudentID()));
            

            Main.AddINFO("SQL: DELETE FROM TimeTable WHERE StudentID = '%s'".formatted(student.getStudentID()));
            statement.execute("DELETE FROM TimeTable WHERE StudentID = '%s'".formatted(student.getStudentID()));
            

            for (int  i = 0 ; i < student.TimeTable.size() ; i++){
                for (int i2 = 0 ; i2< student.TimeTable.get(i).size() ; i2+=2){
                    Main.AddINFO("SQL: INSERT INTO TimeTable(StudentID , StartingTime , EndingTime , DayOfWeek) VALUES ('%s' , '%s' , '%s' , '%s')".formatted(student.getStudentID() , student.TimeTable.get(i).get(i2) , student.TimeTable.get(i).get(i2+1) , DayOfWeek.of(i+1)));
                    statement.execute("INSERT INTO TimeTable(StudentID , StartingTime , EndingTime , DayOfWeek) VALUES ('%s' , '%s' , '%s' , '%s')".formatted(student.getStudentID() , student.TimeTable.get(i).get(i2) , student.TimeTable.get(i).get(i2+1) , DayOfWeek.of(i+1)));
                    

                }
            }            // session time usage table

            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public Group getGroup(int index) {
        Group group = null;
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT * FROM Lecture JOIN GP ON Lecture.LectureID = GP.LectureID WHERE GP.GroupID = '%s'".formatted(index));
            statement.execute("SELECT * FROM Lecture JOIN GP ON Lecture.LectureID = GP.LectureID WHERE GP.GroupID = '%s'".formatted(index));
            

            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                group = new Group(resultSet.getString("Lecture.LectureID"), resultSet.getString("Lecture.LectureName"), resultSet.getString("GP.TutorGroup"), resultSet.getInt("GP.GroupID"));
                Main.AddINFO("SQL: SELECT Student.StudentID FROM Student JOIN GroupMember ON Student.StudentID = GroupMember.StudentID WHERE GroupMember.GroupID = %d".formatted(index));
                statement.execute("SELECT Student.StudentID FROM Student JOIN GroupMember ON Student.StudentID = GroupMember.StudentID WHERE GroupMember.GroupID = %d".formatted(index));
                

                resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    group.StudentList.add(getStudent(resultSet.getString("StudentID")));
                }
                Main.AddINFO("SQL: SELECT MemoID , MemoName , StartingTime , EndingTime , Description FROM Memo WHERE GroupID = '%s'".formatted(index));
                statement.execute("SELECT MemoID , MemoName , StartingTime , EndingTime , Description FROM Memo WHERE GroupID = '%s'".formatted(index));
                

                resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    group.MemoList.add(new Memo(resultSet.getString("MemoName"), LocalDateTime.parse(resultSet.getString("StartingTime").replace(" " , "T")), LocalDateTime.parse(resultSet.getString("EndingTime").replace(" " , "T")), resultSet.getString("Description"), resultSet.getInt("MemoID")));
                }
            }
            return group;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return group;
    }


    public boolean deleteGroup(int index) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: DELETE FROM Memo WHERE GroupID = %s".formatted(index));
            statement.execute("DELETE FROM Memo WHERE GroupID = %s".formatted(index));
            

            Main.AddINFO("SQL: DELETE FROM GroupMember WHERE GroupID = %s".formatted(index));
            statement.execute("DELETE FROM GroupMember WHERE GroupID = %s".formatted(index));
            

            Main.AddINFO("SQL: DELETE FROM GP WHERE GroupID = %s".formatted(index));
            statement.execute("DELETE FROM GP WHERE GroupID = %s".formatted(index));
            


            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean deleteGroup(Group group) throws SQLException {
        return deleteGroup(group.getGroupID());
    }

    public boolean deleteStudent(String StudentID) {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: DELETE FROM GroupMember WHERE StudentID = '%s'".formatted(StudentID));
            statement.execute("DELETE FROM GroupMember WHERE StudentID = '%s'".formatted(StudentID));
            

            Main.AddINFO("SQL: DELETE FROM TimeTable WHERE StudentID = '%s'".formatted(StudentID));
            statement.execute("DELETE FROM TimeTable WHERE StudentID = '%s'".formatted(StudentID));
            

            Main.AddINFO("SQL: DELETE FROM Student WHERE StudentID = '%s'".formatted(StudentID));
            statement.execute("DELETE FROM Student WHERE StudentID = '%s'".formatted(StudentID));
            



            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean deleteStudent(Student student) {
        return deleteStudent(student.getStudentID());
    }


    public boolean deleteGroupMember(Group group, Student student) {
        return deleteGroupMember(group.getGroupID(), student.getStudentID());
    }


    public boolean deleteGroupMember(Group group, String StudentID) {
        return deleteGroupMember(group.getGroupID(), StudentID);
    }


    public boolean deleteGroupMember(int GroupIndex, Student student) {
        return deleteGroupMember(GroupIndex, student.getStudentID());
    }


    public boolean deleteGroupMember(int GroupIndex, String StudentID) {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: DELETE FROM GroupMember WHERE StudentID = '%s' AND GroupID = %d".formatted(StudentID, GroupIndex));
            statement.execute("DELETE FROM GroupMember WHERE StudentID = '%s' AND GroupID = %d".formatted(StudentID, GroupIndex));
            


            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean addGroupMember(Group group, Student student) {
        return addGroupMember(group.getGroupID(), student);
    }


    public boolean addGroupMember(int GroupIndex, Student student) {
        try {
            if (!hasStudent(student.getStudentID())) {
                addStudent(student);
            }
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: INSERT INTO GroupMember VALUES(%s , '%s' , 0)".formatted(GroupIndex, student.getStudentID()));
            statement.execute("INSERT INTO GroupMember VALUES(%s , '%s' , 0)".formatted(GroupIndex, student.getStudentID()));
            


            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean updateGroupMember(Group group, Student student) {
        return updateGroupMember(group.getGroupID(), student);
    }


    public boolean updateGroupMember(int GroupIndex, Student student) {
        try {
            Statement statement = connection.createStatement();
            updateStudent(student);
            if (student.isGroupLeader()) {
                Main.AddINFO("SQL: SELECT * FROM GroupMember WHERE GroupID = '%d' AND IsGroupLeader = 1".formatted(GroupIndex));
                statement.execute("SELECT * FROM GroupMember WHERE GroupID = '%d' AND IsGroupLeader = 1".formatted(GroupIndex));
                

                ResultSet resultSet = statement.getResultSet();
                if (resultSet.next()) {
                    Main.AddINFO("SQL: UPDATE GroupMember SET IsGroupLeader = 0 WHERE GroupID = '%d' AND IsGroupLeader = 1".formatted(GroupIndex));
                    statement.execute("UPDATE GroupMember SET IsGroupLeader = 0 WHERE GroupID = '%d' AND IsGroupLeader = 1".formatted(GroupIndex));
                    

                }
                Main.AddINFO("SQL: UPDATE GroupMember SET IsGroupLeader = 1 WHERE GroupID = '%d' AND StudentID = '%s'".formatted(GroupIndex, student.getStudentID()));
                statement.execute("UPDATE GroupMember SET IsGroupLeader = 1 WHERE GroupID = '%d' AND StudentID = '%s'".formatted(GroupIndex, student.getStudentID()));
                

            }

            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean hasGroupMember(String StudentID, int GroupIndex) {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT * FROM GroupMember WHERE StudentID = '%s' AND GroupID = '%d'".formatted(StudentID, GroupIndex));
            statement.execute("SELECT * FROM GroupMember WHERE StudentID = '%s' AND GroupID = '%d'".formatted(StudentID, GroupIndex));
            

            ResultSet resultSet = statement.getResultSet();

            return resultSet.next();
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean hasGroupMember(String StudentID, Group group) {
        return hasGroupMember(StudentID, group.getGroupID());
    }


    public boolean deleteMemo(int MemoID) {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: DELETE FROM Memo WHERE MemoID = %d".formatted(MemoID));
            statement.execute("DELETE FROM Memo WHERE MemoID = %d".formatted(MemoID));
            


            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean addMemo(Group group, Memo memo) {
        return addMemo(group.getGroupID(), memo);
    }


    public boolean addMemo(int GroupID, Memo memo) {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: INSERT INTO Memo(GroupID , MemoName , StartingTime , EndingTime , Description) VALUES (%d , '%s' , '%s' , '%s' , '%s')".formatted(GroupID, memo.getName(), memo.getStartTime(), memo.getEndTime(), memo.getDescription()));
            statement.execute("INSERT INTO Memo(GroupID , MemoName , StartingTime , EndingTime , Description) VALUES (%d , '%s' , '%s' , '%s' , '%s')".formatted(GroupID, memo.getName(), memo.getStartTime(), memo.getEndTime(), memo.getDescription()));
            


            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean editMemo(int GroupID, Memo memo) {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: UPDATE Memo SET MemoName = '%s' , StartingTime = '%s' , EndingTime = '%s' , Description = '%s' WHERE MemoID = %d".formatted(memo.getName(), memo.getStartTime(), memo.getEndTime(), memo.getDescription(), memo.getMemoID()));
            statement.execute("UPDATE Memo SET MemoName = '%s' , StartingTime = '%s' , EndingTime = '%s' , Description = '%s' WHERE MemoID = %d".formatted(memo.getName(), memo.getStartTime(), memo.getEndTime(), memo.getDescription(), memo.getMemoID()));
            


            return true;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public boolean editMemo(Group group, Memo memo) {
        return editMemo(group.getGroupID(), memo);
    }


    public boolean hasMemo(int MemoId) {
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT * FROM Memo WHERE MemoID = %d".formatted(MemoId));
            statement.execute("SELECT * FROM Memo WHERE MemoID = %d".formatted(MemoId));
            

            ResultSet resultSet = statement.getResultSet();

            return resultSet.next();

        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return false;
    }


    public Memo getMemo(int MemoID) {
        Memo memo = null;
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT MemoID , MemoName , StartingTime , EndingTime , Description FROM Memo WHERE memoID = '%s'".formatted(MemoID));
            statement.execute("SELECT MemoID , MemoName , StartingTime , EndingTime , Description FROM Memo WHERE memoID = '%s'".formatted(MemoID));
            

            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                memo = new Memo(resultSet.getString("MemoName"), resultSet.getTimestamp("StartingTime").toLocalDateTime(), resultSet.getTimestamp("EndingTime").toLocalDateTime(), resultSet.getString("Description"), resultSet.getInt("MemoID"));

                return memo;
            }


        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return memo;
    }

    public boolean hasLecture(String LectureID){
        try{
            Statement statement = connection.createStatement();
            

            Main.AddINFO("SQL: SELECT * FROM Lecture WHERE LECTUREID = '%s'".formatted(LectureID));
            statement.execute("SELECT * FROM Lecture WHERE LECTUREID = '%s'".formatted(LectureID));
            ResultSet resultSet = statement.getResultSet();
            return resultSet.next();
        }catch (SQLException e){
            Main.contactDeveloper(e);
        }
        return false;
    }

    public String getLectureName(String LectureID){
        if (hasLecture(LectureID)){
            try{
                Statement statement = connection.createStatement();
                

                Main.AddINFO("SQL: SELECT * FROM Lecture WHERE LECTUREID = '%s'".formatted(LectureID));
                statement.execute("SELECT * FROM Lecture WHERE LECTUREID = '%s'".formatted(LectureID));
                ResultSet resultSet = statement.getResultSet();
                if (resultSet.next()){
                    return resultSet.getString("LectureName");
                }
                return null;
            }catch (SQLException e){
                Main.contactDeveloper(e);
            }
        }
        return null;
    }

    public List<Lecture> getLectureList(){
        try{
            Statement statement = connection.createStatement();
            

            Main.AddINFO("SQL: SELECT * FROM Lecture");
            statement.execute("SELECT * FROM Lecture");
            ResultSet resultSet = statement.getResultSet();
            List<Lecture> lectureList = new ArrayList<>();
            while (resultSet.next()){
                lectureList.add(new Lecture(resultSet.getString("LectureID") , resultSet.getString("LectureName")));
            }
            return lectureList;
        }catch (SQLException e){
            Main.contactDeveloper(e);
        }
        return null;
    }

    public void CreateLecture(Lecture lecture){
        try{
            if(!hasLecture(lecture.LectureID())){
                Statement statement = connection.createStatement();
                Main.AddINFO("SQL: INSERT INTO Lecture VALUES('%s' ,'%s')".formatted(  lecture.LectureName() , lecture.LectureID()));
                statement.execute("INSERT INTO Lecture VALUES('%s' ,'%s')".formatted(lecture.LectureName() , lecture.LectureID()));
            }else{
                System.out.println("A same LectureID has been found on Database: " + lecture.LectureID());
            }
        }catch (SQLException e){
            Main.contactDeveloper(e);
        }
    }

    public void UpdateLecture(Lecture lecture){
        try{
            if(hasLecture(lecture.LectureID())){
                Statement statement = connection.createStatement();
                

                Main.AddINFO("SQL: UPDATE Lecture SET Lecturename = '%s' WHERE LectureID = '%s'".formatted(lecture.LectureName() , lecture.LectureID()));
                statement.execute("UPDATE Lecture SET Lecturename = '%s' WHERE LectureID = '%s'".formatted(lecture.LectureName() , lecture.LectureID()));
            }else{
                System.out.println("A same LectureID has been found on Database: " + lecture.LectureID());
            }
        }catch (SQLException e){
            Main.contactDeveloper(e);
        }
    }

    public void DeleteLecture(String LectureID){
        try{
            if(hasLecture(LectureID)){
                Statement statement = connection.createStatement();
                Main.AddINFO("SQL: SELECT GroupID FROM GP WHERE LectureID = '%s'".formatted(LectureID));
                statement.execute("SELECT GroupID FROM GP WHERE LectureID = '%s'".formatted(LectureID));
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()){
                    deleteGroup(resultSet.getInt("GroupID"));
                }
                Main.AddINFO("SQL: DELETE FROM Lecture WHERE LectureID = '%s'".formatted(LectureID));
                statement.execute("DELETE FROM Lecture WHERE LectureID = '%s'".formatted(LectureID));
            }
        }catch (SQLException e){
            Main.contactDeveloper(e);
        }
    }


    public List<Student> getAllStudent() {
        List<Student> studentList = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT StudentID FROM Student");
            statement.execute("SELECT StudentID FROM Student");
            

            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                studentList.add(getStudent(resultSet.getString("StudentID")));
            }

            return studentList;
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return studentList;
    }

    public void connectionClose() {
        try {
            connection.close();
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
    }
}
