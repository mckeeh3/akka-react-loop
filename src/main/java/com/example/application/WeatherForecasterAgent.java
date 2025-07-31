package com.example.application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.javasdk.agent.Agent;
import akka.javasdk.agent.ModelProvider;
import akka.javasdk.annotations.ComponentId;
import akka.javasdk.annotations.FunctionTool;

@ComponentId("weather-forecaster-agent")
public class WeatherForecasterAgent extends Agent {
  static Logger log = LoggerFactory.getLogger(WeatherForecasterAgent.class);

  private static final String systemPrompt = """
      You are a comprehensive weather assistant that provides weather information, clothing recommendations, activity suggestions, and travel advice.

      Guidelines for your responses:
      - Use the weatherForecast function to get current weather data first
      - For date handling: if no date is mentioned, use "today". If relative dates like "tomorrow" or "next Tuesday" are mentioned, use today's date as reference to calculate the actual date
      - Use the activitySuggestions function to get activity options based on weather
      - Pick the most suitable activity from the suggestions
      - Use the clothingRecommendations function with the chosen activity to get specific clothing advice
      - Use the travelAdvice function to provide travel-related recommendations
      - Be friendly and helpful in your responses
      - Consider temperature, precipitation, and other weather factors when giving advice
      - Keep responses concise but informative
      - Coordinate all the information to provide comprehensive advice
      - Always explain your reasoning for choosing the activity
      - When mentioning dates in your response, be clear about which date you're referring to
      """.stripIndent();

  private final Random random = new Random();

  public Effect<String> forecast(Prompt prompt) {
    log.info("forecast called with prompt: {}", prompt);
    // Get today's date for reference
    var today = LocalDate.now();
    var todayFormatted = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    return effects()
        .model(ModelProvider
            .openAi()
            .withModelName("gpt-4.1")
            .withApiKey(System.getenv("OPENAI_API_KEY")))
        .systemMessage(systemPrompt + "\n\nToday's date is: " + todayFormatted)
        .userMessage(prompt.userPrompt())
        .thenReply();
  }

  @FunctionTool(description = "Get current weather forecast for a location and date")
  public String weatherForecast(String location, String date) {
    log.info("weatherForecast called with location: {}, date: {}", location, date);

    // Get today's date for reference
    var today = LocalDate.now();
    var todayFormatted = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    // Generate random weather data
    var conditions = new String[] { "sunny", "rainy", "cloudy", "snowy", "partly cloudy" };
    var condition = conditions[random.nextInt(conditions.length)];

    var lowTemp = random.nextInt(25) - 5; // -5 to 20°C
    var highTemp = lowTemp + random.nextInt(15) + 5; // 5-20°C above low

    var forecast = String.format("Weather forecast for %s on %s: %s with temperatures ranging from %d°C to %d°C (Today's date: %s)",
        location, date, condition, lowTemp, highTemp, todayFormatted);

    log.info("weatherForecast returning: {}", forecast);

    return forecast;
  }

