package geneticAlgorithm;

import scala.Int;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jadson Oliveira <jadsonjjmo@gmail.com>
 */

public class Population implements Serializable {

    private List<Chromosome> population;
    private int generation;
    private static final int LIMIT_OF_INDIVIDUALS = 60;


    public Population() {
        super();
    }


    public List<Population> fork(int slices) {
        List<Population> listOfPopulations = new ArrayList<>(slices);

        for (int i = 0; i < slices - 1; i++) {
            listOfPopulations.add(this.clone());
        }
        listOfPopulations.add(this);

        return listOfPopulations;
    }


    public Population evolve() {

        final int maxGeneration = 20;

        this.population = Solver.solve(this.population, maxGeneration, LIMIT_OF_INDIVIDUALS);
        this.generation += maxGeneration;

        return this;
    }


    public static Population combine(Population population1, Population population2) {

        List<Chromosome> individuals = new ArrayList<>(population1.getPopulation());
        individuals.addAll(population2.getPopulation());
        Collections.sort(individuals, (chromosome1, chromosome2) -> {

            final int compareQuantityOfBrokenRestriction = Integer.compare(chromosome1.getQuantityOfRestrictions(), chromosome2.getQuantityOfRestrictions());
            final int compareFitnessDistance = Double.compare(chromosome1.getFitnessDistance(), chromosome2.getFitnessDistance());
            final int compareFitnessRoutes = Double.compare(chromosome1.getFitnessRoutes(), chromosome2.getFitnessRoutes());

            return compareQuantityOfBrokenRestriction == 0 ?
                    (compareFitnessDistance == 0 ? compareFitnessRoutes : compareFitnessDistance) :
                    compareQuantityOfBrokenRestriction;
        });

        Population newPopulation = new Population();
        newPopulation.population = new ArrayList<>();
        newPopulation.generation = Math.max(population1.getGeneration(), population2.getGeneration());

        for (int i = 0; i < LIMIT_OF_INDIVIDUALS; i++) {
            newPopulation.population.add(individuals.get(i).clone());
        }

        return newPopulation;
    }


    public void generateInitialPopulation(Environment environment) {

        population = new ArrayList<>();

        //TODO: Change it to a Logger
        System.out.println("Generating initial population...");

        //Creating dna in order
        for (int i = 0; i < LIMIT_OF_INDIVIDUALS / 3; i++) {
            ArrayList<Integer> newDna = new ArrayList<>();
            newDna.add(0);

            for (int j = 1; j < environment.getCosts().length; j++) {
                newDna.add(j);
            }

            newDna.add(0);

            population.add(new Chromosome(newDna, 0, 0));
        }

        //Creating dna in reverse order
        for (int i = 0; i < LIMIT_OF_INDIVIDUALS / 3; i++) {
            ArrayList<Integer> newDna = new ArrayList<>();
            newDna.add(0);

            for (int j = environment.getCosts().length - 1; j > 0; j--) {
                newDna.add(j);
            }

            newDna.add(0);

            population.add(new Chromosome(newDna, 0, 0));
        }


        //Creating dna in random order
        for (int i = 0; i < LIMIT_OF_INDIVIDUALS / 3; i++) {

            //TODO: improve it!

            Random random = new Random();

            ArrayList<Integer> arrayList = new ArrayList();
            for (int j = 1; j < environment.getCosts().length; j++) {
                arrayList.add(j);
            }

            ArrayList<Integer> newDna = new ArrayList<>();
            newDna.add(0);

            while (!arrayList.isEmpty()) {
                newDna.add(arrayList.remove(random.nextInt(arrayList.size())));
            }

            newDna.add(0);

            population.add(new Chromosome(newDna, 0, 0));

        }

        System.out.println("Initial population successfully generated!");

    }


    public Population clone() {
        Population clone = new Population();
        clone.population = new ArrayList<>(population.stream().map(Chromosome::new).collect(Collectors.toList()));
        return clone;
    }

    public Chromosome getShortest() {

        Collections.sort(population, (chromosome1, chromosome2) -> {

            final int compareQuantityOfBrokenRestriction = Integer.compare(chromosome1.getQuantityOfRestrictions(), chromosome2.getQuantityOfRestrictions());
            final int compareFitnessDistance = Double.compare(chromosome1.getFitnessDistance(), chromosome2.getFitnessDistance());
            final int compareFitnessRoutes = Double.compare(chromosome1.getFitnessRoutes(), chromosome2.getFitnessRoutes());

            return compareQuantityOfBrokenRestriction == 0 ?
                    (compareFitnessDistance == 0 ? compareFitnessRoutes : compareFitnessDistance) :
                    compareQuantityOfBrokenRestriction;
        });

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
