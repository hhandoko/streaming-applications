/**
 * File     : ForkInTheFlow.scala
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
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Partition, RunnableGraph}
import akka.stream.{ActorMaterializer, ClosedShape, FlowShape}

import com.hhandoko.geekcampid.partial.{SinkGraph, SourceGraph, TransformGraph}

object ForkInTheFlow extends App {

  implicit val sys = ActorSystem("combine")
  implicit val mat = ActorMaterializer()

  object WoofGraph {
    private def p(i: String) = i match {
      case i if i.forall(_.isDigit) => 0
      case _                        => 1
    }
    private val t = Flow[String].map {
      case i if i.toInt % 7 == 0 => "Woof"
      case i                     => i
    }

    val g = Flow.fromGraph(GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      val trf = builder.add(t)

      val par  = builder.add(Partition[String](2, p))
      val con  = builder.add(Merge[String](2))

      par.out(0) ~> trf ~> con.in(0)
      par.out(1)        ~> con.in(1)

      FlowShape(par.in, con.out)
    })
  }

  val graph = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._
    SourceGraph.g ~> TransformGraph.g ~> WoofGraph.g ~> SinkGraph.g

    ClosedShape
  }

  RunnableGraph.fromGraph(graph)
    .run()

  sys.scheduler.scheduleOnce(1.second) { sys.terminate() }

}