  @FunctionTool(description = "Get clothing recommendations based on weather conditions, temperature, and specific activity")
  public String clothingRecommendations(String weather, String temperature, String activity) {
    log.info("clothingRecommendations called with weather: {}, temperature: {}, activity: {}", weather, temperature, activity);

    String[] recommendations;

    // Base recommendations based on weather
    if (weather.contains("rainy")) {
      recommendations = new String[] {
          "Waterproof raincoat or umbrella",
          "Waterproof boots or shoes",
          "Warm layers underneath",
          "Hat to keep head dry"
      };
    } else if (weather.contains("snowy")) {
      recommendations = new String[] {
          "Heavy winter coat",
          "Insulated boots",
          "Warm hat, scarf, and gloves",
          "Thermal underwear"
      };
    } else if (weather.contains("sunny")) {
      recommendations = new String[] {
          "Light, breathable clothing",
          "Sunglasses and hat",
          "Sunscreen (SPF 30+)",
          "Comfortable walking shoes"
      };
    } else {
      recommendations = new String[] {
          "Light jacket or sweater",
          "Comfortable walking shoes",
          "Layered clothing for temperature changes"
      };
    }

    // Activity-specific additions
    if (activity.contains("hiking") || activity.contains("walking")) {
      recommendations = new String[] {
          "Sturdy hiking boots or trail shoes",
          "Moisture-wicking clothing",
          "Backpack with water and snacks",
          "Weather-appropriate outer layer"
      };
    } else if (activity.contains("museum") || activity.contains("indoor")) {
      recommendations = new String[] {
          "Comfortable indoor shoes",
          "Light layers for temperature control",
          "Small bag for essentials",
          "Weather-appropriate outer layer for travel"
      };
    } else if (activity.contains("dining") || activity.contains("restaurant")) {
      recommendations = new String[] {
          "Smart casual attire",
          "Comfortable shoes for walking",
          "Weather-appropriate outer layer",
          "Small bag for essentials"
      };
    } else if (activity.contains("sports") || activity.contains("outdoor")) {
      recommendations = new String[] {
          "Athletic wear appropriate for the sport",
          "Sports shoes with good traction",
          "Weather-appropriate outer layer",
          "Water bottle and energy snacks"
      };
    } else if (activity.contains("shopping")) {
      recommendations = new String[] {
          "Comfortable walking shoes",
          "Layered clothing for indoor/outdoor transitions",
          "Weather-appropriate outer layer",
          "Comfortable bag for purchases"
      };
    }

    var recommendation = String.join(", ", recommendations);
    log.info("clothingRecommendations returning: {}", recommendation);
    return recommendation;
  }

  @FunctionTool(description = "Get activity suggestions based on weather conditions and temperature")
  public String activitySuggestions(String weather, String temperature) {
    log.info("activitySuggestions called with weather: {}, temperature: {}", weather, temperature);

    String[] activities;
    if (weather.contains("rainy")) {
      activities = new String[] {
          "Visit indoor museums or galleries",
          "Go to a movie theater",
          "Visit a local café or restaurant",
          "Indoor shopping or markets"
      };
    } else if (weather.contains("snowy")) {
      activities = new String[] {
          "Build a snowman or have a snowball fight",
          "Go sledding or skiing",
          "Visit a warm indoor spa",
          "Hot chocolate at a cozy café"
      };
    } else if (weather.contains("sunny")) {
      activities = new String[] {
          "Outdoor walking or hiking",
          "Visit parks or gardens",
          "Outdoor dining or picnics",
          "Outdoor sports or activities"
      };
    } else {
      activities = new String[] {
          "Moderate outdoor activities",
          "Visit local attractions",
          "Walking tours",
          "Outdoor dining with light jacket"
      };
    }

    var activity = String.join(", ", activities);
    log.info("activitySuggestions returning: {}", activity);
    return activity;
  }

  @FunctionTool(description = "Get travel advice based on weather conditions and location")
  public String travelAdvice(String weather, String location) {
    log.info("travelAdvice called with weather: {}, location: {}", weather, location);

    String[] advice;
    if (weather.contains("rainy")) {
      advice = new String[] {
          "Expect traffic delays due to rain",
          "Public transportation may be crowded",
          "Allow extra travel time",
          "Consider indoor attractions as backup plans"
      };
    } else if (weather.contains("snowy")) {
      advice = new String[] {
          "Check for road closures and delays",
          "Public transportation may have delays",
          "Allow significant extra travel time",
          "Consider remote work if possible"
      };
    } else if (weather.contains("sunny")) {
      advice = new String[] {
          "Normal travel conditions expected",
          "Good day for outdoor activities",
          "Consider walking or cycling for short distances",
          "Plan for outdoor dining options"
      };
    } else {
      advice = new String[] {
          "Standard travel conditions",
          "Moderate outdoor activities possible",
          "Flexible plans recommended"
      };
    }

    var travelAdvice = String.join(", ", advice);
    log.info("travelAdvice returning: {}", travelAdvice);
    return travelAdvice;
  }

  public record Prompt(String sessionId, String userPrompt) {}
}