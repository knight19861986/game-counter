package com.comeon.gamecounter.gateway;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Path("/comeon/gateway/api/v1")
@Produces(MediaType.TEXT_PLAIN)
public class GatewayResource {
    private static final String CORE_BASE_URL = "http://localhost:9090/api";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) {
        try {
            String body = String.format("{\"username\":\"%s\", \"password\":\"%s\"}",
                    credentials.getUsername(), credentials.getPassword());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CORE_BASE_URL + "/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Response.status(response.statusCode()).entity(response.body()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error calling core: " + e.getMessage()).build();
        }
    }

}
