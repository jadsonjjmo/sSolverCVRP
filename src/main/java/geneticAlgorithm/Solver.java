package geneticAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Jadson Oliveira <jadsonjjmo@gmail.com>
 */

public class Solver {

    private final static Random RANDOM_NUMBER = new Random();
    private Environment environment;

    //Initialize Solver with the environment restrictions


    public static List<Chromosome> solve(List<Chromosome> population, int maxGeneration, int limitOfIndividuals) {

        double initialTime = System.currentTimeMillis();

        for (int i = 0; i < maxGeneration; i++) {

            List<Chromosome> selectedIndividuals = selectIndividuals(population, limitOfIndividuals);
            List<Chromosome> newPopulation = crossAndMutate(selectedIndividuals);

            population.addAll(newPopulation);

        }

        return population;
    }


    public static Population combine(Population population1, Population population2) {
        return null;
    }


    public static List<Chromosome> selectIndividuals(List<Chromosome> population, int limitOfIndividuals) {
        int randomNumber = RANDOM_NUMBER.nextInt(100) % 3;

        switch (randomNumber) {
            case 0:
                return rouletteSelection(population, limitOfIndividuals);
            case 1:
                return randomSelection(population, limitOfIndividuals);
            default:
                return tournamentSelection(population, limitOfIndividuals);
        }

    }

    public static List<Chromosome> crossoverIndividuals(List<Chromosome> population) {
        return null;
    }


    /*
    public static List<Chromosome> selectIndividuals(List<Chromosome> population, int limitIndividuals) {

        List<Chromosome> newPopulation = new ArrayList<>();

        Collections.sort(population, (population1, population2) -> {

            if (population1.getQuantityOfRestrictions() == 0 && population2.getQuantityOfRestrictions() == 0) {
                return Double.compare(population1.getFitnessDistance(), population2.getFitnessDistance());
            } else if (population1.getQuantityOfRestrictions() == 0) {
                return -1;
            } else if (population2.getQuantityOfRestrictions() == 0) {
                return 1;
            }

            final int compareFitnessDistance = Double.compare(population1.getFitnessDistance(), population2.getFitnessDistance());
            final int compareFitnessRoutes = Double.compare(population1.getFitnessRoutes(), population2.getFitnessRoutes());

            return compareFitnessDistance == 0 ? compareFitnessRoutes : compareFitnessDistance;
        });

        population.stream().limit(limitIndividuals).forEach(newPopulation::add);

        return newPopulation;

    }
    */

    public static List<Chromosome> rouletteSelection(List<Chromosome> population, int limitOfIndividuals) {
        List<Chromosome> selectedIndividuals = new ArrayList<>();


        return selectedIndividuals;
    }


    public static List<Chromosome> randomSelection(List<Chromosome> population, int limitOfIndividuals) {

        List<Chromosome> selectedIndividuals = new ArrayList<>();

        if (population.size() == 0) {
            return selectedIndividuals;
        }

        //TODO: try to optimize it
        for (int i = 0; i < limitOfIndividuals; i++) {
            selectedIndividuals.add(population.remove(RANDOM_NUMBER.nextInt(population.size())).clone());
        }

        return selectedIndividuals;

    }


    public static List<Chromosome> tournamentSelection(List<Chromosome> population, int limitOfIndividuals) {
        List<Chromosome> selectedIndividuals = new ArrayList<>();


        return selectedIndividuals;
    }


    public static List<Chromosome> crossoverOX(List<Chromosome> population) {

        List<Chromosome> newPopulation = new ArrayList<>(population);

        for (int i = 0; i < population.size(); i++) {


        }

        return population;
    }

    public static List<Chromosome> crossoverPMX(List<Chromosome> population) {

        List<Chromosome> newPopulation = new ArrayList<>(population);

        for (int i = 0; i < population.size(); i++) {

            for (int j = 0; j < population.size(); j++) {

                if (i == j) continue;

                int cutPoint = RANDOM_NUMBER.nextInt(population.get(i).getDna().size());

                ArrayList<Integer> newDna = new ArrayList<>();

                for (int dnaIndex = 0; dnaIndex < population.get(i).getDna().size(); dnaIndex++) {
                    if (dnaIndex <= cutPoint) {
                        newDna.add(population.get(i).getDna().get(dnaIndex));
                    } else {
                        newDna.add(population.get(j).getDna().get(dnaIndex));
                    }
                }

                newPopulation.add(new Chromosome(newDna, 0, 0));

            }

        }

        return population;
    }


    public static List<Chromosome> crossAndMutate(List<Chromosome> population) {
        return population;
    }


}
