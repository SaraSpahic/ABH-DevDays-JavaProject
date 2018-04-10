package models.helpers;

/**
 * The type Administrator statistics.
 */
public class AdministratorStatistics {

    private int numberOfRestaurants;
    private int numberOfCuisines;
    private int numberOfLocations;
    private int numberOfUsers;

    private AdministratorStatistics() {
    }

    public AdministratorStatistics(int numberOfRestaurants, int numberOfCuisines, int numberOfLocations, int numberOfUsers) {
        this.numberOfRestaurants = numberOfRestaurants;
        this.numberOfCuisines = numberOfCuisines;
        this.numberOfLocations = numberOfLocations;
        this.numberOfUsers = numberOfUsers;
    }

    public int getNumberOfRestaurants() {
        return numberOfRestaurants;
    }

    public void setNumberOfRestaurants(int numberOfRestaurants) {
        this.numberOfRestaurants = numberOfRestaurants;
    }

    public int getNumberOfCuisines() {
        return numberOfCuisines;
    }

    public void setNumberOfCuisines(int numberOfCuisines) {
        this.numberOfCuisines = numberOfCuisines;
    }

    public int getNumberOfLocations() {
        return numberOfLocations;
    }

    public void setNumberOfLocations(int numberOfLocations) {
        this.numberOfLocations = numberOfLocations;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }
}
