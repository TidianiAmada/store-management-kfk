package com.sts.internals.messaging.schema;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Product {
    private String productId;
    private String productName;
    private String unitPrice;
    private String productOwner;
    private String ownerLocation;
    private String quantityInLocalStore;
}
