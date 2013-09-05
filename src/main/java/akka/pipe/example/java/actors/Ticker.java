package akka.pipe.example.java.actors;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * User: liorpe
 * Date: 9/2/13
 */
public class Ticker extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    ActorRef pipe;

    public Ticker(ActorRef pipe) {
        this.pipe = pipe;
        this.getContext().watch(pipe);
    }

    @Override
    public void onReceive(Object message) {
        //time ticker
        log.debug("Received String message: {}", message);
        if (message.equals("Tick")) {
            pipe.tell("begin",getSelf());
        } else if (message instanceof Terminated) {
          final Terminated t = (Terminated) message;
          if (t.getActor() == pipe) {
            getSender().tell("finished", getSelf());
          }
        } else {
            unhandled(message);
        }
    }
}

