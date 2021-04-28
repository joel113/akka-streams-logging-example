package com.joel.akka.streams

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("QuickStart")

  val flow = Flow[Int]
    .map(1 / _) // throwing ArithmeticException: / by zero
    .log("error logging") // only this log statement leads to an exception log statement
    .to(Sink.ignore)

  Source(-5 to 5)
    .wireTap(flow)
    .map(_ + 1)
    .log("error logging")
    .runForeach(println)

}
