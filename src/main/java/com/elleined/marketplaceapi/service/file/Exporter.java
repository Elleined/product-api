package com.elleined.marketplaceapi.service.file;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface Exporter<T> {
    void export(HttpServletResponse httpServletResponse, T t)
            throws DocumentException, IOException;

    void export(HttpServletResponse httpServletResponse, T t, LocalDate start, LocalDate end)
            throws DocumentException, IOException;

    default void writeColumnNames(PdfPTable pdfPTable, List<String> columnNames) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        com.lowagie.text.Font columnHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 13);
        columnHeaderFont.setStyle(Font.BOLD);
        columnNames.forEach(columnName -> {
            cell.setPhrase(new Phrase(columnName, columnHeaderFont));
            pdfPTable.addCell(cell);
        });
    }
}
