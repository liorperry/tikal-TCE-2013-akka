package akka.pipe.example.java.messages;

/**
 * User: liorpe
 * Date: 9/2/13
 */
public class RandomNumbersExchange {
    private final int first;
    private final int second;

    public RandomNumbersExchange(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "RandomNumbersExchange{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
