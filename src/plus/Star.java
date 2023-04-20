package plus;

import java.util.ArrayList;
import java.util.LinkedList;

import it.kibo.fp.lib.AnsiColors;
/**
 * A class rapresantation of a star celestial body
 * @see CelestialBody
 */
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

  /**Adds the planet to the list of planets of the solar system if possible
   * @param planet to add to the system
   * @throws IllegalStateException if there are already MAX_PLANETS planets in the system
   */
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

  /**
   * @param searchQuery
   * @return
   */
  public Planet searchPlanet(String searchQuery) {
    Planet planet = searchPlanetByID(searchQuery);

    if (planet != null)
      return planet;

    return searchPlanetByName(searchQuery);
  }

  /**
   * @param ID of the planet to search
   * @return the planet with that ID or null if not found
   */
  private Planet searchPlanetByID(String ID) {
    for (Planet planet : planets) {
      if (planet.getID().equals(ID))
        return planet;
    }

    return null;
  }

  /**
   * @param name of the planet to search
   * @return the first planet with that name, null if not found
   */
  private Planet searchPlanetByName(String name) {
    for (Planet planet : planets) {
      if (planet.getName().equalsIgnoreCase(name))
        return planet;
    }

    return null;
  }

  /**
   * @param searchQuery ID or name of the celestial body to search
   * @return the celestial body or null if not found
   */
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
  /**Method used to calculate the center of mass of this star's solar system
   * @return the center of mass of this star's solar system
   */
  public Position centerOfMass() {
    double totalSystemMass = getMass();
    double weightedPositionSumX = 0;
    double weightedPositionSumY = 0;
    //weighted average position of the celestial bodies
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

  /**Method used to find the next route element to reach this star
   * @param element the celestial body where the subroute starts
   * @return if "element" is a satellite of this solar system, the return value is the satellite's orbiting planet, this star otherwise
   */
  private CelestialBody getNextRouteElement(CelestialBody element) {
    if (element instanceof Satellite) {
      for (Planet planet : planets) {
        if (planet.doesSatelliteOrbitsAround((Satellite) element))
          return planet;
      }
    }

    return this;
  }

  /**This method calculates the route to use to reach finish from start using the scheme moon1 > planet1 > star > planet2 > moon2
   * @param start celetial body where the route starts
   * @param finish celestial body to reach
   * @return a linked list containing the ordered celestial bodies to pass through to reach finish from start (start/finish included) 
   */
  public LinkedList<CelestialBody> calculateRoute(CelestialBody start, CelestialBody finish) {
    LinkedList<CelestialBody> startingList = new LinkedList<>(), finishingList = new LinkedList<>();
    startingList.add(start);
    finishingList.add(finish);
    /*
    cycle to add the "parent" of start to startingList and the "parent" of finish to finishingList until the to lists
    reach a common element (the star or the orbiting planet, if start and finish are two satellites orbiting the same planet)
    */
    while (!startingList.getLast().equals(finishingList.getLast())) {
      startingList.add(getNextRouteElement(startingList.getLast()));
      finishingList.add(getNextRouteElement(finishingList.getLast()));
    }
    //the last element is already in starting list
    finishingList.removeLast();
    while (!finishingList.isEmpty())
      startingList.add(finishingList.removeLast());

    return startingList;
  }

  /**
   * @return an arraylist containing all celestial bodies in this star's system
   */
  private ArrayList<CelestialBody> getStarSystem() {
    ArrayList<CelestialBody> starSystem = new ArrayList<>();

    starSystem.add(this);
    starSystem.addAll(planets);
    for (Planet planet : planets)
      starSystem.addAll(planet.getSatellites());

    return starSystem;
  }

  /**If the satellite belongs to this solar system, this method will find the satellite's orbiting planet
   * @param satellite
   * @return satellite's orbiting planet if it belong's to this solar system, null otherwise
   */
  private Planet getOrbitingPlanet(Satellite satellite) {
    for (Planet planet : planets) {
      if (planet.doesSatelliteOrbitsAround(satellite))
        return planet;
    }

    return null;
  }

  /**It calculates wether collisions may occur in the future or not in this solar system
   * @return true if there will be collisions, false otherwise
   */
  public boolean detectCollisions() {
    ArrayList<CelestialBody> starSystem = getStarSystem();

    for (int i = 0; i < starSystem.size() - 1; i++) {
      CelestialBody current = starSystem.get(i);
      for (int j = starSystem.size() - 1; j > 0; j--) {
        CelestialBody toCheck = starSystem.get(j);

        // Check for star vs satellites collisions
        if (current instanceof Star) {
          if (toCheck instanceof Planet)
            break;

          Satellite toCheckSatellite = (Satellite) toCheck;
          Planet toCheckOrbitingPlanet = getOrbitingPlanet(toCheckSatellite);
          if (toCheckOrbitingPlanet.getPosition().distance(getPosition()) == toCheckSatellite.getRelativePosition()
              .distance(getPosition()))
            return true;
        }
        // Check for planet collisions
        else if (current instanceof Planet) {
          // planet vs planet collision
          if (toCheck instanceof Planet && current.getPosition().equals(toCheck.getPosition())) {
            return true;
          }
          // planet vs satellite collision
          else {
            Satellite toCheckSatellite = (Satellite) toCheck;
            double planetToStarDistance = current.getPosition().distance(getPosition());
            if (toCheckSatellite.getMinStarDistance() <= planetToStarDistance
                && planetToStarDistance <= toCheckSatellite.getMaxStarDistance())
              return true;
          }
        }
        // Check for satellite vs satellite collisions
        else {
          if (toCheck instanceof Planet)
            break;

          double currentMinStarDistance = ((Satellite) current).getMinStarDistance();
          double currentMaxStarDistance = ((Satellite) current).getMaxStarDistance();
          double toCheckMinStarDistance = ((Satellite) toCheck).getMinStarDistance();
          double toCheckMaxStarDistance = ((Satellite) toCheck).getMaxStarDistance();

          boolean firstCheck = currentMinStarDistance <= toCheckMinStarDistance
              && toCheckMinStarDistance <= currentMaxStarDistance;
          boolean secondCheck = currentMinStarDistance <= toCheckMaxStarDistance
              && toCheckMinStarDistance <= currentMaxStarDistance;
          boolean thirdCheck = toCheckMinStarDistance <= currentMinStarDistance
              && currentMinStarDistance <= toCheckMaxStarDistance;
          boolean fourthCheck = toCheckMinStarDistance <= currentMaxStarDistance
              && currentMinStarDistance <= toCheckMaxStarDistance;
          if (firstCheck || secondCheck || thirdCheck || fourthCheck)
            return true;
        }
      }
    }

    return false;
  }
}
