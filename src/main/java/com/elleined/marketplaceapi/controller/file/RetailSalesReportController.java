package com.elleined.marketplaceapi.controller.file;

import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.file.order.RetailOrderExporter;
import com.elleined.marketplaceapi.service.order.OrderService;
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
@RequestMapping("/users/{userId}/export/pdf/retail-orders")
public class RetailSalesReportController {
    private final UserService userService;
    private final OrderService<RetailOrder> retailOrderService;
    private final RetailOrderExporter retailOrderExporter;

    @GetMapping("/sales-report")
    public void export(HttpServletResponse response,
                       @PathVariable("userId") int userId) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=sales-report.pdf";
        response.setHeader(headerKey, headerValue);

        User user = userService.getById(userId);
        List<RetailOrder> soldOrders = retailOrderService.getAllProductOrderByStatus(user, Order.Status.SOLD);

        retailOrderExporter.export(response, soldOrders);
    }

    @GetMapping("/sellers-sales-report")
    public void exportAll(HttpServletResponse response) throws IOException {
        Set<User> sellers = userService.getAllSeller();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=sales-report.pdf";
        response.setHeader(headerKey, headerValue);

        List<RetailOrder> sellersAllSoldOrder = sellers.stream()
                .flatMap(seller -> retailOrderService.getAllProductOrderByStatus(seller, Order.Status.SOLD).stream())
                .toList();

        retailOrderExporter.export(response, sellersAllSoldOrder);
    }

    @GetMapping("/sales-report-by-date-range")
    public void export(HttpServletResponse response,
                          @PathVariable("userId") int userId,
                          @RequestParam("startDate") LocalDate start,
                          @RequestParam("endDate") LocalDate end) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=sales-report.pdf";
        response.setHeader(headerKey, headerValue);

        User user = userService.getById(userId);
        List<RetailOrder> soldOrders = retailOrderService.getAllProductOrderByStatus(user, Order.Status.SOLD);

        retailOrderExporter.export(response, soldOrders, start, end);
    }

    @GetMapping("/sellers-sales-report-by-range")
    public void exportAll(HttpServletResponse response,
                                     @RequestParam("startDate") LocalDate start,
                                     @RequestParam("endDate") LocalDate end) throws IOException {
        Set<User> sellers = userService.getAllSeller();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=sales-report.pdf";
        response.setHeader(headerKey, headerValue);

        List<RetailOrder> sellersAllSoldOrder = sellers.stream()
                .flatMap(seller -> retailOrderService.getAllProductOrderByStatus(seller, Order.Status.SOLD).stream())
                .toList();

        retailOrderExporter.export(response, sellersAllSoldOrder, start, end);
    }
}
