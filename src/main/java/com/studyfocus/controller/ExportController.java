package com.studyfocus.controller;

import com.studyfocus.model.Progress;
import com.studyfocus.service.ProgressService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@RestController
public class ExportController {

    private final ProgressService progressService;

    public ExportController(ProgressService progressService) {
        this.progressService = progressService;
    }

    // -------- CSV EXPORT --------
    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=progress.csv");

        PrintWriter writer = response.getWriter();
        writer.println("State,Time");

        for (Progress p : progressService.getAll()) {
            writer.println(p.state + "," + p.time);
        }
        writer.close();
    }

    // -------- EXCEL EXPORT --------
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws Exception {

        response.setContentType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(
            "Content-Disposition", "attachment; filename=progress.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Progress");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("State");
        header.createCell(1).setCellValue("Time");

        int rowNum = 1;
        for (Progress p : progressService.getAll()) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.state);
            row.createCell(1).setCellValue(p.time.toString());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
