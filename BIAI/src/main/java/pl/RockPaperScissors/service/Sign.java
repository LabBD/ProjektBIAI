package pl.RockPaperScissors.service;

/**
 * Created by Kamil S on 2016-06-07.
 */
public enum Sign {
    ROCK(-1.0), PAPER(0.0),SCISSORS(1.0);
  
    private double value;

    public static Sign get(double value){
        if(value<=-0.5)
            return ROCK;
        else if(value>=0.5)
            return SCISSORS;
        else
            return PAPER;
    }

    public Sign counterSign(){
        if(value==-1.0)
            return PAPER;
        else if(value==0.0)
            return SCISSORS;
        else if(value==1.0)
            return ROCK;
        else
            return null;
    }

    Sign(double value) {
        this.value=value;
    }


    public double toDouble() {
        return value;
    }
}
