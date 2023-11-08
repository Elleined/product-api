package com.elleined.marketplaceapi.controller.file;

import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.file.Exporter;
import com.elleined.marketplaceapi.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/export/pdf")
public class SalesReportController {

    private final UserService userService;
    private final SellerGetAllService sellerGetAllService;

    private final Exporter<List<OrderItem>> orderItemExporter;

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
        List<OrderItem> soldOrders = sellerGetAllService.getAllSellerProductOrderByStatus(user, OrderItem.OrderItemStatus.SOLD);
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
        List<OrderItem> soldOrders = sellerGetAllService.getAllSellerProductOrderByStatus(user, OrderItem.OrderItemStatus.SOLD);
        orderItemExporter.export(response, soldOrders);
    }

    @GetMapping("/sellers-sales-report")
    public void exportAllSalesReport(HttpServletResponse response) throws IOException {
        Set<User> sellers = userService.getAllSeller();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=sales-report.pdf";
        response.setHeader(headerKey, headerValue);

        List<OrderItem> sellersAllSoldOrder = sellers.stream()
                .flatMap(seller -> sellerGetAllService.getAllSellerProductOrderByStatus(seller, OrderItem.OrderItemStatus.SOLD).stream())
                .toList();
        orderItemExporter.export(response, sellersAllSoldOrder);
    }

    @GetMapping("/sellers-sales-report-by-range")
    public void exportAllSalesReport(HttpServletResponse response,
                                     @RequestParam("startDate") LocalDate start,
                                     @RequestParam("endDate") LocalDate end) throws IOException {
        Set<User> sellers = userService.getAllSeller();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=sales-report.pdf";
        response.setHeader(headerKey, headerValue);

        List<OrderItem> sellersAllSoldOrder = sellers.stream()
                .flatMap(seller -> sellerGetAllService.getAllSellerProductOrderByStatus(seller, OrderItem.OrderItemStatus.SOLD).stream())
                .toList();
        orderItemExporter.export(response, sellersAllSoldOrder, start, end);
    }
}
