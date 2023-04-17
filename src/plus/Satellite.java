package plus;

public class Satellite extends CelestialBody {
  private Position relativePosition;
  private double minStarDistance;
  private double maxStarDistance;

  public Satellite(String name, double mass, Position position, boolean relativePositioning, Planet orbitingPlanet) {
    super(name, mass, relativePositioning ? position.add(orbitingPlanet.getPosition()) : position);
    this.relativePosition = position.subtract(orbitingPlanet.getPosition());
    double starPlanetDistance = orbitingPlanet.getPosition().distance(new Position(0, 0));
    minStarDistance = starPlanetDistance - relativePosition.distance(new Position(0, 0));
    maxStarDistance = starPlanetDistance + relativePosition.distance(new Position(0, 0));
  }

  public Position getRelativePosition() {
    return relativePosition;
  }

  public double getMinStarDistance() {
    return minStarDistance;
  }

  public double getMaxStarDistance() {
    return maxStarDistance;
  }

  public String toStringRelative() {
    return String.format("{ID: %s, Name: %s, Mass: %.2f, RelativePosition: %s}", getID(), getName(), getMass(),
        relativePosition);
  }
}
