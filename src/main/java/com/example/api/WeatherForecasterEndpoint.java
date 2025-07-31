package com.example.api;

import com.example.application.WeatherForecasterAgent;

import akka.javasdk.annotations.Acl;
import akka.javasdk.annotations.http.HttpEndpoint;
import akka.javasdk.annotations.http.Post;
import akka.javasdk.client.ComponentClient;

/**
 * This is a simple Akka Endpoint that uses an agent to provide weather forecasts and clothing recommendations.
 */
@Acl(allow = @Acl.Matcher(principal = Acl.Principal.INTERNET))
@HttpEndpoint
public class WeatherForecasterEndpoint {

  public record Request(String user, String prompt) {}

  private final ComponentClient componentClient;

  public WeatherForecasterEndpoint(ComponentClient componentClient) {
    this.componentClient = componentClient;
  }

  @Post("/forecast")
  public String forecast(Request request) {
    var prompt = new WeatherForecasterAgent.Prompt(request.user, request.prompt);

    return componentClient
        .forAgent()
        .inSession(request.user)
        .method(WeatherForecasterAgent::forecast)
        .invoke(prompt);
  }
}