package rc.personal.app.mapper;

import org.springframework.stereotype.Component;
import rc.personal.app.DTO.MethodNodeDTO;
import rc.personal.app.DTO.MethodNodeDTOImpl;
import rc.personal.app.model.Method;

import java.util.ArrayList;
import java.util.List;

@Component
public class MethodMapper {
    public static List<MethodNodeDTO> getMethodsByFileName(List<Method> methods) {
        List<MethodNodeDTO> result = new ArrayList<>();

        for (Method method : methods) {
            MethodNodeDTO node = getNodeByFileName(result, method.getFileName());
            if (node == null) {
                node = new MethodNodeDTOImpl(method.getFileName());
                result.add(node);
            }
            node.getChildren().add(new MethodNodeDTOImpl(method.getName()));
        }

        return result;
    }

    private static MethodNodeDTO getNodeByFileName(List<MethodNodeDTO> nodes, String fileName) {
        for (MethodNodeDTO node : nodes) {
            if (node.getFileName().equals(fileName)) {
                return node;
            }
        }
        return null;
    }
}
