package com.elleined.marketplaceapi.service.file;

import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.utils.Formatter;
import com.elleined.marketplaceapi.utils.OrderItemUtils;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class OrderItemExporter implements Exporter<List<OrderItem>> {

    @Override
    public void export(HttpServletResponse response, List<OrderItem> orderItems) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 2, 2, 2, 2);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.addTitle("Sales Report");
        document.addCreationDate();

        document.open();
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 20);
        headerFont.setStyle(Font.BOLDITALIC);
        Paragraph header = new Paragraph("Sales Report", headerFont);
        header.setAlignment(Paragraph.ALIGN_CENTER);

        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[] {2, 2, 2, 2, 2});
        table.setSpacingBefore(10);

        writeColumnNames(table, Arrays.asList("Product id", "Product name", "Total price", "Order quantity", "Date sold"));
        writeTableData(table, orderItems);

        document.add(header);
        document.add(table);

        document.close();
        log.debug("Pdf generation success!");
    }

    @Override
    public void export(HttpServletResponse response, List<OrderItem> orderItems, LocalDate start, LocalDate end) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 2, 2, 2, 2);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.addTitle("Sales report");
        document.addCreationDate();

        document.open();
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 20);
        headerFont.setStyle(Font.BOLDITALIC);
        Paragraph header = new Paragraph("Sales report", headerFont);
        header.setAlignment(Paragraph.ALIGN_CENTER);

        PdfPTable table = new PdfPTable(5);
        table.setWidths(new int[] {2, 2, 2, 2, 2});
        table.setSpacingBefore(10);

        writeColumnNames(table, Arrays.asList("Product id", "Product name", "Total price", "Order quantity", "Date sold"));
        List<OrderItem> rangedOrderItems = OrderItemUtils.getOrderItemsByRange(orderItems, start, end);
        writeTableData(table, rangedOrderItems);

        document.add(header);
        document.add(table);

        document.close();
        log.debug("Pdf generation success!");
    }

    private void writeTableData(PdfPTable table, List<OrderItem> orders) {
        for (OrderItem orderItem : orders) {
            table.addCell(String.valueOf(orderItem.getProduct().getId()));
            table.addCell(orderItem.getProduct().getCrop().getName());
            table.addCell(String.valueOf(orderItem.getPrice()));
            table.addCell(String.valueOf(orderItem.getOrderQuantity()));
            table.addCell(Formatter.formatDate(orderItem.getUpdatedAt()));
        }
    }

    private void writeColumnNames(PdfPTable pdfPTable, List<String> columnNames) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font columnHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 13);
        columnHeaderFont.setStyle(Font.BOLD);
        columnNames.forEach(columnName -> {
            cell.setPhrase(new Phrase(columnName, columnHeaderFont));
            pdfPTable.addCell(cell);
        });
    }
}
