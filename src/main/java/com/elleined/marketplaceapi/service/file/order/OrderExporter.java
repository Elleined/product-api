package com.elleined.marketplaceapi.service.file.order;

import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.service.file.Exporter;

public interface OrderExporter<T extends Order> extends Exporter<T> {
}
