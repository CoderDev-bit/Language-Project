package cipher;

public class Letter implements Comparable<Letter>{
    private int frequency;
    private char letter;

    public Letter(char letter, int frequency){
        this.letter = letter;
        this.frequency = frequency;
    }

    public Letter(int frequency){
        this.letter = ' ';
        this.frequency = frequency;
    }

    public char getText(){
        return letter;
    }

    public int getFreq(){
        return frequency;
    }

    public void setFreq(int frequency){
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "Char: " + letter + " Frequency: " + frequency + "\n";
    }

    public int compareTo(Letter target){
        if (target == null){ return -1; }
        if (frequency > target.getFreq()){ return -1; }
        if (frequency == target.getFreq()){ return 0; }
        return 1;
    }
}