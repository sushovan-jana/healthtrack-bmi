package com.healthtrack.bmi.util;

import com.healthtrack.bmi.dto.ClinicSettings;
import com.healthtrack.bmi.entity.BmiCalculation;
import com.healthtrack.bmi.entity.DoctorNote;
import com.healthtrack.bmi.entity.Patient;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
public class PdfGeneratorUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.of("Asia/Kolkata"));

    private static final Color TEAL_PRIMARY = new Color(15, 118, 110);    // #0F766E
    private static final Color SLATE_TEXT = new Color(71, 85, 105);       // #475569
    private static final Color BG_LIGHT = new Color(248, 250, 252);       // #F8FAFC
    private static final Color BORDER_GRAY = new Color(226, 232, 240);    // #E2E8F0

    /**
     * Builds the PDF document using OpenPDF and JFreeChart.
     */
    public byte[] generateReport(
            Patient patient,
            List<BmiCalculation> calculations,
            List<DoctorNote> notes,
            ClinicSettings clinic,
            String reportId,
            Instant generatedAt
    ) throws DocumentException, IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 54);
        PdfWriter writer = PdfWriter.getInstance(document, out);

        // Bind dynamic footer page callback
        String timestampStr = DATE_FORMATTER.format(generatedAt);
        writer.setPageEvent(new FooterPageEvent(clinic, reportId, timestampStr));

        document.open();

        // 1. Clinic Branding Header
        addHeader(document, clinic);
        document.add(new Paragraph("\n"));

        // 2. Patient Demographics Profile
        addPatientDemographics(document, patient);
        document.add(new Paragraph("\n"));

        // 3. Prominent Latest Calculation details
        if (!calculations.isEmpty()) {
            addLatestBmiBox(document, calculations.get(0));
            document.add(new Paragraph("\n"));
        }

        // 4. BMI Progression Trend Chart
        if (calculations.size() > 1) {
            addBmiTrendChart(document, calculations);
            document.add(new Paragraph("\n"));
        }

        // 5. BMI History Table
        addHistoryTable(document, calculations);
        document.add(new Paragraph("\n"));

        // 6. Doctor Recommendations Notes
        addDoctorNotes(document, notes);

        document.close();
        return out.toByteArray();
    }

    private void addHeader(Document document, ClinicSettings clinic) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{60, 40});

        // Left Branding Info
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        Paragraph clinicName = new Paragraph(clinic.getName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Font.BOLD, TEAL_PRIMARY));
        Paragraph subtitle = new Paragraph("BMI Clinical Report", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, SLATE_TEXT));
        leftCell.addElement(clinicName);
        leftCell.addElement(subtitle);
        headerTable.addCell(leftCell);

        // Right Branding Info
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        Paragraph docName = new Paragraph(clinic.getDoctorName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD, SLATE_TEXT));
        Paragraph address = new Paragraph(clinic.getAddress(), FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, SLATE_TEXT));
        Paragraph phones = new Paragraph("Ph: " + String.join(", ", clinic.getPhones()), FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, SLATE_TEXT));
        Paragraph email = new Paragraph("Email: " + clinic.getEmail(), FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, SLATE_TEXT));
        
        rightCell.addElement(docName);
        rightCell.addElement(address);
        rightCell.addElement(phones);
        rightCell.addElement(email);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        // Colored divider bar
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        PdfPCell dividerCell = new PdfPCell();
        dividerCell.setFixedHeight(2);
        dividerCell.setBackgroundColor(TEAL_PRIMARY);
        dividerCell.setBorder(Rectangle.NO_BORDER);
        divider.addCell(dividerCell);
        document.add(divider);
    }

    private void addPatientDemographics(Document document, Patient patient) throws DocumentException {
        document.add(new Paragraph("PATIENT PROFILE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.BOLD, TEAL_PRIMARY)));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{50, 50});

        addDemographicRow(table, "Full Name:", patient.getName());
        addDemographicRow(table, "Mobile Number:", patient.getPhoneNumber());
        addDemographicRow(table, "Age:", String.valueOf(patient.getAge()));
        addDemographicRow(table, "Gender:", patient.getGender());

        document.add(table);
    }

    private void addDemographicRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD, SLATE_TEXT)));
        labelCell.setBorderColor(BORDER_GRAY);
        labelCell.setPadding(6);
        labelCell.setBackgroundColor(BG_LIGHT);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, Color.BLACK)));
        valueCell.setBorderColor(BORDER_GRAY);
        valueCell.setPadding(6);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addLatestBmiBox(Document document, BmiCalculation latest) throws DocumentException {
        document.add(new Paragraph("LATEST BMI SUMMARY", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.BOLD, TEAL_PRIMARY)));
        document.add(new Paragraph("\n"));

        PdfPTable box = new PdfPTable(4);
        box.setWidthPercentage(100);
        box.setWidths(new float[]{25, 25, 25, 25});

        // Color based on WHO categories
        Color alertColor = getCategoryColor(latest.getClassification());

        addBmiBoxCell(box, "Height", latest.getHeight() + " cm", Color.BLACK);
        addBmiBoxCell(box, "Weight", latest.getWeight() + " kg", Color.BLACK);
        addBmiBoxCell(box, "BMI Value", String.valueOf(latest.getBmiValue()), alertColor);
        addBmiBoxCell(box, "Risk Profile", getRiskLevel(latest.getClassification()), alertColor);

        document.add(box);
    }

    private void addBmiBoxCell(PdfPTable table, String label, String value, Color valueColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(BORDER_GRAY);
        cell.setPadding(10);
        cell.setBackgroundColor(BG_LIGHT);
        
        Paragraph labelP = new Paragraph(label.toUpperCase(), FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, SLATE_TEXT));
        labelP.setAlignment(Element.ALIGN_CENTER);
        
        Paragraph valueP = new Paragraph(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD, valueColor));
        valueP.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(labelP);
        cell.addElement(valueP);
        table.addCell(cell);
    }

    private void addBmiTrendChart(Document document, List<BmiCalculation> calculations) throws DocumentException, IOException {
        document.add(new Paragraph("BMI PROGRESSION TIMELINE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.BOLD, TEAL_PRIMARY)));
        document.add(new Paragraph("\n"));

        // Sort calculations chronologically for chart plotting
        List<BmiCalculation> sorted = calculations.stream()
                .sorted(Comparator.comparing(BmiCalculation::getCalculatedAt))
                .toList();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (BmiCalculation calc : sorted) {
            String dateStr = DATE_FORMATTER.format(calc.getCalculatedAt()).substring(5, 10); // MM-DD
            dataset.addValue(calc.getBmiValue(), "BMI", dateStr);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                null, 
                "Measurement Date (MM-DD)", 
                "BMI Value", 
                dataset, 
                PlotOrientation.VERTICAL, 
                false, 
                true, 
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(BORDER_GRAY);
        plot.setRangeGridlinePaint(BORDER_GRAY);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, TEAL_PRIMARY);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);

        // Convert chart to PNG bytes in memory
        BufferedImage image = chart.createBufferedImage(500, 200);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ImageIO.write(image, "png", chartOut);

        Image pdfImage = Image.getInstance(chartOut.toByteArray());
        pdfImage.setAlignment(Element.ALIGN_CENTER);
        document.add(pdfImage);
    }

    private void addHistoryTable(Document document, List<BmiCalculation> calculations) throws DocumentException {
        document.add(new Paragraph("HISTORICAL BMI CALCULATIONS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.BOLD, TEAL_PRIMARY)));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{25, 15, 15, 15, 30});

        // Headers
        addTableHeaderCell(table, "Date");
        addTableHeaderCell(table, "Height");
        addTableHeaderCell(table, "Weight");
        addTableHeaderCell(table, "BMI");
        addTableHeaderCell(table, "WHO Status");

        // Data Rows
        boolean alternate = false;
        for (BmiCalculation calc : calculations) {
            Color rowBg = alternate ? BG_LIGHT : Color.WHITE;
            
            table.addCell(createTableCell(DATE_FORMATTER.format(calc.getCalculatedAt()), rowBg));
            table.addCell(createTableCell(calc.getHeight() + " cm", rowBg));
            table.addCell(createTableCell(calc.getWeight() + " kg", rowBg));
            table.addCell(createTableCell(String.valueOf(calc.getBmiValue()), rowBg));
            
            // Classification status color
            PdfPCell statusCell = createTableCell(calc.getClassification(), rowBg);
            statusCell.getPhrase().getFont().setColor(getCategoryColor(calc.getClassification()));
            table.addCell(statusCell);

            alternate = !alternate;
        }

        document.add(table);
    }

    private void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.BOLD, Color.WHITE)));
        cell.setBackgroundColor(TEAL_PRIMARY);
        cell.setBorderColor(BORDER_GRAY);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private PdfPCell createTableCell(String text, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, Color.BLACK)));
        cell.setBackgroundColor(bgColor);
        cell.setBorderColor(BORDER_GRAY);
        cell.setPadding(6);
        return cell;
    }

    private void addDoctorNotes(Document document, List<DoctorNote> notes) throws DocumentException {
        document.add(new Paragraph("DOCTOR RECOMMENDATIONS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.BOLD, TEAL_PRIMARY)));
        document.add(new Paragraph("\n"));

        if (notes.isEmpty()) {
            document.add(new Paragraph("No recommendations recorded.", FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, SLATE_TEXT)));
            return;
        }

        com.lowagie.text.List bulletList = new com.lowagie.text.List(false, 15);
        bulletList.setListSymbol(new Chunk("\u2022 ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD, TEAL_PRIMARY)));

        for (DoctorNote note : notes) {
            String noteText = String.format("%s (%s)", note.getNote(), DATE_FORMATTER.format(note.getCreatedAt()).substring(0, 10));
            bulletList.add(new ListItem(new Phrase(noteText, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, Color.BLACK))));
        }

        document.add(bulletList);
    }

    private Color getCategoryColor(String classification) {
        if (classification.equalsIgnoreCase("Normal weight")) {
            return new Color(13, 148, 136); // Teal green
        } else if (classification.contains("thinness")) {
            return new Color(217, 119, 6);   // Amber
        } else {
            return new Color(220, 38, 38);   // Red
        }
    }

    private String getRiskLevel(String classification) {
        if (classification.equalsIgnoreCase("Normal weight")) {
            return "Minimal Risk";
        } else if (classification.contains("thinness")) {
            return "Moderate Risk";
        } else if (classification.equalsIgnoreCase("Overweight")) {
            return "Increased Risk";
        } else if (classification.equalsIgnoreCase("Obese Class I")) {
            return "High Risk";
        } else if (classification.equalsIgnoreCase("Obese Class II")) {
            return "Very High Risk";
        } else {
            return "Extremely High Risk";
        }
    }

    /**
     * Inner class to implement headers and footers with page numbers.
     */
    private static class FooterPageEvent extends PdfPageEventHelper {
        private final ClinicSettings clinic;
        private final String reportId;
        private final String timestamp;

        public FooterPageEvent(ClinicSettings clinic, String reportId, String timestamp) {
            this.clinic = clinic;
            this.reportId = reportId;
            this.timestamp = timestamp;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            
            // Left Footer Text
            Phrase footerLeft = new Phrase(
                    String.format("Generated By: %s | Report ID: %s", clinic.getDoctorName(), reportId),
                    FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, SLATE_TEXT)
            );
            
            // Right Footer Text
            Phrase footerRight = new Phrase(
                    String.format("Date: %s | Page %d", timestamp, writer.getPageNumber()),
                    FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, SLATE_TEXT)
            );

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, footerLeft, document.left(), document.bottom() - 20, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footerRight, document.right(), document.bottom() - 20, 0);
        }
    }
}
