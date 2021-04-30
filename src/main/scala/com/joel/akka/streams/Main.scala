package com.joel.akka.streams

import akka.actor.ActorSystem
import akka.stream.KillSwitches
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object Main {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("QuickStart")

    val anotherFlow = Flow[Int]
      .async // exception boundary
      .map({ bla =>
        1 / 0
        "bla"
      }) // throwing ArithmeticException: / by zero
      .log("error logging") // only this log statement leads to an exception log statement
      .to(Sink.ignore)

    val flow = Flow[Int]
      .divertTo(anotherFlow, _ => true)
      .log("error logging")
      .to(Sink.ignore)

    val (killSwitch, future) = Source(-5 to 5)
      //.wireTap(flow) // exception boundary
      .divertTo(flow, _ => true)
      .map(_ + 1)
      .log("error logging")
      .viaMat(KillSwitches.single)(Keep.right)
      .toMat(Sink.ignore)(Keep.both) // materializes the stream but only keeps the future
      .run()
    //.runForeach(println)
    //.runWith(Sink.foreach(println))

    Await.result(future, 1.second)

    killSwitch.shutdown()

    system.terminate()

  }

}
