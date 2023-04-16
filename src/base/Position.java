package base;

public class Position {
  private double x;
  private double y;

  public Position(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public Position subtract(Position other) {
    return new Position(x - other.x, y - other.y);
  }

  public Position add(Position other) {
    return new Position(x + other.x, y + other.y);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Position))
      return false;

    Position objPosition = (Position) obj;

    return x == objPosition.x && y == objPosition.y;
  }

  @Override
  public String toString() {
    return String.format("(%.2f, %.2f)", x, y);
  }
}
