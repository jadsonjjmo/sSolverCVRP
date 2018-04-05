package geneticAlgorithm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Jadson Oliveira <jadsonjjmo@gmail.com>
 */

public class Environment implements Serializable {

    private double mutationRate;
    private double elitismRate;
    private double vehiclesCapacity;
    private int quantityOfVehicles;
    private double[][] distancePoints;
    private double[] costs;


    //TODO: Improve code (hard coded)
    public void fixChromosome(Chromosome chromosome) {

        chromosome.setFitnessRoutes(0);
        chromosome.setFitnessDistance(0);
        chromosome.setQuantityOfRestrictions(0);

        ArrayList<ArrayList<Integer>> container = new ArrayList<>();
        container.add(new ArrayList<>());

        for (int i = 0, j = 0, sum = 0; i < chromosome.getDna().size(); i++) {

            sum += costs[chromosome.getDna().get(i)];

            if (sum > getVehiclesCapacity()) {
                j++;

                container.add(new ArrayList<>());
                sum = (int) costs[chromosome.getDna().get(i)];

            } else if (chromosome.getDna().get(i) == 0) {
                if (i > 0) {
                    if (chromosome.getDna().get(i - 1) != 0) {
                        j++;
                        container.add(new ArrayList<>());
                    }
                } else {
                    container.add(new ArrayList<>());
                    sum = (int) costs[0];
                }
            }

            if (chromosome.getDna().get(i) != 0) {
                container.get(j).add(chromosome.getDna().get(i));
            }

        }

        ArrayList<Integer> newDna = new ArrayList<>();

        int auxiliaryQuantityOfVehicles = getQuantityOfVehicles();

        for (int i = 0; i < container.size(); i++) {
            for (int j = 0; j < container.get(i).size(); j++) {
                newDna.add(container.get(i).get(j));
            }

            if (newDna.size() > 1) {
                if (newDna.get(newDna.size() - 1) != 0 && auxiliaryQuantityOfVehicles > 1) {
                    newDna.add(0);
                    auxiliaryQuantityOfVehicles--;
                } else if(newDna.get(newDna.size() - 1) != 0){
                    newDna.add(0);
                    chromosome.addBrokenRestriction();
                }
            } else {
                newDna.add(0);
            }

        }

        chromosome.setDna(newDna);

        Set<Integer> set = new TreeSet<>();

        for (int i = 0; i < chromosome.getDna().size(); i++) {

            if (set.contains(chromosome.getDna().get(i)) && chromosome.getDna().get(i) != 0) {
                chromosome.getDna().remove(i);
                continue;
            }

            set.add(chromosome.getDna().get(i));

        }

        double sum = 0;

        for (int i = 0; i < costs.length; i++) {

            if (!set.contains(i)) {
                sum += costs[i];
                if (sum > getVehiclesCapacity()) {
                    chromosome.getDna().add(0);
                    sum = costs[i];
                }
                chromosome.getDna().add(i);
            }

        }

        if (chromosome.getDna().get(0) != 0) {
            chromosome.getDna().add(0, 0);
        }
        if (chromosome.getDna().get(chromosome.getDna().size() - 1) != 0) {
            chromosome.getDna().add(0);
        }

    }


    public void firstEvaluationMethod(Chromosome chromosome){

        float distance = 0;

        for(int i = 1; i<chromosome.getDna().size(); i++){

            int currentNumber = chromosome.getDna().get(i);
            int previousNumber = chromosome.getDna().get(i-1);

            distance += distancePoints[previousNumber][currentNumber];

        }

        chromosome.setFitnessDistance(distance);

    }


    public void secondEvaluationMethod(Chromosome chromosome){

        int count = 0;

        for(Integer gene : chromosome.getDna()){
            if(gene == 0){
                count ++;
            }
        }

        chromosome.setFitnessRoutes(count);

    }


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

    public int getQuantityOfVehicles() {
        return this.quantityOfVehicles;
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

    public void setQuantityOfVehicles(int quantityOfVehicles) {
        this.quantityOfVehicles = quantityOfVehicles;
    }

    public void setDistancePoints(double[][] distancePoints) {
        this.distancePoints = distancePoints;
    }

    public void setCosts(double[] costs) {
        this.costs = costs;
    }

}
