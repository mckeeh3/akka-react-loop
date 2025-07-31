package com.example;

import akka.javasdk.ServiceSetup;
import akka.javasdk.annotations.Setup;
import com.typesafe.config.Config;

@Setup
public class Bootstrap implements ServiceSetup {

  public Bootstrap(Config config) {
    if (
      config.getString("akka.javasdk.agent.model-provider").equals("openai") &&
      config.getString("akka.javasdk.agent.openai.api-key").isBlank()
    ) {
      throw new IllegalStateException(
        "No API keys found. Make sure you have OPENAI_API_KEY defined as environment variable, or change the model provider configuration in application.conf to use a different LLM."
      );
    }
  }
}
