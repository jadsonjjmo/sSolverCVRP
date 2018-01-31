package geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jadson Oliveira <jadsonjjmo@gmail.com>
 */

public class Population {

    private List<Chromosome> population;
    private int generation;


    public Population(){
        super();
    }


    public List<Population> fork(int slices){
        List<Population> listOfPopulations = new ArrayList<>(slices);

        for(int i = 0; i < slices - 1; i++){
            listOfPopulations.add(this.clone());
        }
        listOfPopulations.add(this);

        return listOfPopulations;
    }


    public Population evolve(){

        final int maxGeneration = 100;
        final int limitOfIndividuals = 100;

        this.population = Solver.solve(this.population, maxGeneration, limitOfIndividuals);
        this.generation += maxGeneration;

        return this;
    }


    public static Population combine(Population population1, Population population2){return null;}


    public void generateInitialPopulation(){



    }


    public Population clone(){
        Population clone = new Population();
        clone.population = new ArrayList<>(population.stream().map(Chromosome::new).collect(Collectors.toList()));
        return clone;
    }

    public Chromosome getShortest(){
        return this.population.get(0);
    }


    //Getters

    public List<Chromosome> getPopulation() {
        return this.population;
    }

    public int getGeneration() {
        return this.generation;
    }

}
