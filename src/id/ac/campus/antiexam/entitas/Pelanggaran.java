package id.ac.campus.antiexam.entitas;

public class Pelanggaran {
    private int id;
    private int sessionId;
    private String violationCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getPelanggaranCode() {
        return violationCode;
    }

    public void setPelanggaranCode(String violationCode) {
        this.violationCode = violationCode;
    }
}

