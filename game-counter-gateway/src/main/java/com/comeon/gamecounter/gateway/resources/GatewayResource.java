package com.comeon.gamecounter.gateway.resources;

import com.comeon.gamecounter.gateway.POJO.LoginRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Path("/comeon/gateway/api/v1")
@Produces(MediaType.TEXT_PLAIN)
public class GatewayResource {
    private static final String CORE_BASE_URL = "http://localhost:9090/comeon/core/api/v1";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello ComeOn!";
    }

    @POST
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    public Response signup(@QueryParam("password") String password) {
        if (password == null || password.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing password")
                    .build();
        }

        try {
            String url = CORE_BASE_URL + "/signup?password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Response.status(response.statusCode()).entity(response.body()).build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Error calling core: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        try {
            if (loginRequest == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Request body is missing or malformed").build();
            }

            String requestBody = String.format("{\"playerId\": %d, \"password\": \"%s\"}",
                    loginRequest.getPlayerId(), loginRequest.getPassword());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CORE_BASE_URL + "/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());

            return Response.status(response.statusCode())
                    .entity(response.body())
                    .build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Error calling core: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/logout/{sessionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@PathParam("sessionId") String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing sessionId")
                    .build();
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CORE_BASE_URL + "/logout/" + sessionId))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Response.status(response.statusCode()).entity(response.body()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error calling core: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/hit")
    @Produces(MediaType.TEXT_PLAIN)
    public Response hitGame(@QueryParam("sessionId") String sessionId,
                            @QueryParam("gameCode") String gameCode) {
        if (sessionId == null || sessionId.isBlank() || gameCode == null || gameCode.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing or blank sessionId or gameCode")
                    .build();
        }

        try {
            String url = String.format("%s/hit?sessionId=%s&gameCode=%s",
                    CORE_BASE_URL,
                    URLEncoder.encode(sessionId, StandardCharsets.UTF_8),
                    URLEncoder.encode(gameCode, StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Response.status(response.statusCode()).entity(response.body()).build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity("Error calling core: " + e.getMessage())
                    .build();
        }
    }
}
