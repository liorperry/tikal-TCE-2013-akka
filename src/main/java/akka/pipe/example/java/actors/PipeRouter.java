package akka.pipe.example.java.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.BroadcastRouter;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.gracefulStop;

public class PipeRouter extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    //router
    ActorRef router;

    public PipeRouter(int nrOfCustomers) {
        //pipe actor router
        router = getContext().actorOf(
                new Props(PipelineActor.class).withRouter(new BroadcastRouter(nrOfCustomers)),"PipeActor");
    }

    public void onReceive(Object message) {
        log.debug("Received String message: {}", message);
        if (message instanceof String) {
            if (message.equals("begin")) {
                //load balance request across multiple pipeline actors chain
                router.tell("process", getSelf());

            } else if (message.equals("completed")) {
                //when completed stop gracefully
                    Future<Boolean> stopped = gracefulStop(router, Duration.create(5, TimeUnit.SECONDS),getContext().system());
                try {
                    Await.result(stopped, Duration.create(6, TimeUnit.SECONDS));
                } catch (Exception e) {
                    // the actor wasn't stopped within 5 seconds
                    getSender().tell(new akka.actor.Status.Failure(e), getSelf());
                }
            }
        } else {
            unhandled(message);
        }
    }
}