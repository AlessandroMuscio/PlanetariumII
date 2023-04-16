package base;

public class Satellite extends CelestialBody {
  private Position relativePosition;

  public Satellite(String name, double mass, Position position, Position relativePosition) {
    super(name, mass, position);
    this.relativePosition = relativePosition;
  }

  public String toStringRelative() {
    return String.format("{ID: %s, Name: %s, Mass: %.2f, RelativePosition: %s}", getID(), getName(), getMass(),
        relativePosition);
  }
}
