package rc.personal.app.DTO;

import java.util.Calendar;
import java.util.List;

public interface MethodNodeDTO {
    String getFileName();
    List<MethodNodeDTO> getChildren();
}

