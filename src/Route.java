import java.util.ArrayList;

public class Route {
    private ArrayList<Taxa> taxas;
    private double score;

    public Route(ArrayList<Taxa> taxas,double score){
        this.taxas=taxas;
        this.score=score;
    }

    public ArrayList<Taxa> getTaxas(){return taxas;}
    public double getScore(){return score;}

    public void printRoute(){
        for(int i=0;i<taxas.size();i++){
            System.out.print(taxas.get(i).getIndex()+"  ");
        }
        System.out.println();
    }
}
