package geneticAlgorithm;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Jadson Oliveira <jadsonjjmo@gmail.com>
 */

public class Chromosome implements Serializable {

    private ArrayList<Integer> dna;
    private double fitnessDistance;
    private double fitnessRoutes;
    private int quantityOfRestrictions;


    public Chromosome(ArrayList<Integer> dna, double fitnessDistance, double fitnessRoutes) {
        this.dna = dna;
        this.fitnessDistance = fitnessDistance;
        this.fitnessRoutes = fitnessRoutes;
        this.quantityOfRestrictions = 0;
    }

    public Chromosome(){
        this(new ArrayList<Integer>(), 0, 0);
    }

    public Chromosome(Chromosome chromosome){
        this.dna = new ArrayList<>(chromosome.getDna());
        this.fitnessDistance = chromosome.getFitnessDistance();
        this.fitnessRoutes = chromosome.getFitnessRoutes();
        this.quantityOfRestrictions = chromosome.getQuantityOfRestrictions();
    }


    public Chromosome clone(){

        return new Chromosome(this);

    }


    //Getters

    public ArrayList<Integer> getDna() {
        return this.dna;
    }

    public double getFitnessDistance() {
        return this.fitnessDistance;
    }

    public double getFitnessRoutes() {
        return this.fitnessRoutes;
    }

    public int getQuantityOfRestrictions() {
        return this.quantityOfRestrictions;
    }

}
