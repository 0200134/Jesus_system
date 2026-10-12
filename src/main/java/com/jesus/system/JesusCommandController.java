package com.jesus.system;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/jesus")
public class JesusCommandController {

    @PostMapping("/eject-and-resurrect")
    public ResponseEntity<Map<String, Object>> manualEjectAndResurrect(
            @RequestParam(defaultValue = "admin-override") String authorityToken) {
        
        System.out.println("[⚠️ EMERGENCY] Manual Ejection triggered! Token: " + authorityToken);
        
        return ResponseEntity.ok(Map.of(
            "status", "SUCCESS",
            "action", "EJECT_AND_RESURRECT_EXECUTED",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
