import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class ACO {
    private Double[][] phermoneMatrix=null;
    private double[][] distancesMatrix=null;
    private int taxasSize=Driver.initialRoute.size();

    public ACO(double[][] basicDistancesMatrix) throws IOException {
        initializeDistances(basicDistancesMatrix);
        initializePhermoneLevels();
        //printDistanceMatrix();
    }

    public void initializePhermoneLevels(){
        phermoneMatrix=new Double[taxasSize][taxasSize];
        IntStream.range(0,taxasSize).forEach(x->{
            IntStream.range(0,taxasSize).forEach(y->phermoneMatrix[x][y]=0.01);
        });
    }

    public void initializeDistances(double[][] basicDistancesMatrix){
        distancesMatrix=new double[2*taxasSize-2][2*taxasSize-2];
        for (int row = 0; row < 2*taxasSize-2; row ++) {
            for (int col = 0; col < 2*taxasSize-2; col++) {
                if(col<taxasSize && row<taxasSize) {
                    this.setDistancesMatrix(row,col,basicDistancesMatrix[row][col]);
                }
                else
                {
                    this.setDistancesMatrix(row,col,0.0);
                }
            }
        }
    }

    public void setDistancesMatrix(int x,int y,double value) {
        distancesMatrix[x][y]=value;
        distancesMatrix[y][x]=value;
    }

    public void setPheramoneMatrix(int x,int y,double value) {
        phermoneMatrix[x][y]=value;
        phermoneMatrix[y][x]=value;
    }

    public Double[][] getPheramoneMatrix(){return phermoneMatrix;}

    public double[][] getDistancesMatrix() {
        return distancesMatrix;
    }

    public void resetDistanceMatrix(){
        for (int row = 0; row < 2*taxasSize-2; row ++) {
            if(row>=taxasSize){
                for (int col = 0; col < 2 * taxasSize - 2; col++) {
                    this.setDistancesMatrix(row, col, 0.0);
                }
            }
            else {
                for (int col = taxasSize; col < 2 * taxasSize - 2; col++) {
                    this.setDistancesMatrix(row, col, 0.0);
                }
            }
        }
    }

    public void printPheramoneMatrix(){
        IntStream.range(0,taxasSize).forEach(x-> {
            IntStream.range(0, taxasSize).forEach(y -> {
                System.out.print(phermoneMatrix[x][y]+"  ");
            });
            System.out.println();
        });
    }
}