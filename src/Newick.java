import java.util.ArrayList;

public class Newick {
    static int taxasSize=Driver.initialRoute.size();
    ACO aco;
    private ArrayList<Taxa> taxaSeq=new ArrayList<Taxa>();
    private ArrayList<Elemenet> elementSeq=new ArrayList<Elemenet>();

    public Newick(ACO aco,ArrayList<Taxa> taxaSeq){
        this.aco=aco;
        this.taxaSeq=taxaSeq;
        InitializeSeqElementList();

        while(elementSeq.size()>1) {
            int min = FindMin();
            UpdateSeq(min);
        }
        System.out.println();
        System.out.println(elementSeq.get(0).getBackString());
    }

    private void InitializeSeqElementList(){
        for(int i=0;i<taxasSize;i++) {
            int next = i + 1;
            if(i==taxasSize-1) next=0; //last element check with first one...

            int before=taxaSeq.get(i).getIndex();
            int after=taxaSeq.get(next).getIndex();

            double distance = aco.getDistancesMatrix()[before][after];
            elementSeq.add(new Elemenet(before,after,distance));
        }
    }

    public int FindMin(){
        int minIndex=0;
        double minVal=elementSeq.get(0).getDistVal();
        int size=elementSeq.size();
        for(int i=1;i<size;i++){
            double curntVal=elementSeq.get(i).getDistVal();
            if(minVal>curntVal){
                minVal=curntVal;
                minIndex=i;
            }
        }
        return minIndex;
    }

    public void UpdateSeq(int minIndex){
        int backIndex=minIndex-1;
        int frontIndex=minIndex+1;
        if(minIndex==0) backIndex=elementSeq.size()-1;
        if(minIndex==elementSeq.size()-1) frontIndex=0;

        ArrayList<Integer> listCurrent=elementSeq.get(minIndex).getListBefore();
        listCurrent.addAll(elementSeq.get(minIndex).getListAfter()); //Add listAfter and listBefore to one list...
        ArrayList<Integer> listBack=elementSeq.get(backIndex).getListBefore(); //Get the previous element before list...
        ArrayList<Integer> listFront=elementSeq.get(frontIndex).getListAfter(); //Get next element after list...
        double backDistTotal=0,frontDistTotal=0;
        int backCount=0,frontCount=0;

        for(int i=0;i<listCurrent.size();i++){
            int ele1=listCurrent.get(i);
            int ele2=0;
            for(int j=0;j<listFront.size();j++){
                ele2=listFront.get(j);
                frontDistTotal+=aco.getDistancesMatrix()[ele1][ele2];
                frontCount++;
            }
            for(int j=0;j<listBack.size();j++){
                ele2=listBack.get(j);
                backDistTotal+=aco.getDistancesMatrix()[ele1][ele2];
                backCount++;
            }
        }

        double frontDist=frontDistTotal/frontCount;
        double backDist=backDistTotal/backCount;
        elementSeq.get(backIndex).setListAfter(listCurrent); //Set back element's listAfter as updated concatenated listCurrent...
        elementSeq.get(frontIndex).setListBefore(listCurrent); //Update front element's listBefore...
        elementSeq.get(backIndex).setDistValue(backDist); //Update distance values...
        elementSeq.get(frontIndex).setDistValue(frontDist);

        String newickString="("+elementSeq.get(minIndex).getBackString()+", "+elementSeq.get(minIndex).getFrontString()+")"; //Build the newick string...
        elementSeq.get(backIndex).setFrontString(newickString); //Update strings in nearby elements...
        elementSeq.get(frontIndex).setBackString(newickString);

        elementSeq.remove(minIndex); //Remove the element which has max distance value from sequence...
    }

}

class Elemenet{
    private ArrayList<Integer> listBefore=new ArrayList<Integer>();
    private ArrayList<Integer> listAfter=new ArrayList<Integer>();
    private String backString="",frontString="";
    private double distValue;
    private int size;

    public Elemenet(int bef, int aftr, double dist){
        this.listBefore.add(bef);
        this.listAfter.add(aftr);
        this.distValue=dist;
        //this.size=size;
        this.backString=Driver.initialRoute.get(bef).getSciName();
        this.frontString=Driver.initialRoute.get(aftr).getSciName();
    }

    public double getDistVal(){
        return this.distValue;
    }
    public ArrayList<Integer> getListBefore(){
        return this.listBefore;
    }
    public ArrayList<Integer> getListAfter(){ return this.listAfter; }
    public String getBackString(){return this.backString;}
    public String getFrontString(){return this.frontString;}
    public void setListBefore( ArrayList<Integer> beforeList){ this.listBefore=beforeList; }
    public void setListAfter( ArrayList<Integer> afterList){ this.listAfter=afterList; }
    public void setDistValue( double distance){ this.distValue=distance; }
    public void setBackString(String backSrt){this.backString=backSrt;}
    public void setFrontString(String frontStr){this.frontString=frontStr;}
}