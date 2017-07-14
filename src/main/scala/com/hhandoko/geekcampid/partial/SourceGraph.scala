/**
 * File     : SourceGraph.scala
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

import akka.stream.SourceShape
import akka.stream.scaladsl.{Flow, GraphDSL, Source}

/**
 * FizzBuzz source iterator graph.
 */
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
