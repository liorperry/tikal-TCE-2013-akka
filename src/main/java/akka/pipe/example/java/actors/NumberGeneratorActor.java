package akka.pipe.example.java.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pipe.example.java.messages.RandomNumbersExchange;

import java.util.Random;

/**
 * User: liorpe
 * Date: 9/2/13
 */
public class NumberGeneratorActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    Random r = new Random();

    public void onReceive(Object message) throws Exception {
        //numbers generator actor
        if (message instanceof  String) {
            log.debug("NumberGeneratorActor.process : {}", message);
            getSender().tell(new RandomNumbersExchange(r.nextInt(999999),r.nextInt(19)), getSelf());
        } else
            unhandled(message);
    }
}
