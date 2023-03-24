package rc.personal.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rc.personal.app.DTO.MethodNodeDTO;
import rc.personal.app.service.MethodService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api-method/v1")
public class MethodController {

    @Autowired
    private MethodService methodService;

    @GetMapping("/methods")
    public ResponseEntity<List<MethodNodeDTO>> getAllMethods() {
        List<MethodNodeDTO> methods = methodService.getAllMethods();
        return ResponseEntity.ok(methods);
    }

    @PostMapping("/update-from-directory")
    public ResponseEntity<String> updateMethodsFromDirectory() {
        try {
            methodService.updateMethodsFromDirectory();
            return ResponseEntity.ok("Methods updated successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update methods: " + e.getMessage());
        }
    }


}
