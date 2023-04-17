package plus;

import java.util.ArrayList;
import java.util.LinkedList;

import it.kibo.fp.lib.AnsiColors;

public class Star extends CelestialBody {
  private static final int MAX_PLANETS = 26000;

  private ArrayList<Planet> planets;

  public Star(String name, double mass, Position position) {
    super(name, mass, position);
    planets = new ArrayList<>();
  }

  public ArrayList<Planet> getPlanets() {
    return planets;
  }

  public void addPlanet(Planet planet) throws IllegalStateException {
    if (planets.size() >= MAX_PLANETS)
      throw new IllegalStateException(
          AnsiColors.YELLOW + "Warning!" + AnsiColors.RESET + "\nMaximum number of planets reached");

    planets.add(planet);
  }

  public void removePlanet(Planet planet) {
    planet.getSatellites().clear();

    planets.remove(planet);
  }

  public Planet searchPlanet(String searchQuery) {
    Planet planet = searchPlanetByID(searchQuery);

    if (planet != null)
      return planet;

    return searchPlanetByName(searchQuery);
  }

  private Planet searchPlanetByID(String ID) {
    for (Planet planet : planets) {
      if (planet.getID().equals(ID))
        return planet;
    }

    return null;
  }

  private Planet searchPlanetByName(String name) {
    for (Planet planet : planets) {
      if (planet.getName().equals(name))
        return planet;
    }

    return null;
  }

  public CelestialBody searchCelestialBody(String searchQuery) {
    ArrayList<CelestialBody> celestialBodies = new ArrayList<>();

    celestialBodies.add(this);
    celestialBodies.addAll(planets);

    for (Planet planet : planets)
      celestialBodies.addAll(planet.getSatellites());

    for (CelestialBody celestialBody : celestialBodies) {
      if (celestialBody.getID().equals(searchQuery) || celestialBody.getName().equals(searchQuery))
        return celestialBody;
    }

    return null;
  }

  public Position centerOfMass() {
    double totalSystemMass = getMass();
    double weightedPositionSumX = 0;
    double weightedPositionSumY = 0;

    for (Planet planet : planets) {
      totalSystemMass += planet.getMass();

      weightedPositionSumX += planet.getMass() * planet.getPosition().getX();
      weightedPositionSumY += planet.getMass() * planet.getPosition().getY();

      for (Satellite satellite : planet.getSatellites()) {
        totalSystemMass += satellite.getMass();

        weightedPositionSumX += satellite.getMass() * satellite.getPosition().getX();
        weightedPositionSumY += satellite.getMass() * satellite.getPosition().getY();
      }
    }

    return new Position(weightedPositionSumX / totalSystemMass, weightedPositionSumY / totalSystemMass);
  }

  private CelestialBody getNextRouteElement(CelestialBody element) {
    if (element instanceof Satellite) {
      for (Planet planet : planets) {
        if (planet.doesSatelliteOrbitsAround((Satellite) element))
          return planet;
      }
    }

    return this;
  }

  public LinkedList<CelestialBody> calculateRoute(CelestialBody start, CelestialBody finish) {
    LinkedList<CelestialBody> startingList = new LinkedList<>(), finishingList = new LinkedList<>();
    startingList.add(start);
    finishingList.add(finish);

    while (!startingList.getLast().equals(finishingList.getLast())) {
      startingList.add(getNextRouteElement(startingList.getLast()));
      finishingList.add(getNextRouteElement(finishingList.getLast()));
    }

    finishingList.removeLast();
    while (!finishingList.isEmpty())
      startingList.add(finishingList.removeLast());

    return startingList;
  }

  private Planet getOrbitingPlanet (Satellite satellite) {
    for (Planet planet : planets) {
      if (planet.doesSatelliteOrbitsAround(satellite))
        return planet;
    }
    return null;
  }
}
