package akka.pipe.example.java.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pipe.example.java.messages.PowerNumbersExchange;
import akka.pipe.example.java.messages.RandomNumbersExchange;

/**
 * User: liorpe
 * Date: 9/2/13
 */
public class NumberPowerActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public void onReceive(Object message) throws Exception {
        //power numbers calculator
        if (message instanceof  RandomNumbersExchange) {
            log.debug("NumberPowerActor.process : {}", message);
            RandomNumbersExchange randomNumbersExchange = (RandomNumbersExchange) message;
            int first = randomNumbersExchange.getFirst();
            int second = randomNumbersExchange.getSecond();
            getSender().tell(new PowerNumbersExchange(first, second, Double.toString(Math.pow(first, second))), getSelf());
        } else
            unhandled(message);
    }
}
