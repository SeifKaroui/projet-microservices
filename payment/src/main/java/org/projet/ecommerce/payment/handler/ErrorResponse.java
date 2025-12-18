package org.projet.ecommerce.payment.handler;

import java.util.Map;

public record ErrorResponse(
    Map<String, String> errors
) {

}
