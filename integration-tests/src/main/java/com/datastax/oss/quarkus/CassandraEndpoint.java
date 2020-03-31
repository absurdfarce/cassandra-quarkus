package com.datastax.oss.quarkus;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.datastax.oss.quarkus.dao.Product;

@Path("/cassandra")
public class CassandraEndpoint {

    @Inject
    ProductDaoService dao;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/product/{description}")
    public UUID saveProduct(@PathParam("description") String desc) {
        UUID id = UUID.randomUUID();
        dao.getDao().update(new Product(id, desc));
        return id;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/product/{id}")
    public Product getProduct(@PathParam("id") UUID id) {
        return dao.getDao().findById(id);
    }

}
