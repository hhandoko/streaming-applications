/**
 * File     : SinkGraph.scala
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
package com.hhandoko.geekcampid.partial

import akka.stream.SinkShape
import akka.stream.scaladsl.{Flow, GraphDSL, Sink}

/**
 * Colorised println sink.
 */
object SinkGraph {
  private val f = Flow[String].map {
    case i @ "_Fizz"               => (Console.BLUE, i)
    case i @ "Buzz!"               => (Console.YELLOW, i)
    case i @ "FIZZBUZZ"            => (Console.RED, i)
    case i if i.forall(_.isLetter) => (Console.GREEN, i)
    case i                         => (Console.WHITE, i)
  }
  private val s = Sink.foreach[(String, String)] {
    case (c, i) => println(c + i)
  }

  val g = Sink.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._
    val flo = builder.add(f)
    val sin = builder.add(s)

    flo ~> sin

    SinkShape(flo.in)
  })
}
