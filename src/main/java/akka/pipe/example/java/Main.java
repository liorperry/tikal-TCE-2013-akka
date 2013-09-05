/**
 * Copyright (C) 2009-2012 Typesafe Inc. <http://www.typesafe.com>
 */

package akka.pipe.example.java;

//#imports

import akka.actor.*;
import akka.pipe.example.java.actors.PipeRouter;
import akka.pipe.example.java.actors.Ticker;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

//#app
public class Main {

    public static final String NAME = "PipeExample";

    public static void main(String[] args) {
        Main snake = new Main();
        snake.init(4);
    }

    Cancellable cancellable;
    ActorRef tickActor;

    public void init(final int nrOfWorkers) {
        // Create an Akka system
        ActorSystem system = ActorSystem.create(NAME);

        // create the router actor
        final ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
          public UntypedActor create() {
            return new PipeRouter(Integer.valueOf(nrOfWorkers));
          }
        }), "PipeRouter");

        //akka 2.2 new type of actor creation
//        ActorRef master = system.actorOf(new Props(PipeRouter.class, Integer.valueOf(nrOfWorkers)));

        //This will schedule to send the Tick-message
        //to the tickActor after 0ms repeating every 50ms

        //tick actor
        //akka 2.2 new type of actor creation
//        tickActor = system.actorOf(Props.create(Ticker.class, master));

        tickActor = system.actorOf(new Props(new UntypedActorFactory() {
                  public UntypedActor create() {
                    return new Ticker(master);
                  }
                }), "TickActor");

        //begin ticker & get cancellable
        cancellable = system.scheduler().schedule(Duration.Zero(),
                Duration.create(10, TimeUnit.MILLISECONDS), tickActor, "Tick",
                system.dispatcher());
    }

}