package cos.hudie.hk.groupTimeTable.Database.H2;

import cos.hudie.hk.groupTimeTable.Main;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static cos.hudie.hk.groupTimeTable.FileUtils.Import.ImportFromTXT.FilePath;

public class Query extends cos.hudie.hk.groupTimeTable.Database.Query {
    public Query() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:%s/Members".formatted(FilePath), "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            Main.contactDeveloper(e);
        } catch (ClassNotFoundException e) {
            Main.contactDeveloper(e);
            e.printStackTrace();
        }
        try {
            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS Lecture(LectureName CHAR(50) , LectureID CHAR(8) , PRIMARY KEY(LectureID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS Lecture(LectureName CHAR(50) , LectureID CHAR(8) , PRIMARY KEY(LectureID))");

            statement.execute("CREATE TABLE IF NOT EXISTS GP(GroupID INTEGER AUTO_INCREMENT , LectureID CHAR(8) , TutorGroup CHAR(4) , PRIMARY KEY(GroupID) , FOREIGN KEY(LectureID) REFERENCES Lecture(LectureID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS GP(GroupID INTEGER AUTO_INCREMENT , LectureID CHAR(8) , TutorGroup CHAR(4) , PRIMARY KEY(GroupID) , FOREIGN KEY(LectureID) REFERENCES Lecture(LectureID))");

            statement.execute("CREATE TABLE IF NOT EXISTS Memo(MemoID INTEGER AUTO_INCREMENT, GroupID INTEGER NOT NULL, MemoName TEXT" +
                    ", StartingTime DATETIME, EndingTime DATETIME, Description TEXT ,FOREIGN KEY(GroupID) REFERENCES GP(GroupID) , PRIMARY KEY(MemoID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS Memo(MemoID INTEGER AUTO_INCREMENT, GroupID INTEGER NOT NULL, MemoName TEXT" +
                    ", StartingTime DATETIME, EndingTime DATETIME, Description TEXT ,FOREIGN KEY(GroupID) REFERENCES GP(GroupID) , PRIMARY KEY(MemoID))");

            statement.execute("CREATE TABLE IF NOT EXISTS Student(StudentID CHAR(9) NOT NULL, StudentName TEXT , PRIMARY KEY(StudentID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS Student(StudentID CHAR(9) NOT NULL, StudentName TEXT , PRIMARY KEY(StudentID))");

            statement.execute("CREATE TABLE IF NOT EXISTS GroupMember(GroupID INTEGER NOT NULL, StudentID CHAR(9) NOT NULL, IsGroupLeader BIT ,FOREIGN KEY(GroupID) REFERENCES GP(GroupID) , FOREIGN KEY(StudentID) REFERENCES Student(StudentID), PRIMARY KEY(GroupID , StudentID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS GroupMember(GroupID INTEGER NOT NULL, StudentID CHAR(9) NOT NULL, IsGroupLeader BIT ,FOREIGN KEY(GroupID) REFERENCES GP(GroupID) , FOREIGN KEY(StudentID) REFERENCES Student(StudentID), PRIMARY KEY(GroupID , StudentID))");

            statement.execute("CREATE TABLE IF NOT EXISTS TimeTable(ID INTEGER AUTO_INCREMENT , StudentID CHAR(9) NOT NULL , StartingTime NUMERIC(4,0) , EndingTime NUMERIC(4,0) , DayOfWeek ENUM('MONDAY' , 'TUESDAY' , 'WEDNESDAY' , 'THURSDAY' , 'FRIDAY' , 'SATURDAY' , 'SUNDAY') , FOREIGN KEY(StudentID) REFERENCES Student(StudentID) , PRIMARY KEY(ID))");
            Main.AddINFO("SQL :" +"CREATE TABLE IF NOT EXISTS TimeTable(ID INTEGER AUTO_INCREMENT , StudentID CHAR(9) NOT NULL , StartingTime NUMERIC(4,0) , EndingTime NUMERIC(4,0) , DayOfWeek ENUM('MONDAY' , 'TUESDAY' , 'WEDNESDAY' , 'THURSDAY' , 'FRIDAY' , 'SATURDAY' , 'SUNDAY') , FOREIGN KEY(StudentID) REFERENCES Student(StudentID) , PRIMARY KEY(ID))");

        } catch (SQLException e) {
            Main.contactDeveloper(e);
        }

    }
}
