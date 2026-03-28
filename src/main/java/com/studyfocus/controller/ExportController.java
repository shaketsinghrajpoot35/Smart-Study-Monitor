package com.studyfocus.controller;

import com.studyfocus.entity.DailyReport;
import com.studyfocus.entity.User;
import com.studyfocus.repository.DailyReportRepository;
import com.studyfocus.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@RestController
public class ExportController {

    private final DailyReportRepository dailyReportRepository;
    private final UserRepository userRepository;

    public ExportController(DailyReportRepository dailyReportRepository, UserRepository userRepository) {
        this.dailyReportRepository = dailyReportRepository;
        this.userRepository = userRepository;
    }

    private User getUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    private List<DailyReport> getLast30Days(User user) {
        LocalDate today = LocalDate.now();
        return dailyReportRepository.findByUserAndDateBetweenOrderByDateAsc(user, today.minusDays(30), today);
    }

    // -------- CSV EXPORT (Per-User, Last 30 days) --------
    @GetMapping("/export/csv")
    public void exportCsv(Authentication auth, HttpServletResponse response) throws Exception {
        User user = getUser(auth);
        if (user == null) { response.sendError(401, "Not authenticated"); return; }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=studyfocus_report_" + user.getUsername() + ".csv");

        PrintWriter writer = response.getWriter();
        writer.println("Date,Study Minutes,Break Minutes,Average Focus Score");

        for (DailyReport r : getLast30Days(user)) {
            writer.printf("%s,%d,%d,%d%n",
                r.getDate(),
                r.getTotalStudySeconds() / 60,
                r.getTotalBreakSeconds() / 60,
                r.getAverageFocusScore()
            );
        }
        writer.close();
    }

    // -------- EXCEL EXPORT (Per-User, Last 30 days) --------
    @GetMapping("/export/excel")
    public void exportExcel(Authentication auth, HttpServletResponse response) throws Exception {
        User user = getUser(auth);
        if (user == null) { response.sendError(401, "Not authenticated"); return; }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=studyfocus_report_" + user.getUsername() + ".xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Study Report");

        // Header row with bold style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row header = sheet.createRow(0);
        String[] columns = {"Date", "Study Minutes", "Break Minutes", "Avg Focus Score"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }

        int rowNum = 1;
        for (DailyReport r : getLast30Days(user)) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getDate().toString());
            row.createCell(1).setCellValue(r.getTotalStudySeconds() / 60);
            row.createCell(2).setCellValue(r.getTotalBreakSeconds() / 60);
            row.createCell(3).setCellValue(r.getAverageFocusScore());
        }

        // Auto-size columns after data
        for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
