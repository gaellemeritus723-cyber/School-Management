package com.example.school.controller;

import com.example.school.repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ReportController {

    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final CourseRepository courseRepo;

    public ReportController(StudentRepository s, TeacherRepository t, CourseRepository c) {
        this.studentRepo = s; this.teacherRepo = t; this.courseRepo = c;
    }

    @GetMapping("/report/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public void generatePdf(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=rapport_ecole.pdf");

        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // ─── Fonts ───────────────────────────────────────────────
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, new BaseColor(33, 97, 140));
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        BaseColor primaryColor = new BaseColor(33, 97, 140);
        BaseColor lightGray = new BaseColor(240, 240, 240);

        // ─── Header ──────────────────────────────────────────────
        Paragraph title = new Paragraph("Rapport de Gestion Scolaire", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(5);
        doc.add(title);

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph dateP = new Paragraph("Généré le : " + date, normalFont);
        dateP.setAlignment(Element.ALIGN_CENTER);
        dateP.setSpacingAfter(20);
        doc.add(dateP);

        // Horizontal line
        doc.add(new Chunk(new LineSeparator()));
        doc.add(Chunk.NEWLINE);

        // ─── Summary ─────────────────────────────────────────────
        addSectionTitle(doc, "Vue d'ensemble", sectionFont, primaryColor);

        PdfPTable summaryTable = new PdfPTable(3);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingAfter(20);
        addColoredCell(summaryTable, "Etudiants\n" + studentRepo.count(), primaryColor, headerFont);
        addColoredCell(summaryTable, "Enseignants\n" + teacherRepo.count(), new BaseColor(39, 174, 96), headerFont);
        addColoredCell(summaryTable, "Cours\n" + courseRepo.count(), new BaseColor(142, 68, 173), headerFont);
        doc.add(summaryTable);

        // ─── Students ────────────────────────────────────────────
        addSectionTitle(doc, "Liste des Etudiants", sectionFont, primaryColor);
        PdfPTable stTable = new PdfPTable(new float[]{1, 3, 3, 2});
        stTable.setWidthPercentage(100);
        stTable.setSpacingAfter(20);
        addTableHeader(stTable, primaryColor, headerFont, "ID", "Nom", "Email", "Téléphone");
        boolean alt = false;
        for (var s : studentRepo.findAll()) {
            BaseColor bg = alt ? lightGray : BaseColor.WHITE;
            addTableRow(stTable, bg, normalFont,
                    String.valueOf(s.getId()), s.getName(),
                    s.getEmail() != null ? s.getEmail() : "-",
                    s.getPhone() != null ? s.getPhone() : "-");
            alt = !alt;
        }
        doc.add(stTable);

        // ─── Teachers ────────────────────────────────────────────
        addSectionTitle(doc, "Liste des Enseignants", sectionFont, new BaseColor(39, 174, 96));
        PdfPTable tTable = new PdfPTable(new float[]{1, 3, 3, 2});
        tTable.setWidthPercentage(100);
        tTable.setSpacingAfter(20);
        addTableHeader(tTable, new BaseColor(39, 174, 96), headerFont, "ID", "Nom", "Matière", "Email");
        alt = false;
        for (var t : teacherRepo.findAll()) {
            BaseColor bg = alt ? lightGray : BaseColor.WHITE;
            addTableRow(tTable, bg, normalFont,
                    String.valueOf(t.getId()), t.getName(),
                    t.getSubject() != null ? t.getSubject() : "-",
                    t.getEmail() != null ? t.getEmail() : "-");
            alt = !alt;
        }
        doc.add(tTable);

        // ─── Courses ─────────────────────────────────────────────
        addSectionTitle(doc, "Liste des Cours", sectionFont, new BaseColor(142, 68, 173));
        PdfPTable cTable = new PdfPTable(new float[]{1, 3, 1, 3});
        cTable.setWidthPercentage(100);
        cTable.setSpacingAfter(20);
        addTableHeader(cTable, new BaseColor(142, 68, 173), headerFont, "ID", "Titre", "Crédits", "Enseignant");
        alt = false;
        for (var c : courseRepo.findAll()) {
            BaseColor bg = alt ? lightGray : BaseColor.WHITE;
            addTableRow(cTable, bg, normalFont,
                    String.valueOf(c.getId()), c.getTitle(),
                    c.getCredits() != null ? String.valueOf(c.getCredits()) : "-",
                    c.getTeacher() != null ? c.getTeacher().getName() : "-");
            alt = !alt;
        }
        doc.add(cTable);

        // ─── Footer ──────────────────────────────────────────────
        doc.add(new Chunk(new LineSeparator()));
        Paragraph footer = new Paragraph("School Management System — Rapport confidentiel", normalFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);

        doc.close();
    }

    private void addSectionTitle(Document doc, String text, Font font, BaseColor bg) throws DocumentException {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        t.setSpacingBefore(10);
        t.setSpacingAfter(8);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(8);
        cell.setBorder(Rectangle.NO_BORDER);
        t.addCell(cell);
        doc.add(t);
    }

    private void addColoredCell(PdfPTable table, String text, BaseColor bg, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(15);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
    }

    private void addTableHeader(PdfPTable table, BaseColor bg, Font font, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, font));
            cell.setBackgroundColor(bg);
            cell.setPadding(8);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, BaseColor bg, Font font, String... values) {
        for (String v : values) {
            PdfPCell cell = new PdfPCell(new Phrase(v, font));
            cell.setBackgroundColor(bg);
            cell.setPadding(7);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        }
    }
}
