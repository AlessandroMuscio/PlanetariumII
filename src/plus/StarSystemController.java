package plus;

import java.util.LinkedList;

import it.kibo.fp.lib.AnsiColors;
import it.kibo.fp.lib.InputData;
import it.kibo.fp.lib.Menu;

public class StarSystemController extends Menu {
  private final String STAR_COLORS = "" + AnsiColors.YELLOW_BACKGROUND + AnsiColors.BLACK + "Star" + AnsiColors.RESET;
  private final String PLANET_COLORS = "" + AnsiColors.BLUE_BACKGROUND + AnsiColors.BLACK + "Planet" + AnsiColors.RESET;
  private final String SATELLITE_COLORS = "" + AnsiColors.WHITE_BACKGROUND + AnsiColors.BLACK + "Satellite"
      + AnsiColors.RESET;
  private final String ON = AnsiColors.GREEN + "ON" + AnsiColors.RESET;
  private final String OFF = AnsiColors.RED + "OFF" + AnsiColors.RESET;

  private static StarSystemController instance;

  private Star star;
  private boolean relativePositioning;

  private StarSystemController(String title, String[] entries, boolean useExitEntry, boolean centredTitle,
      boolean useVerticalFrame) {
    super(title, entries, useExitEntry, centredTitle, useVerticalFrame);
    star = initializeStarSystem();
  }

  public static StarSystemController getInstance() {
    if (instance == null)
      instance = new StarSystemController("Star System Controller", new String[] {
          "Add a new planet",
          "Add a new satellite",
          "Remove a planet",
          "Remove a satellite",
          "Search a celestial body",
          "View celestial body informations",
          "Calculate center of mass",
          "Toggle relative positioning",
          "Calculate route",
          "Detect collisions"
      }, true, true, false);

    return instance;
  }

  private static void successfulMessage(String message) {
    System.out.printf(AnsiColors.GREEN + "Success!" + AnsiColors.RESET + "\n%s\n", message);
  }

  private static void errorMessage(String message) {
    System.out.printf(AnsiColors.RED + "Error!" + AnsiColors.RESET + "\n%s\n", message);
  }

  // TODO: Remove in production
  private Star createTestingStarSystem() {
    relativePositioning = false;
    Star star = new Star("Sole", 30, new Position(0, 0));

    Planet planetOne = new Planet("Pianeta1", 5, new Position(0, -3));
    Planet planetTwo = new Planet("Pianeta2", 7, new Position(3, 3));

    Satellite satelliteOne = new Satellite("Luna1", 1, new Position(-1, -4), relativePositioning, planetOne);
    Satellite satelliteTwo = new Satellite("Luna2", 2, new Position(2, 3), relativePositioning, planetTwo);
    Satellite satelliteThree = new Satellite("Luna3", 1, new Position(4, 4), relativePositioning, planetTwo);

    planetOne.addSatellite(satelliteOne);

    planetTwo.addSatellite(satelliteTwo);
    planetTwo.addSatellite(satelliteThree);

    star.addPlanet(planetOne);
    star.addPlanet(planetTwo);

    return star;
  }

