/**
 * File     : TransformGraph.scala
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

import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL}

/**
 * String transformation for FizzBuzz inputs.
 */
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
