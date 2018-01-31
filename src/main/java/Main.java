import geneticAlgorithm.Environment;
import geneticAlgorithm.Population;
import geneticAlgorithm.Solver;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * @author Jadson Oliveira <jadsonjjmo@gmail.com>
 */

public class Main {

    public static void main(String[] args){

        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("sSolverCVRP");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        //Quantity of iterations
        int iterations = 10;

        Population population = new Population();
        population.generateInitialPopulation();

        for(int i = 0; i < iterations; i++){
            JavaRDD<Population> datasetPopulation = sparkContext.parallelize(population.fork(sparkContext.defaultParallelism()));

            population = datasetPopulation.map(Population::evolve).reduce(Population::combine);

            System.out.println(population.getShortest().getDna());
        }


    }

}
