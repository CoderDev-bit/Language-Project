package cipher;

public class Letter implements Comparable<Letter>{
    private final int frequency;
    private final char letter;

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

    @Override
    public String toString() {
        return "Char: " + letter + " Frequency: " + frequency;
    }

    public int compareTo(Letter text){
        if (text == null || frequency > text.getFreq()) { return -1; }
        if (frequency == text.getFreq()) { return 0; }
        return 1;
    }
}