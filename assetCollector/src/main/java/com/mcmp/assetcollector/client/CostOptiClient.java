package com.mcmp.assetcollector.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/costopti/be")
public interface CostOptiClient {

    @GetExchange("/updateRscMeta")
    ResponseEntity<Void> updateSvcGrpMeta();
}
