package cos.hudie.hk.groupTimeTable.Database.SQLite;

import cos.hudie.hk.groupTimeTable.Database.ImportGroup;
import cos.hudie.hk.groupTimeTable.Database.Lecture;
import cos.hudie.hk.groupTimeTable.GroupManager.Group;
import cos.hudie.hk.groupTimeTable.GroupManager.Memo;
import cos.hudie.hk.groupTimeTable.Main;
import cos.hudie.hk.groupTimeTable.Student;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Query extends cos.hudie.hk.groupTimeTable.Database.Query {



    public Query() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Members.db");
        } catch (SQLException e) {
            Main.contactDeveloper(e);
        } catch (ClassNotFoundException e) {
            Main.contactDeveloper(e);
        }
        try {
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS Lecture(LectureName CHAR(50) , LectureID CHAR(8) , PRIMARY KEY(LectureID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS Lecture(LectureName CHAR(50) , LectureID CHAR(8) , PRIMARY KEY(LectureID))");

            statement.execute("CREATE TABLE IF NOT EXISTS GP(GroupID INTEGER NOT NULL , LectureID CHAR(8) , TutorGroup CHAR(4) , PRIMARY KEY(GroupID) , FOREIGN KEY(LectureID) REFERENCES Lecture(LectureID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS GP(GroupID INTEGER NOT NULL , LectureID CHAR(8) , TutorGroup CHAR(4) , PRIMARY KEY(GroupID) , FOREIGN KEY(LectureID) REFERENCES Lecture(LectureID))");


            Main.AddINFO("SQL :" + "CREATE TABLE IF NOT EXISTS Memo(MemoID INTEGER NOT NULL, GroupID INTEGER NOT NULL, MemoName TEXT" +
                    ", StartingTime DATETIME, EndingTime DATETIME, Description TEXT ,FOREIGN KEY(GroupID) REFERENCES GP(GroupID) , PRIMARY KEY(MemoID))");
            statement.execute("CREATE TABLE IF NOT EXISTS Memo(MemoID INTEGER NOT NULL, GroupID INTEGER NOT NULL, MemoName TEXT" +
                    ", StartingTime DATETIME, EndingTime DATETIME, Description TEXT ,FOREIGN KEY(GroupID) REFERENCES GP(GroupID) , PRIMARY KEY(MemoID))");

            statement.execute("CREATE TABLE IF NOT EXISTS Student(StudentID CHAR(9) NOT NULL, StudentName TEXT , PRIMARY KEY(StudentID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS Student(StudentID CHAR(9) NOT NULL, StudentName TEXT , PRIMARY KEY(StudentID))");

            statement.execute("CREATE TABLE IF NOT EXISTS GroupMember(GroupID INTEGER NOT NULL, StudentID CHAR(9) NOT NULL, IsGroupLeader BIT ,FOREIGN KEY(GroupID) REFERENCES GP(GroupID) , FOREIGN KEY(StudentID) REFERENCES Student(StudentID), PRIMARY KEY(GroupID , StudentID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS GroupMember(GroupID INTEGER NOT NULL, StudentID CHAR(9) NOT NULL, IsGroupLeader BIT ,FOREIGN KEY(GroupID) REFERENCES GP(GroupID) , FOREIGN KEY(StudentID) REFERENCES Student(StudentID), PRIMARY KEY(GroupID , StudentID))");

            statement.execute("CREATE TABLE IF NOT EXISTS TimeTable(ID INTEGER NOT NULL , StudentID CHAR(9) , StartingTime INTEGER(4) , EndingTime INTEGER(4) , DayOfWeek , FOREIGN KEY(StudentID) REFERENCES Student(StudentID) , PRIMARY KEY(ID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS TimeTable(ID INTEGER NOT NULL , StudentID CHAR(9) , StartingTime INTEGER(4) , EndingTime INTEGER(4) , DayOfWeek , FOREIGN KEY(StudentID) REFERENCES Student(StudentID) , PRIMARY KEY(ID))");

        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }

    }
    @Override
    public List<ImportGroup> getGroupList() {
        List<ImportGroup> importGroups = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT Lecture.LectureName , GP.GroupID FROM GP JOIN Lecture ON Lecture.LectureID = GP.LectureID");
            statement.execute("SELECT Lecture.LectureName , GP.GroupID FROM GP JOIN Lecture ON Lecture.LectureID = GP.LectureID");


            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                importGroups.add(new ImportGroup(resultSet.getString("LectureName"), resultSet.getInt("GroupID")));
            }

        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }
        return importGroups;
    }

    @Override
    public Group getGroup(int index) {
        Group group = null;
        try {
            Statement statement = connection.createStatement();
            Main.AddINFO("SQL: SELECT * FROM Lecture JOIN GP ON Lecture.LectureID = GP.LectureID WHERE GP.GroupID = '%s'".formatted(index));
            statement.execute("SELECT * FROM Lecture JOIN GP ON Lecture.LectureID = GP.LectureID WHERE GP.GroupID = '%s'".formatted(index));


            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                group = new Group(resultSet.getString("LectureID"), resultSet.getString("LectureName"), resultSet.getString("TutorGroup"), resultSet.getInt("GroupID"));
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
}
