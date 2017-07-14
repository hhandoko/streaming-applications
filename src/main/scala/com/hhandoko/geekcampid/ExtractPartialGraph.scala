/**
 * File     : ExtractPartialGraph.scala
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
import akka.stream.{ActorMaterializer, ClosedShape, FlowShape, SourceShape}

object ExtractPartialGraph extends App {

  implicit val sys = ActorSystem("combine")
  implicit val mat = ActorMaterializer()

  object SourceGraph {
    private val s = Source(1 to 1000)
    private val f = Flow[Int].map {
      case i if i % 15 == 0 => "FizzBuzz"
      case i if i % 5 == 0 => "Buzz"
      case i if i % 3 == 0 => "Fizz"
      case i => i.toString
    }

    val g = Source.fromGraph(GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      val src = builder.add(s)
      val flo = builder.add(f)

      src ~> flo

      SourceShape(flo.out)
    })
  }

  object TransformGraph {
    private val t1 = Flow[String].map {
      case "Fizz" => "_Fizz"
      case i      => i
    }
    private val t2 = Flow[String].map {
      case "Buzz" => "Buzz!"
      case i      => i
    }
    private val t3 = Flow[String].map {
      case i @ "FizzBuzz" => i.toUpperCase
      case i              => i
    }

    val g = Flow.fromGraph(GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      val trf1 = builder.add(t1)
      val trf2 = builder.add(t2)
      val trf3 = builder.add(t3)

      trf1 ~> trf2 ~> trf3

      FlowShape(trf1.in, trf3.out)
    })
  }

  val graph = GraphDSL.create() { implicit builder =>
    val sink = Sink.foreach[String](println)

    import GraphDSL.Implicits._
    SourceGraph.g ~> TransformGraph.g ~> sink

    ClosedShape
  }

  RunnableGraph.fromGraph(graph)
    .run()

  sys.scheduler.scheduleOnce(1.second) { sys.terminate() }

}
