package base;

import java.util.ArrayList;

import it.kibo.fp.lib.AnsiColors;

public class Planet extends CelestialBody {
  private static final int MAX_SATELLITES = 5000;

  private ArrayList<Satellite> satellites;

  public Planet(String name, double mass, Position position) {
    super(name, mass, position);
    satellites = new ArrayList<>();
  }

  public ArrayList<Satellite> getSatellites() {
    return satellites;
  }

  public void addSatellite(Satellite satellite) throws IllegalStateException {
    if (satellites.size() >= MAX_SATELLITES)
      throw new IllegalStateException(
          AnsiColors.YELLOW + "Warning!" + AnsiColors.RESET + "\nMaximum number of satellites reached");

    satellites.add(satellite);
  }

  public void removeSatellite(Satellite satellite) {
    satellites.remove(satellite);
  }

  public Satellite searchSatellite(String searchQuery) {
    Satellite satellite = searchSatelliteByID(searchQuery);

    if (satellite != null)
      return satellite;

    return searchSatelliteByName(searchQuery);
  }

  private Satellite searchSatelliteByID(String ID) {
    for (Satellite satellite : satellites) {
      if (satellite.getID().equals(ID))
        return satellite;
    }

    return null;
  }

  private Satellite searchSatelliteByName(String name) {
    for (Satellite satellite : satellites) {
      if (satellite.getName().equals(name))
        return satellite;
    }

    return null;
  }

  public boolean doesSatelliteOrbitsAround(Satellite searchedSatellite) {
    for (Satellite satellite : satellites) {
      if (satellite.equals(searchedSatellite))
        return true;
    }

    return false;
  }
}
