import geneticAlgorithm.Population;
import geneticAlgorithm.Solver;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;

/**
 * @author Jadson Monteiro <jadsonjjmo@gmail.com>
 */

public class Main {

    public static void main(String[] args) throws IOException{

        SparkConf sparkConf = new SparkConf().setMaster("local[4]").setAppName("sSolverCVRP");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        sparkContext.setLogLevel("ERROR");

        //Quantity of iterations
        int iterations = 700;

        Solver.readInput();

        double initialTime = System.currentTimeMillis();

        Population population = new Population();
        population.generateInitialPopulation(Solver.environment);

        double shortestDistance = Double.MAX_VALUE;

        for(int i = 0; i < iterations || population.getShortest().getQuantityOfRestrictions() != 0; i++){
            JavaRDD<Population> datasetPopulation = sparkContext.parallelize(population.fork(sparkContext.defaultParallelism()));

            population = datasetPopulation.map(Population::evolve).reduce(Population::combine);

            if(i<iterations && population.getShortest().getFitnessDistance() < shortestDistance){
                shortestDistance = population.getShortest().getFitnessDistance();
                System.out.println(population.getShortest().getFitnessDistance());
                System.out.println(population.getShortest().getQuantityOfRestrictions());
                System.out.println(population.getShortest().getDna());
            }

        }

        System.out.println(population.getGeneration());
        System.out.println(population.getShortest().getFitnessDistance());
        System.out.println(population.getShortest().getQuantityOfRestrictions());
        System.out.println(population.getShortest().getDna());

        System.out.println("Time: " + (System.currentTimeMillis() - initialTime)/1000.0);

    }

}
