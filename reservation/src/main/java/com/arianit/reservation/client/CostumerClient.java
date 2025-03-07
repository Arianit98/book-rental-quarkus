package com.arianit.reservation.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

@RegisterRestClient(configKey = "costumer-api")
@Path("/api/v1/costumers")
public interface CostumerClient {

    @GET
    RestResponse<?> getCostumers();

    @GET
    @Path("/{id}")
    RestResponse<?> getCostumer(@RestPath("id") Long id);
}
