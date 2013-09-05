package akka.pipe.example.java.messages;

/**
 * User: liorpe
 * Date: 9/2/13
 */
public class PowerNumbersExchange extends RandomNumbersExchange {
    private final String result;

    public PowerNumbersExchange(int first, int second,String result) {
        super(first,second);
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "PowerNumbersExchange{" +
                "result='" + result + '\'' +
                '}';
    }
}
