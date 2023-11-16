package com.elleined.marketplaceapi.service.file.order;

import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.service.file.Exporter;
import com.elleined.marketplaceapi.service.order.OrderService;
import com.elleined.marketplaceapi.utils.Formatter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class RetailOrderExporter implements OrderExporter<RetailOrder> {

    private void writeTableData(PdfPTable table, List<RetailOrder> retailOrders) {
        retailOrders.forEach(retailOrder -> {
            table.addCell(String.valueOf(retailOrder.getRetailProduct().getSeller().getId()));
            table.addCell(String.valueOf(retailOrder.getRetailProduct().getId()));
            table.addCell(retailOrder.getRetailProduct().getCrop().getName());
            table.addCell(String.valueOf(retailOrder.getPrice()));
            table.addCell(String.valueOf(retailOrder.getOrderQuantity()));
            table.addCell(Formatter.formatDate(retailOrder.getUpdatedAt()));
        });
    }

    @Override
    public void export(HttpServletResponse response, List<RetailOrder> retailOrders) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 2, 2, 2, 2);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.addTitle("Sales Report");
        document.addCreationDate();

        document.open();
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 20);
        headerFont.setStyle(Font.BOLDITALIC);
        Paragraph header = new Paragraph("Sales Report", headerFont);
        header.setAlignment(Paragraph.ALIGN_CENTER);

        PdfPTable table = new PdfPTable(6);
        table.setWidths(new int[] {2, 2, 2, 2, 2 ,2});
        table.setSpacingBefore(10);

        writeColumnNames(table, Arrays.asList("Seller id", "Product id", "Product name", "Total price", "Order quantity", "Date sold"));
        writeTableData(table, retailOrders);

        document.add(header);
        document.add(table);

        document.close();
        log.debug("Pdf generation success!");
    }

    @Override
    public void export(HttpServletResponse response, List<RetailOrder> retailOrders, LocalDate start, LocalDate end) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 2, 2, 2, 2);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.addTitle("Sales report");
        document.addCreationDate();

        document.open();
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 20);
        headerFont.setStyle(Font.BOLDITALIC);
        Paragraph header = new Paragraph("Sales report", headerFont);
        header.setAlignment(Paragraph.ALIGN_CENTER);

        PdfPTable table = new PdfPTable(6);
        table.setWidths(new int[] {2, 2, 2, 2, 2, 2});
        table.setSpacingBefore(10);

        writeColumnNames(table, Arrays.asList("Seller id", "Product id", "Product name", "Total price", "Order quantity", "Date sold"));
        List<RetailOrder> rangedRetailOrders = OrderService.getByDateRange(retailOrders, start, end);
        writeTableData(table, rangedRetailOrders);

        document.add(header);
        document.add(table);

        document.close();
        log.debug("Pdf generation success!");
    }
}
