package rc.personal.app.model;

public class Method {
    private String name;
    private String code;
    private String fileName;

    public Method(String name, String code, String fileName) {
        this.name = name;
        this.code = code;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getFileName() {
        return fileName;
    }
}


