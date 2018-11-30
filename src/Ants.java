import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Ants {
    public static final double ALPHA=1; //Parameter used for controlling the importance of the  pheramone trail. The value is >=0
    public static final double BETA=1; //Parameter used for controlling the importance of the distance between source and destination
    public static final double DELTA=0.5; //Parameter used for recalculate the distance...

    private ACO aco;
    private double PHEROMONE_EVAP_FACTOR;
    private int NumOfAnts=0;
    private Route route=null;
    static int invalidNodeIndex=-1;
    static int numbOfTaxas=Driver.initialRoute.size();
    private double transProb=0.0;
    private int iterator=0;

    public Ants(ACO aco,int NumOfAnts,double PEV){
        this.aco=aco;
        this.NumOfAnts=NumOfAnts;
        this.PHEROMONE_EVAP_FACTOR=PEV;
    }

    public double call(){
        int originatingCityIndex=ThreadLocalRandom.current().nextInt(numbOfTaxas);
        //Random random=new Random();
        //int originatingCityIndex = random.nextInt(numbOfTaxas);
        ArrayList<Taxa> routeNodes=new ArrayList<Taxa>(numbOfTaxas);
        HashMap<String, Boolean> visitedNodes=new HashMap<String, Boolean>(numbOfTaxas);
        IntStream.range(0,numbOfTaxas).forEach(x->visitedNodes.put(Driver.initialRoute.get(x).getName(),false));
        double antsInNode=NumOfAnts;
        double antsInPath=0;
        int numbOfVisitedNodes=0;
        double routeScore=0.0;
        int x=originatingCityIndex;
        int y=0;
        routeNodes.add(numbOfVisitedNodes++,Driver.initialRoute.get(x));
        visitedNodes.put(Driver.initialRoute.get(x).getName(),true);

        while (y!=invalidNodeIndex) {
            if (numbOfVisitedNodes < numbOfTaxas) {
                y = getY(x, visitedNodes);
                if (y != invalidNodeIndex) {
                    routeNodes.add(numbOfVisitedNodes++, Driver.initialRoute.get(y));
                    visitedNodes.put(Driver.initialRoute.get(y).getName(), true);
                    localPheramoneUpdate(aco, x, y); //Local Pheramone update
                    //routeScore += transProb; //add trans. prob. for next node to routeScore
                    antsInNode = antsInNode * transProb; //calculate the amount of ants in next node
                    routeScore += antsInNode; //add trans. prob. for next node to routeScore

                    antsInPath+=antsInNode; //add no of ants in node to no of ants in path
                    recalculate_distance(x, y, visitedNodes);

                    x = y;
                    iterator++;     //==========//

                }
            } else
            {y = invalidNodeIndex;}
        }
        route=new Route(routeNodes,routeScore);
        aco.resetDistanceMatrix(); //Reset the extra values in distance matrix...
       // aco.printPheramoneMatrix();
        //return antsInNode;
        return antsInPath;
    }

    private int getY(int x,HashMap<String, Boolean> visitedNodes){
        int returnY=invalidNodeIndex;
        ArrayList<Double> transitionProbabilities=getTransitionProbabilities(x,visitedNodes);
        double random=ThreadLocalRandom.current().nextDouble(); //???

        double max=0.0;
        int transProbSize=transitionProbabilities.size();
        for(int y=0;y<transProbSize;y++){
            if(transitionProbabilities.get(y)>max) {
            //if(transitionProbabilities.get(y)>0.1) {
                max=transitionProbabilities.get(y);
                returnY = y;
                //break;
            }
//
        }

//        for(int y=0;y<transProbSize;y++){
//            if(transitionProbabilities.get(y)>random) {
//                max=transitionProbabilities.get(y);
//                returnY = y;
//                break;
//            }
//            else
//                random -= transitionProbabilities.get(y);
//        }

        transProb=max; //set transition probability value to add to routeScore...
        return returnY;
    }

    private ArrayList<Double> getTransitionProbabilities(int x,HashMap<String, Boolean> visitedCities){
        ArrayList<Double> transitionProbabilities=new ArrayList<Double>(numbOfTaxas);
        IntStream.range(0,numbOfTaxas).forEach(i->transitionProbabilities.add(0.0)); //Initialize Transition probabilities as 0
        double denominator=getTPDenominator(transitionProbabilities,x,visitedCities);
        IntStream.range(0,numbOfTaxas).forEach(y->{
            if(y!=x)
                transitionProbabilities.set(y, transitionProbabilities.get(y)/denominator);
        });

        return transitionProbabilities;
    }

    private double getTPDenominator(ArrayList<Double> transitionProbabilities, int x, HashMap<String, Boolean> visitedCities){
        double denominator=0.0;
        for(int y=0;y<numbOfTaxas;y++){
            if(!visitedCities.get(Driver.initialRoute.get(y).getName())){
                transitionProbabilities.set(y,getTPNumerator(x,y));
                denominator += transitionProbabilities.get(y);
            }
        }
        return denominator;
    }

    private double getTPNumerator(int x, int y){
        double numerator=0.0;
        double phermoneLevel=aco.getPheramoneMatrix()[y][x];

        if (phermoneLevel != 0.0) {
            if (iterator == 0) {
                numerator = Math.pow(phermoneLevel, ALPHA) * Math.pow(1 / aco.getDistancesMatrix()[x][y], BETA);
            } else {
                numerator = Math.pow(phermoneLevel, ALPHA) * Math.pow(1 / aco.getDistancesMatrix()[numbOfTaxas+iterator-1][y], BETA);
            }
        }
        return numerator;
    }

    private void recalculate_distance(int x,int y,HashMap<String, Boolean> visitedCites){    //============================//
        for(int i=0;i<numbOfTaxas;i++){
            if(!visitedCites.get(Driver.initialRoute.get(i).getName())) {
                double value=0;
                double distXY = aco.getDistancesMatrix()[x][y];
                double distXI = 0;
                double distIY = aco.getDistancesMatrix()[i][y];
                if(iterator==0) {
                    distXI = aco.getDistancesMatrix()[x][i];
                }
                else {
                    distXI = aco.getDistancesMatrix()[numbOfTaxas+iterator-1][i];
                }

                if(distIY>=distXI)
                    value=distXI+(distXI-distIY)*DELTA; //Calculation for recalculate the distance from new node...
                else if(distIY<distXI)
                    value=distXI+(distIY-distXI)*DELTA; //Calculation for recalculate the distance from new node...

                aco.setDistancesMatrix(i,numbOfTaxas+iterator,value);
            }
            else{
                //aco.setDistancesMatrix(i,numbOfTaxas+iterator,0);
            }
        }
    }

    public Route getRoute(){return route;}

    public void localPheramoneUpdate(ACO aco, int x, int y){
        double oldPValue = aco.getPheramoneMatrix()[x][y];
        double distance=aco.getDistancesMatrix()[x][y];
        //double newValue = ((1 - PHEROMONE_EVAP_FACTOR) * oldPValue)+(PHEROMONE_EVAP_FACTOR * oldPValue);
        double newValue = ((1 - PHEROMONE_EVAP_FACTOR) * oldPValue)+(0.005/distance);
        if (newValue < 0.00)
            aco.setPheramoneMatrix(x,y,0);
        else
            aco.setPheramoneMatrix(x,y,newValue);
    }
}
