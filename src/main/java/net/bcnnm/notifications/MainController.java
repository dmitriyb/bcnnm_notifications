package net.bcnnm.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Path("/api")
public class MainController {
    @Autowired
    private StubRepository repository;

    @GET
    @Path("/save/{name}/{content}")
    @Produces(MediaType.APPLICATION_JSON)
    public Stub saveStub(@PathParam("name") String name, @PathParam("content") String content) {
        Stub newStub = new Stub(name, content);
        repository.save(newStub);
        return newStub;
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Stub> saveStub(@QueryParam("name") String name) {
        if (name == null) {
            return repository.findAll();
        }
        return Arrays.asList(repository.findByName(name));
    }
}
