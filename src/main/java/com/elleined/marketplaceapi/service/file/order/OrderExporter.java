package com.elleined.marketplaceapi.service.file.order;

import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.service.file.Exporter;

public interface OrderExporter<T extends Product> extends Exporter<T> {
}
