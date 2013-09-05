package akka.pipe.example.java.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.pattern.Patterns;
import akka.pipe.example.java.messages.PowerNumbersExchange;
import akka.pipe.example.java.messages.RandomNumbersExchange;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * User: liorpe
 * Date: 9/2/13
 */
public class PipelineActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    ActorRef generator;
    ActorRef calculator;
    ActorRef filter;

    public PipelineActor() {
        generator = getContext().actorOf(new Props(NumberGeneratorActor.class),"NumberGeneratorActor");
        calculator = getContext().actorOf(new Props(NumberPowerActor.class),"NumberPowerActor");
        filter = getContext().actorOf(new Props(NumberFilterActor.class),"NumberFilterActor");
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            if (message.equals("process")) {
                log.debug("Received String message: {}", message);
                simplePipeline();
            } else if (message.equals("foundOne")) {
                System.out.print("*");
            } else if (message.equals("completed")) {
                System.out.println("completed");
            }
        } else
            unhandled(message);
    }

    private void simplePipeline() throws Exception {
        Timeout timeout = new Timeout(Duration.create(100, TimeUnit.MILLISECONDS));
        //create ask to numbers generator
        Future<Object> future = Patterns.ask(generator, "begin calculation", timeout);
        RandomNumbersExchange result = (RandomNumbersExchange) Await.result(future, timeout.duration());

        //pipe response to numbers power calculator
        future = Patterns.ask(calculator, result, timeout);
        PowerNumbersExchange finalResult = (PowerNumbersExchange) Await.result(future, timeout.duration());

        //fire calculation result to numbers filter
        filter.tell(finalResult, getSelf());
    }

    private static SupervisorStrategy strategy =
      new OneForOneStrategy(10, Duration.create("1 minute"),
        new Function<Throwable, SupervisorStrategy.Directive>() {
          @Override
          public SupervisorStrategy.Directive apply(Throwable t) {
            if (t instanceof ArithmeticException) {
                return SupervisorStrategy.resume();
            } else if (t instanceof NullPointerException) {
              return SupervisorStrategy.restart();
            } else if (t instanceof IllegalArgumentException) {
              return SupervisorStrategy.stop();
            } else {
              return SupervisorStrategy.escalate();
            }
          }
        });

    @Override
    public SupervisorStrategy supervisorStrategy() {
      return strategy;
    }
}
