import geneticAlgorithm.Chromosome;
import geneticAlgorithm.Population;
import geneticAlgorithm.Solver;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.broadcast.Broadcast;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Jadson Monteiro <jadsonjjmo@gmail.com>
 */

public class Main {

    public static void main(String[] args) throws IOException {

        //System.setIn(new FileInputStream("/opt/spark/test.in"));

        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("sSolverCVRP");
        //SparkConf sparkConf = new SparkConf().setAppName("sSolverCVRP");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        sparkContext.setLogLevel("ERROR");

        //Quantity of iterations
//        int iterations = 1000;

        Solver solver = new Solver();
        solver.readInput();




        double result = 0;
        double time = 0;

        for (int iterations = 100; iterations <= 10000; iterations *= 10) {

            for(int crossType = 0; crossType<=3; crossType++) {
                solver.crossoverType = crossType;
                Broadcast<Solver> broadcast = sparkContext.broadcast(solver);

                for (int j = 0; j < 10; j++) {

                    double initialTime = System.currentTimeMillis();

                    Population population = new Population();
                    population.generateInitialPopulation(solver.environment);

/*

        System.out.println("################### Parameters ###################");
        System.out.println("Generations: " + iterations * 10);
        System.out.println("Quantity of initial individuals: " + population.LIMIT_OF_INDIVIDUALS);
        System.out.println("Rate of elitism: 5% change it");
        System.out.println("Rate of random mutation: 5%");
        System.out.println("Rate of inverse mutation: 5%");
        System.out.println("Rate of crossover OX: 100%");
        System.out.println("Rate of crossover CX: 0% change it");
        System.out.println("Rate of crossover PMX: 0% change it");
*/


                    double shortestDistance = Double.MAX_VALUE;
                    int countMaxSameDistance = 0;


                    for (int i = 0; i < iterations; i++) {
                        JavaRDD<Population> datasetPopulation = sparkContext.parallelize(population.fork(sparkContext.defaultParallelism()));

                        population = datasetPopulation.map(new Function<Population, Population>() {
                            @Override
                            public Population call(Population population) throws Exception {
                                final int maxGeneration = 10;
                                broadcast.getValue().solve(population.getPopulation(), maxGeneration, Population.LIMIT_OF_INDIVIDUALS);

                                for (int i = 0; i < maxGeneration; i++) {

                                    broadcast.getValue().evaluateIndividuals(population.getPopulation(), broadcast.getValue().environment);

                                    List<Chromosome> selectedBestIndividuals = broadcast.getValue().elitism(population.getPopulation(), 10);

                                    List<Chromosome> selectedIndividuals = broadcast.getValue().selectIndividuals(population.getPopulation(), Population.LIMIT_OF_INDIVIDUALS);

                                    List<Chromosome> newPopulation = broadcast.getValue().crossoverIndividuals(selectedIndividuals);

                                    broadcast.getValue().evaluateIndividuals(newPopulation, broadcast.getValue().environment);

                                    broadcast.getValue().mutation(newPopulation);

                                    broadcast.getValue().evaluateIndividuals(newPopulation, broadcast.getValue().environment);

                                    population.getPopulation().clear();

                                    population.getPopulation().addAll(newPopulation);
                                    population.getPopulation().addAll(selectedBestIndividuals);
                                }

                                population.generation += maxGeneration;

                                return population;
                            }
                        }).reduce(new Function2<Population, Population, Population>() {
                            @Override
                            public Population call(Population population1, Population population2) throws Exception {
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

                                for (int i = 0; i < Population.LIMIT_OF_INDIVIDUALS; i++) {
                                    newPopulation.population.add(individuals.get(i).clone());
                                }

                                return newPopulation;
                            }
                        });

                        if (population.getShortest().getFitnessDistance() == shortestDistance) {
                            //Break there is no more evolution
                            if (countMaxSameDistance == 100) break;
                            countMaxSameDistance++;
                        } else {
//                shortestDistance = population.getShortest().getFitnessDistance();
//                System.out.println(population.getShortest().getFitnessDistance());
//                System.out.println(population.getShortest().getQuantityOfRestrictions());
//                System.out.println(population.getShortest().getDna());
                            countMaxSameDistance = 0;
                        }

                    }

                    result += population.getShortest().getFitnessDistance();
                    time += (System.currentTimeMillis() - initialTime) / 1000.0;

                }

                System.out.println("Quantity of iterations\t"+(iterations*10));
                System.out.println("Crossover type\t"+crossType);
                System.out.println("Result\t"+(result/10));
                System.out.println("Time\t"+(time/10));
            }

        }
    }

}
