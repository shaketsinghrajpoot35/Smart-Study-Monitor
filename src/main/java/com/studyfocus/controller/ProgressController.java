package com.studyfocus.controller;
import java.util.List;
import java.util.ArrayList;
import com.studyfocus.model.Progress;
import com.studyfocus.service.ProgressService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService ps) {
        this.progressService = ps;
    }

    @GetMapping("/progress")
    public List<Progress> getProgress() {
        return progressService.getAll();
    }
}
