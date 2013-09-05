package akka.pipe.example.java.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pipe.example.java.messages.PowerNumbersExchange;

/**
 * User: liorpe
 * Date: 9/2/13
 */
public class NumberFilterActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);


    public void onReceive(Object message) throws Exception {
        //numbers filter actor
        if (message instanceof PowerNumbersExchange) {
            log.debug("NumberFilterActor.process : {}", message);
            String result = ((PowerNumbersExchange) message).getResult();
            if (result.contains("222")) {
                getSender().tell("foundOne",getSelf());
            }
        } else
            unhandled(message);
    }
}
