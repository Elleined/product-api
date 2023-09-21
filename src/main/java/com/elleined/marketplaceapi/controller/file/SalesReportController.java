package com.elleined.marketplaceapi.controller.file;

import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.file.Exporter;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/export/pdf")
public class SalesReportController {

    private final UserService userService;
    private final SellerService sellerService;

    private final Exporter<List<OrderItem>> orderItemExporter;

    public SalesReportController(UserService userService,
                                 @Qualifier("sellerServiceImpl") SellerService sellerService,
                                 Exporter<List<OrderItem>> orderItemExporter) {
        this.userService = userService;
        this.sellerService = sellerService;
        this.orderItemExporter = orderItemExporter;
    }

    @GetMapping("/sales-report-by-date-range")
    public void exportSalesReport(HttpServletResponse response,
                          @PathVariable("userId") int userId,
                          @RequestParam("startDate") LocalDate start,
                          @RequestParam("endDate") LocalDate end) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=sales-report.pdf";
        response.setHeader(headerKey, headerValue);

        User user = userService.getById(userId);
        List<OrderItem> soldOrders = sellerService.getAllSellerProductOrderByStatus(user, OrderItem.OrderItemStatus.SOLD);
        orderItemExporter.export(response, soldOrders, start, end);
    }

    @GetMapping("/sales-report")
    public void exportSalesReport(HttpServletResponse response,
                 @PathVariable("userId") int userId) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=sales-report.pdf";
        response.setHeader(headerKey, headerValue);

        User user = userService.getById(userId);
        List<OrderItem> soldOrders = sellerService.getAllSellerProductOrderByStatus(user, OrderItem.OrderItemStatus.SOLD);
        orderItemExporter.export(response, soldOrders);
    }
}
