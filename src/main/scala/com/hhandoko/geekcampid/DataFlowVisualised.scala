/**
 * File     : DataFlowVisualised.scala
 * License  :
 *   Copyright (c) 2017 Herdy Handoko
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.hhandoko.geekcampid

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}

object DataFlowVisualised extends App {

  implicit val sys = ActorSystem("visualise")
  implicit val mat = ActorMaterializer()

  val graph = GraphDSL.create() { implicit builder =>
    val src  = Source(1 to 1000)
    val sink = Sink.foreach[String](println)

    val f1 = Flow[Int].map {
      case i if i % 15 == 0 => "FizzBuzz"
      case i if i % 5 == 0  => "Buzz"
      case i if i % 3 == 0  => "Fizz"
      case i                => i.toString
    }

    val t1 = Flow[String].map {
      case "Fizz" => "_Fizz"
      case i      => i
    }
    val t2 = Flow[String].map {
      case "Buzz" => "Buzz!"
      case i      => i
    }
    val t3 = Flow[String].map {
      case i @ "FizzBuzz" => i.toUpperCase
      case i              => i
    }

    import GraphDSL.Implicits._
    src ~> f1 ~> t1 ~> t2 ~> t3 ~> sink

    ClosedShape
  }

  RunnableGraph.fromGraph(graph)
    .run()

  sys.scheduler.scheduleOnce(1.second) { sys.terminate() }

}
