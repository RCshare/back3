package rc.personal.app.DTO;

import java.util.ArrayList;
import java.util.List;

public class MethodNodeDTOImpl implements MethodNodeDTO {
    private String fileName;
    private List<MethodNodeDTO> children = new ArrayList<>();

    public MethodNodeDTOImpl(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public List<MethodNodeDTO> getChildren() {
        return children;
    }
}