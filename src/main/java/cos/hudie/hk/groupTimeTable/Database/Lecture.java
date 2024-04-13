package cos.hudie.hk.groupTimeTable.Database;

public record Lecture(String LectureID , String LectureName) {
    @Override
    public String toString() {
        return "%10s %20s".formatted(LectureID , LectureName);
    }
}
