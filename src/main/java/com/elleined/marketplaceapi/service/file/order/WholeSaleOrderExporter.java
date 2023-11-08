package com.elleined.marketplaceapi.service.file.order;

import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.utils.Formatter;
import com.elleined.marketplaceapi.utils.OrderUtils;
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
public class WholeSaleOrderExporter implements OrderExporter<WholeSaleProduct> {
    
    @Override
    public void export(HttpServletResponse response, WholeSaleProduct wholeSaleProduct) throws DocumentException, IOException {
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
        writeTableData(table, wholeSaleProduct.getWholeSaleOrders());

        document.add(header);
        document.add(table);

        document.close();
        log.debug("Pdf generation success!");
    }

    @Override
    public void export(HttpServletResponse response, WholeSaleProduct wholeSaleProduct, LocalDate start, LocalDate end) throws DocumentException, IOException {
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
        List<WholeSaleOrder> rangedWholeSaleOrders = OrderUtils.getOrdersByDateRange(wholeSaleProduct.getWholeSaleOrders(), start, end);
        writeTableData(table, rangedWholeSaleOrders);

        document.add(header);
        document.add(table);

        document.close();
        log.debug("Pdf generation success!");
    }

    private void writeTableData(PdfPTable table, List<WholeSaleOrder> wholeSaleOrders) {
        wholeSaleOrders.forEach(wholeSaleOrder -> {
            table.addCell(String.valueOf(wholeSaleOrder.getWholeSaleProduct().getSeller().getId()));
            table.addCell(String.valueOf(wholeSaleOrder.getWholeSaleProduct().getId()));
            table.addCell(wholeSaleOrder.getWholeSaleProduct().getCrop().getName());
            table.addCell(String.valueOf(wholeSaleOrder.getWholeSaleProduct().getPrice()));
            table.addCell(String.valueOf(wholeSaleOrder.getWholeSaleProduct().getAvailableQuantity()));
            table.addCell(Formatter.formatDate(wholeSaleOrder.getUpdatedAt()));
        });
    }
}