  private Star initializeStarSystem() {
    String name = InputData.readNonEmptyString("What is your star's name? ", true);

    // TODO: Remove in production
    if (name.equals("sudo"))
      return createTestingStarSystem();

    double mass = InputData.readDoubleWithMinimum(String.format("What is %s mass? ", name), 0);
    Position position = new Position(0, 0);

    relativePositioning = InputData.readYesOrNo("Would you like to use the relative positioning");

    try {
      loadingMessage("Your star system is being created");
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      successfulMessage("Star system creation completed");

      try {
        wait(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return new Star(name, mass, position);
  }

  private boolean isPositionFree(Position position) {
    if (star.getPosition().equals(position))
      return false;

    for (Planet planet : star.getPlanets()) {
      if (planet.getPosition().equals(position))
        return false;

      for (Satellite satellite : planet.getSatellites()) {
        if (satellite.getPosition().equals(position))
          return false;
      }
    }

    return true;
  }

  private String generateStarSystemString() {
    StringBuilder starSystem = new StringBuilder(
        String.format("Relative positioning " + (relativePositioning ? ON : OFF) + "\n" + STAR_COLORS + ": %s\n",
            star));

    for (Planet planet : star.getPlanets()) {
      starSystem
          .append(String.format("\t- " + PLANET_COLORS + ": %s\n", planet));

      for (Satellite satellite : planet.getSatellites())
        starSystem.append(String
            .format("\t\t- " + SATELLITE_COLORS + ": %s\n",
                relativePositioning ? satellite.toStringRelative() : satellite));
    }

    return starSystem.toString();
  }

  private String generatePlanetString(Planet planet) {
    StringBuilder planetString = new StringBuilder(
        String.format("Relative positioning " + (relativePositioning ? ON : OFF) + "\n" + PLANET_COLORS + ": %s\n",
            planet));

    for (Satellite satellite : planet.getSatellites())
      planetString
          .append(String.format("\t- " + SATELLITE_COLORS + ": %s\n",
              relativePositioning ? satellite.toStringRelative() : satellite));

    return planetString.toString();
  }

  private String generateSatelliteString(Satellite satellite) {
    StringBuilder satelliteString = new StringBuilder(
        String.format(STAR_COLORS + "%s" + AnsiColors.RESET + " > ", star.getName()));

    for (Planet planet : star.getPlanets()) {
      if (planet.doesSatelliteOrbitsAround(satellite)) {
        satelliteString.append(String.format(
            PLANET_COLORS + "%s" + AnsiColors.RESET + " > " + SATELLITE_COLORS + "%s" + AnsiColors.RESET,
            planet.getName(),
            satellite.getName()));

        return satelliteString.toString();
      }
    }

    return satelliteString.toString();
  }

  private double calculateRouteLength(LinkedList<CelestialBody> route) {
    double length = 0;

    CelestialBody previous = null;
    for (CelestialBody current : route) {
      if (previous != null)
        length += previous.getPosition().distance(current.getPosition());

      previous = current;
    }

    return length;
  }

  public void addPlanet() {
    String name = InputData.readNonEmptyString("What is the planet name? ", true);
    double mass = InputData.readDoubleWithMinimum(String.format("What is %s mass? ", name), 0);
    Position position = null;

    do {
      if (position != null)
        errorMessage("Position already taken");

      double x = InputData.readDouble(String.format("What is the x coordinate of %s? ", name));
      double y = InputData.readDouble(String.format("What is the y coordinate of %s? ", name));

      position = new Position(x, y);
    } while (!isPositionFree(position));

    Planet planet = new Planet(name, mass, position);

    try {
      star.addPlanet(planet);

      successfulMessage(String.format("You have successfully added the planet %s to your star system", planet));

      wait(4000);
    } catch (IllegalStateException | InterruptedException e) {
      System.out.println(e.getMessage());
    }
  }

  public void addSatellite() {
    Planet planet = null;
    do {
      planet = star.searchPlanet(
          InputData.readNonEmptyString("Around what planet does this satellite orbits (insert ID or name)? ", false));

      if (planet == null)
        errorMessage("Planet not found");
    } while (planet == null);

    String name = InputData.readNonEmptyString("What is the satellite name? ", true);
    double mass = InputData.readDoubleWithMinimum(String.format("What is %s mass? ", name), 0);
    Position position = null, relativePosition;

    do {
      if (position != null)
        errorMessage("Position already taken");

      String output = String.format(
          "What is the x coordinate of %s (relative positioning "
              + (relativePositioning ? ON : OFF) + ")? ",
          name);
      double x = InputData.readDouble(output);
      output = String.format(
          "What is the y coordinate of %s (relative positioning "
              + (relativePositioning ? ON : OFF) + ")? ",
          name);
      double y = InputData.readDouble(output);

      position = new Position(x, y);
    } while (!isPositionFree(position));

    Satellite satellite = new Satellite(name, mass, position, relativePositioning, planet);

    try {
      planet.addSatellite(satellite);

      successfulMessage(
          String.format("You have successfully added the satellite %s to your star system", satellite));

      wait(4000);
    } catch (IllegalStateException | InterruptedException e) {
      System.out.println(e.getMessage());
    }
  }

  public void removePlanet() {
    Planet planet = star.searchPlanet(
        InputData.readNonEmptyString("Which planet would you like to remove (insert ID or name)? ", false));

    if (planet == null) {
      errorMessage("Planet not found");

      try {
        wait(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return;
    }

    star.removePlanet(planet);

    successfulMessage(String.format("The planet %s was successfully removed", planet.getName()));
    try {
      wait(4000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void removeSatellite() {
    for (Planet planet : star.getPlanets()) {
      Satellite satellite = planet.searchSatellite(
          InputData.readNonEmptyString("Which planet would you like to remove (insert ID or name)? ", false));
      if (satellite != null) {
        planet.removeSatellite(satellite);

        successfulMessage(String.format("The satellite %s was successfully removed", satellite.getName()));

        try {
          wait(4000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return;
      }
    }

    errorMessage("Satellite not found");

    try {
      wait(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void searchCelestialBody() {
    String searchQuery = InputData.readNonEmptyString("What celestial body are you looking for (insert ID or name)? ",
        false);

    try {
      loadingMessage("Looking for your celestial body");
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      CelestialBody celestialBody = star.searchCelestialBody(searchQuery);
      if (celestialBody == null) {
        errorMessage(String.format("The celestial body %s wasn't found", searchQuery));

        try {
          wait(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return;
      }

      if (celestialBody instanceof Satellite) {
        Satellite searchedSatellite = (Satellite) celestialBody;

        for (Planet planet : star.getPlanets()) {
          if (planet.doesSatelliteOrbitsAround(searchedSatellite)) {
            successfulMessage(String.format(
                "The celestial body you searched is a satellite of the system that orbits around the planet %s",
                planet.getName()));

            try {
              wait(5000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            return;
          }
        }
      }

      if (celestialBody instanceof Planet) {
        successfulMessage("The celestial body you searched is a planet of the system");

        try {
          wait(4000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return;
      }

      successfulMessage("The celestial body you searched for is the star of the system");

      try {
        wait(4000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void viewCelestialBodyInformations() {
    String searchQuery = InputData.readNonEmptyString(
        "Which celestial body do you want to see the informations of (insert ID or name)? ",
        false);

    try {
      loadingMessage("Looking for your celestial body");
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      CelestialBody celestialBody = star.searchCelestialBody(searchQuery);
      if (celestialBody == null) {
        errorMessage(String.format("The celestial body %s wasn't found", searchQuery));
        return;
      }

      if (celestialBody instanceof Satellite) {
        successfulMessage(generateSatelliteString((Satellite) celestialBody));
        return;
      }

      if (celestialBody instanceof Planet) {
        successfulMessage(generatePlanetString((Planet) celestialBody));
        return;
      }

      successfulMessage(generateStarSystemString());
    }
  }

  public void calculateCenterOfMass() {
    try {
      loadingMessage("Calculating center of mass");
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      successfulMessage(String.format("Center of Mass = %s",
          star.centerOfMass()));

      try {
        wait(4000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void toggleRelativePositioning() {
    System.out
        .println(String.format("Right now the relative positioning is turned %s", relativePositioning ? ON : OFF));

    relativePositioning = InputData
        .readYesOrNo(String.format("Would you like to turn it %s", relativePositioning ? OFF : ON))
            ? !relativePositioning
            : relativePositioning;
  }

  public void calculateRoute() {
    String searchQueryStart = InputData.readNonEmptyString(
        "Which celestial body are you starting from (insert ID or name)? ",
        false);
    String searchQueryFinish = InputData.readNonEmptyString(
        "Which celestial body are you travelling to (insert ID or name)? ",
        false);

    try {
      loadingMessage("Calculating route");
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      CelestialBody startingCelestialBody = star.searchCelestialBody(searchQueryStart);
      CelestialBody finishingCelestialBody = star.searchCelestialBody(searchQueryFinish);

      if (startingCelestialBody == null || finishingCelestialBody == null) {
        errorMessage("One or both the celestial bodies you entered weren't found");

        return;
      }

      if (startingCelestialBody.equals(finishingCelestialBody)) {
        successfulMessage("You are already at your destination");

        return;
      }

      LinkedList<CelestialBody> route = star.calculateRoute(startingCelestialBody, finishingCelestialBody);
      double routeLength = calculateRouteLength(route);

      StringBuilder stringedRoute = new StringBuilder(route.removeFirst().getName() + " > ");

      while (!route.isEmpty())
        stringedRoute.append(route.removeFirst().getName() + " > ");

      String message = String.format(
          "Your route for going from %s to %s is:\n\n%s\n\nAnd you will be travelling %.2f parsec",
          startingCelestialBody.getName(), finishingCelestialBody.getName(),
          stringedRoute.substring(0, stringedRoute.length() - 3), routeLength);

      successfulMessage(message);
    }
  }
}
