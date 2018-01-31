package geneticAlgorithm;

/**
 * @author Jadson Oliveira <jadsonjjmo@gmail.com>
 */

public class Environment {

    private double mutationRate;
    private double elitismRate;
    private double vehiclesCapacity;
    private double[][] distancePoints;
    private double[] costs;


    //Getters

    public double getMutationRate() {
        return this.mutationRate;
    }

    public double getElitismRate() {
        return this.elitismRate;
    }

    public double getVehiclesCapacity() {
        return this.vehiclesCapacity;
    }

    public double[][] getDistancePoints() {
        return this.distancePoints;
    }

    public double[] getCosts() {
        return this.costs;
    }


    //Setters

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public void setElitismRate(double elitismRate) {
        this.elitismRate = elitismRate;
    }

    public void setVehiclesCapacity(double vehiclesCapacity) {
        this.vehiclesCapacity = vehiclesCapacity;
    }

    public void setDistancePoints(double[][] distancePoints) {
        this.distancePoints = distancePoints;
    }

    public void setCosts(double[] costs) {
        this.costs = costs;
    }

}
