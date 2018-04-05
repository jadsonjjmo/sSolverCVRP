package geneticAlgorithm;

import java.io.*;
import java.util.*;

/**
 * @author Jadson Oliveira <jadsonjjmo@gmail.com>
 */

public class Solver implements Serializable{

    private final Random RANDOM_NUMBER = new Random();
    public final Environment environment = new Environment();
    public int crossoverType = 0;

    //Initialize Solver with the environment restrictions


    public List<Chromosome> solve(List<Chromosome> population, int maxGeneration, int limitOfIndividuals) {

        for (int i = 0; i < maxGeneration; i++) {

            evaluateIndividuals(population, environment);

            List<Chromosome> selectedBestIndividuals = elitism(population, (int)Math.ceil(limitOfIndividuals*0.10));

            List<Chromosome> selectedIndividuals = selectIndividuals(population, limitOfIndividuals);

            List<Chromosome> newPopulation = crossoverIndividuals(selectedIndividuals);

            evaluateIndividuals(newPopulation, environment);

            mutation(newPopulation);

            evaluateIndividuals(newPopulation, environment);

            population.clear();

            population.addAll(newPopulation);
            population.addAll(selectedBestIndividuals);
        }

        return population;
    }

    public List<Chromosome> selectIndividuals(List<Chromosome> population, int limitOfIndividuals) {
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

    public List<Chromosome> crossoverIndividuals(List<Chromosome> population) {
//        int randomNumber = RANDOM_NUMBER.nextInt(100) % 4;
//        int randomNumber = 0;

        switch (crossoverType) {
            case 0:
                return crossoverOX(population);
            case 1:
                return crossoverCX(population);
            case 2:
                return crossoverTemp(population);
            default:
                return crossoverPMX(population);
        }
    }

    public void evaluateIndividuals(List<Chromosome> population, Environment environment) {
        for (Chromosome chromosome : population) {
            environment.fixChromosome(chromosome);
            environment.firstEvaluationMethod(chromosome);
            environment.secondEvaluationMethod(chromosome);
        }
    }

    public List<Chromosome> elitism(List<Chromosome> population, int limitIndividuals) {

        List<Chromosome> newPopulation = new ArrayList<>();

        Collections.sort(population, (chromosome1, chromosome2) -> {

            final int compareQuantityOfBrokenRestriction = Integer.compare(chromosome1.getQuantityOfRestrictions(), chromosome2.getQuantityOfRestrictions());
            final int compareFitnessDistance = Double.compare(chromosome1.getFitnessDistance(), chromosome2.getFitnessDistance());
            final int compareFitnessRoutes = Double.compare(chromosome1.getFitnessRoutes(), chromosome2.getFitnessRoutes());

            return compareQuantityOfBrokenRestriction == 0 ?
                    (compareFitnessDistance == 0 ? compareFitnessRoutes : compareFitnessDistance) :
                    compareQuantityOfBrokenRestriction;
        });

        for(int i = 0; i<limitIndividuals; i++){
            newPopulation.add(population.get(i).clone());
        }

        return newPopulation;
    }

    public List<Chromosome> rouletteSelection(List<Chromosome> population, int limitOfIndividuals) {
        List<Chromosome> selectedIndividuals = new ArrayList<>();

        int sum = 0;

        for (Chromosome chromosome : population) {
            sum += chromosome.getFitnessDistance() / chromosome.getFitnessRoutes();
        }

        for (int i = 0; i < limitOfIndividuals; i++) {
            long auxiliaryNumber = 0;
            int randomNumber = RANDOM_NUMBER.nextInt(sum);
            for (int j = 0; j < population.size(); j++) {

                auxiliaryNumber += (long) population.get(j).getFitnessDistance() / population.get(j).getFitnessRoutes();

                if (j == 0) continue;

                if (auxiliaryNumber > randomNumber) {
                    selectedIndividuals.add(new Chromosome(population.get(j - 1)));
                    break;
                }

            }

        }


        return selectedIndividuals;
    }

    public List<Chromosome> randomSelection(List<Chromosome> population, int limitOfIndividuals) {

        List<Chromosome> selectedIndividuals = new ArrayList<>();

        if (population.size() == 0) {
            return selectedIndividuals;
        }

        //TODO: try to optimize it
        for (int i = 0; i < limitOfIndividuals; i++) {
            selectedIndividuals.add(population.remove(RANDOM_NUMBER.nextInt(population.size())));
        }

        return selectedIndividuals;

    }

    public List<Chromosome> tournamentSelection(List<Chromosome> population, int limitOfIndividuals) {
        List<Chromosome> selectedIndividuals = new ArrayList<>();
        List<Chromosome> newPopulation = new ArrayList<>();

        for (int i = 0; i < Math.min(limitOfIndividuals * 1.5, population.size()); i++) {
            selectedIndividuals.add(population.get(RANDOM_NUMBER.nextInt(population.size())).clone());
        }

        Collections.sort(selectedIndividuals, (chromosome1, chromosome2) -> {

            final int compareQuantityOfBrokenRestriction = Integer.compare(chromosome1.getQuantityOfRestrictions(), chromosome2.getQuantityOfRestrictions());
            final int compareFitnessDistance = Double.compare(chromosome1.getFitnessDistance(), chromosome2.getFitnessDistance());
            final int compareFitnessRoutes = Double.compare(chromosome1.getFitnessRoutes(), chromosome2.getFitnessRoutes());

            return compareQuantityOfBrokenRestriction == 0 ?
                    (compareFitnessDistance == 0 ? compareFitnessRoutes : compareFitnessDistance) :
                    compareQuantityOfBrokenRestriction;
        });

        for(int i = 0; i < limitOfIndividuals; i++){
            newPopulation.add(selectedIndividuals.get(i).clone());
        }

        return newPopulation;
    }

    public List<Chromosome> crossoverOX(List<Chromosome> population) {

        List<Chromosome> newPopulation = new ArrayList<>(population);

        for (int i = 0; i < population.size(); i++) {

            for (int j = 0; j < population.size(); j++) {

                if (i == j) {
                    continue;
                }

                int cutPoint1 = RANDOM_NUMBER.nextInt(population.get(0).getDna().size());
                int cutPoint2 = Math.min(RANDOM_NUMBER.nextInt(population.get(0).getDna().size()) + cutPoint1, population.get(0).getDna().size());

                ArrayList<Integer> newDna = new ArrayList<>();

                for (int k = 0; k < Math.min(cutPoint1, population.get(i).getDna().size()); k++) {
                    newDna.add(population.get(i).getDna().get(k));
                }
                for (int k = cutPoint1; k < Math.min(cutPoint2, population.get(j).getDna().size()); k++) {
                    newDna.add(population.get(j).getDna().get(k));
                }
                for (int k = cutPoint2; k < population.get(i).getDna().size(); k++) {
                    newDna.add(population.get(i).getDna().get(k));
                }

                newPopulation.add(new Chromosome(newDna, 0, 0));
            }

        }

        return newPopulation;
    }

    public List<Chromosome> crossoverCX(List<Chromosome> population) {

        List<Chromosome> newPopulation = new ArrayList<>(population);

        for (int i = 0; i < population.size(); i++) {

            for (int j = 0; j < population.size(); j++) {

                if (i == j) {
                    continue;
                }

                ArrayList<Integer> newDna = new ArrayList<>();

                int cutPoint = 0;
                int lastPosition = 0;

                while (cutPoint < population.size()) {
                    cutPoint += RANDOM_NUMBER.nextInt(population.get(i).getDna().size());
                    for (int k = lastPosition; k < Math.min(cutPoint, population.get(i).getDna().size()); k++) {
                        newDna.add(population.get(i).getDna().get(k));
                    }

                    lastPosition = cutPoint;

                    cutPoint += RANDOM_NUMBER.nextInt(population.get(j).getDna().size());
                    for (int k = lastPosition; k < Math.min(cutPoint, population.get(j).getDna().size()); k++) {
                        newDna.add(population.get(j).getDna().get(k));
                    }

                    lastPosition = cutPoint;
                }

                newPopulation.add(new Chromosome(newDna, 0, 0));

            }

        }

        return newPopulation;
    }

    public List<Chromosome> crossoverPMX(List<Chromosome> population) {

        List<Chromosome> newPopulation = new ArrayList<>(population);

        for (int i = 0; i < population.size(); i++) {

            for (int j = 0; j < population.size(); j++) {

                if (i == j) continue;

                int cutPoint = RANDOM_NUMBER.nextInt(population.get(i).getDna().size());

                ArrayList<Integer> newDna = new ArrayList<>();

                for (int dnaIndex = 0; dnaIndex < Math.min(population.get(i).getDna().size(), population.get(j).getDna().size()); dnaIndex++) {
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


    public List<Chromosome> crossoverTemp(List<Chromosome> population){
        List<Chromosome> newPopulation = new ArrayList<>(population);

        for(int i = 0; i<population.size(); i++){
            for(int j = 0; j<population.size(); j++){

                if (i == j) continue;

                int cutPoint = RANDOM_NUMBER.nextInt(population.get(i).getDna().size());

                ArrayList<Integer> newDna = new ArrayList<>();

                for(int k = 0; k<Math.min(population.get(i).getDna().size(), population.get(j).getDna().size()); k++){
                    if(population.get(i).getDna().get(k) == population.get(j).getDna().get(k)){
                        newDna.add(population.get(i).getDna().get(k));
                    } else{
                        int randomGeneParent = RANDOM_NUMBER.nextInt(2);

                        if(population.get(i).getDna().size() <= k){
                            randomGeneParent = 1;
                        }
                        if(population.get(j).getDna().size() <= k){
                            randomGeneParent = 0;
                        }

                        if(randomGeneParent == 0){
                            newDna.add(population.get(i).getDna().get(k));
                        } else{
                            newDna.add(population.get(j).getDna().get(k));
                        }
                    }
                }

                newPopulation.add(new Chromosome(newDna, 0, 0));


            }
        }

        return population;

    }


    public void mutation(List<Chromosome> population){

        int randomMutation = RANDOM_NUMBER.nextInt(101);
        int reverseMutation = RANDOM_NUMBER.nextInt(101);

        if(randomMutation <= 5){
            randomMutation(population);
        }
        if(reverseMutation <= 5) {
            reverseMutation(population);
        }

    }

    public void randomMutation(List<Chromosome> population) {

        if(population.size() == 0)return;


        int quantityOfIndividuals = RANDOM_NUMBER.nextInt(population.size());

        for(int i = 0; i<quantityOfIndividuals; i++) {
            int quantityOfMutations = RANDOM_NUMBER.nextInt(population.get(i).getDna().size()/3);
            for (int j = 0; j < quantityOfMutations; j++) {
                int position1 = RANDOM_NUMBER.nextInt(population.get(i).getDna().size());
                int position2 = RANDOM_NUMBER.nextInt(population.get(i).getDna().size());

                int aux1 = population.get(i).getDna().get(position1);
                int aux2 = population.get(i).getDna().get(position2);
                population.get(i).getDna().set(position1, aux2);
                population.get(i).getDna().set(position2, aux1);
            }
        }

    }

    public void reverseMutation(List<Chromosome> population){
        Collections.reverse(population);
    }


    public void readInput() throws IOException{

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        StringBuilder stringBuilder = new StringBuilder();

        String stringName = bufferedReader.readLine();
        String stringComments = bufferedReader.readLine();
        String stringType = bufferedReader.readLine();
        String stringDimension = bufferedReader.readLine();
        String stringCostType = bufferedReader.readLine();
        String stringCapacity = bufferedReader.readLine();

        int quantityOfNodes = Integer.parseInt(stringDimension.split(" : ")[1].trim());
        int capacity = Integer.parseInt(stringCapacity.split(" : ")[1].trim());

        environment.setCosts(new double[quantityOfNodes]);
        environment.setDistancePoints(new double[quantityOfNodes][quantityOfNodes]);
        environment.setVehiclesCapacity(capacity);
        environment.setQuantityOfVehicles(4);//Change it


        int coordinates[][] = new int[2][quantityOfNodes];


        bufferedReader.readLine();//NODE COORDINATES SECTION

        for(int i = 0; i<quantityOfNodes; i++){
            StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine().trim());

            int id = Integer.parseInt(stringTokenizer.nextToken())-1;
            int x = Integer.parseInt(stringTokenizer.nextToken().trim());
            int y = Integer.parseInt(stringTokenizer.nextToken().trim());

            coordinates[0][id] = x;
            coordinates[1][id] = y;
        }

        bufferedReader.readLine();//DEMAND SECTION


        for(int i = 0; i<quantityOfNodes; i++){
            StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());

            int id = Integer.parseInt(stringTokenizer.nextToken());
            int demand = Integer.parseInt(stringTokenizer.nextToken());

            environment.getCosts()[id-1] = demand;
        }

        for(int i = 0; i<quantityOfNodes; i++){

            for(int j = i+1; j<quantityOfNodes; j++){

                environment.getDistancePoints()[i][j] = Math.sqrt(Math.pow(coordinates[0][i]-coordinates[0][j], 2) + Math.pow(coordinates[1][i]-coordinates[1][j], 2));
                environment.getDistancePoints()[j][i] = Math.sqrt(Math.pow(coordinates[0][i]-coordinates[0][j], 2) + Math.pow(coordinates[1][i]-coordinates[1][j], 2));

            }

        }

    }

/*
    public static void readInput() throws IOException{

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        StringBuilder stringBuilder = new StringBuilder();

        String stringName = bufferedReader.readLine();
        String stringType = bufferedReader.readLine();
        String stringComments = bufferedReader.readLine();
        String stringDimension = bufferedReader.readLine();
        String stringCapacity = bufferedReader.readLine();
        String stringQuantityVehicles = bufferedReader.readLine();
        String stringIgnore1 = bufferedReader.readLine();
        String stringIgnore2 = bufferedReader.readLine();
        String stringIgnore3 = bufferedReader.readLine();
        String stringIgnore4 = bufferedReader.readLine();


        int quantityOfNodes = Integer.parseInt(stringDimension.split(": ")[1].trim());
        int quantityOfVehicles = Integer.parseInt(stringQuantityVehicles.split(": ")[1].trim());
        int capacity = Integer.parseInt(stringCapacity.split(": ")[1].trim());

        environment.setCosts(new double[quantityOfNodes]);
        environment.setDistancePoints(new double[quantityOfNodes][quantityOfNodes]);
        environment.setVehiclesCapacity(capacity);
        environment.setQuantityOfVehicles(quantityOfVehicles);//Change it


        double coordinates[][] = new double[2][quantityOfNodes];


        bufferedReader.readLine();//NODE COORDINATES SECTION

        for(int i = 1; i<quantityOfNodes-1; i++){
            StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine().trim());

            int id = Integer.parseInt(stringTokenizer.nextToken()) - 1;
            double x = Double.parseDouble(stringTokenizer.nextToken().trim());
            double y = Double.parseDouble(stringTokenizer.nextToken().trim());

            coordinates[0][id] = x;
            coordinates[1][id] = y;
        }

        bufferedReader.readLine();//DEMAND SECTION


        for(int i = 1; i<quantityOfNodes-1; i++){
            StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine());

            int id = Integer.parseInt(stringTokenizer.nextToken());
            int demand = Integer.parseInt(stringTokenizer.nextToken());

            environment.getCosts()[id-1] = demand;
        }


        for(int i = 0; i<quantityOfNodes; i++){

            for(int j = i+1; j<quantityOfNodes; j++){

                environment.getDistancePoints()[i][j] = Math.sqrt(Math.pow(coordinates[0][i]-coordinates[0][j], 2) + Math.pow(coordinates[1][i]-coordinates[1][j], 2));
                environment.getDistancePoints()[j][i] = Math.sqrt(Math.pow(coordinates[0][i]-coordinates[0][j], 2) + Math.pow(coordinates[1][i]-coordinates[1][j], 2));

            }

        }



    }
*/

}
