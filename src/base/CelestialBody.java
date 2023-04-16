package base;

import it.kibo.fp.lib.RandomDraws;

public class CelestialBody {
  private static int counter = 1000;

  private final String ID;
  private String name;
  private double mass;
  private Position position;

  public CelestialBody(String name, double mass, Position position) {
    this.ID = generateID();
    this.name = name;
    this.mass = mass;
    this.position = position;
  }

  public String getID() {
    return ID;
  }

  public String getName() {
    return name;
  }

  public double getMass() {
    return mass;
  }

  public Position getPosition() {
    return position;
  }

  private String generateID() {
    String ID = String.format("CB_%d", counter);

    counter += RandomDraws.drawInteger(1, 100);

    return ID;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CelestialBody))
      return false;

    CelestialBody objCelestialBody = (CelestialBody) obj;

    return ID.equals(objCelestialBody.ID);
  }

  @Override
  public String toString() {
    return String.format("{ID: %s, Name: %s, Mass: %.2f, Position: %s}", ID, name, mass, position);
  }
}
