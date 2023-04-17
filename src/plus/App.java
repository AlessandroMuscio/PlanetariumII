package plus;

import it.kibo.fp.lib.InputData;

public class App {
  public static void main(String[] args) throws Exception {
    StarSystemController.clearConsole();
    System.out.println("Welcome to the OIFCS (Official Intergalactic Federation Control System)");
    StarSystemController.wait(2000);
    System.out.println("Before going any further please initialize your star, the heart of your star system");
    StarSystemController.wait(2500);

    StarSystemController starSystemController = StarSystemController.getInstance();
    int choice;
    do {
      StarSystemController.clearConsole();

      choice = starSystemController.choose();
      switch (choice) {
        case 1:
          do {
            starSystemController.addPlanet();
          } while (InputData.readYesOrNo("Would you like to add another planet"));
          break;

        case 2:
          do {
            starSystemController.addSatellite();
          } while (InputData.readYesOrNo("Would you like to add another satellite"));
          break;

        case 3:
          do {
            starSystemController.removePlanet();
          } while (InputData.readYesOrNo("Would you like to remove another planet"));
          break;

        case 4:
          do {
            starSystemController.removeSatellite();
          } while (InputData.readYesOrNo("Would you like to remove another satellite"));
          break;

        case 5:
          do {
            starSystemController.searchCelestialBody();
          } while (InputData.readYesOrNo("Would you like to search another celestial body"));
          break;

        case 6:
          do {
            starSystemController.viewCelestialBodyInformations();
          } while (InputData.readYesOrNo("Would you like to view another celestial body informations"));
          break;

        case 7:
          starSystemController.calculateCenterOfMass();
          break;

        case 8:
          starSystemController.toggleRelativePositioning();
          break;

        case 9:
          do {
            starSystemController.calculateRoute();
          } while (InputData.readYesOrNo("Would you like to calculate another route?"));
          break;

        case 10:
          starSystemController.detectCollisions();
          break;

        case 0:
          break;

        default:
          System.out.println("No operation associated with that choice");
          StarSystemController.wait(2000);
          break;
      }

    } while (choice != 0);

    System.out.println("Thank you for using the OIFCS (Official Intergalactic Federation Control System)");
    StarSystemController.wait(2000);
    System.out.println("We hope to see you soon!");
    StarSystemController.wait(1500);
    StarSystemController.clearConsole();
  }
}
