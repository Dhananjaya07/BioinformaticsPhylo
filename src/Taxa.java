public class Taxa {
    private String name;
    private String sciName;
    private int index;

    public Taxa(String name,String sciName,int index) {
        this.name = name;
        this.sciName=sciName;
        this.index = index;
    }

    public String getName(){return name;}
    public String getSciName(){return sciName;};
    public int getIndex(){return index;};
}
