package com.arianit.costumer;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/api/v1/costumers")
public class CostumerResource {

    @Inject
    CostumerService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<List<Costumer>> getCostumers() {
        return RestResponse.ok(service.findAllCostumers());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Costumer> getCostumerById(@RestPath Long id) {
        Costumer costumer = service.findCostumerById(id);
        if (costumer == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(costumer);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Costumer> createCostumer(@Valid Costumer costumer) {
        costumer = service.persistCostumer(costumer);
        UriBuilder uriBuilder = UriBuilder.fromResource(CostumerResource.class).path(Long.toString(costumer.id));
        return RestResponse.created(uriBuilder.build());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<Costumer> updateCostumer(@Valid Costumer costumer) {
        costumer = service.updateCostumer(costumer);
        if (costumer == null) {
            return RestResponse.notFound();
        }
        return RestResponse.ok(costumer);
    }

    @DELETE
    @Path("/{id}")
    public RestResponse<?> deleteCostumer(@RestPath Long id) {
        service.deleteCostumer(id);
        return RestResponse.noContent();
    }
}
