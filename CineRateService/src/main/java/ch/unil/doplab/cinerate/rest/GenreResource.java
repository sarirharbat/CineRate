package ch.unil.doplab.cinerate.rest;

import ch.unil.doplab.cinerate.domain.Genre;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

@Path("/genres")
public class GenreResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getAllGenres() {
        var labels = new ArrayList<String>();
        for (Genre genre : Genre.values()) {
            labels.add(genre.getLabel());
        }
        return labels;
    }
}