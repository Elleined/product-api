package com.elleined.marketplaceapi.service.file.order;

import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.service.file.Exporter;
import com.elleined.marketplaceapi.utils.Formatter;
import com.elleined.marketplaceapi.utils.OrderUtils;
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

public interface OrderExporter<T extends Product> extends Exporter<T> {
}
