/**
 * File     : CanBeComposed.scala
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

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

object CanBeComposed extends App {

  implicit val sys = ActorSystem("compose")
  implicit val mat = ActorMaterializer()

  val rangeSource  = Source(1 to 1000)
  val fizzBuzzFlow = Flow[Int].map {
    case i if i % 15 == 0 => "FizzBuzz"
    case i if i % 5 == 0  => "Buzz"
    case i if i % 3 == 0  => "Fizz"
    case i                => i.toString
  }
  val printlnSink  = Sink.foreach[String](println)

  val nestedSource  = rangeSource.via(fizzBuzzFlow)
  val prefixFlow    = Flow[String].map {
    case "Fizz"    => "_Fizz"
    case i         => i
  }
  val suffixFlow    = Flow[String].map {
    case "Buzz"    => "Buzz!"
    case i         => i
  }
  val uppercaseFlow = Flow[String].map {
    case i @ "FizzBuzz" => i.toUpperCase
    case i              => i
  }
  val nestedFlow    = prefixFlow.via(suffixFlow).via(uppercaseFlow)
  val nestedSink    = nestedFlow.to(printlnSink)

  nestedSource
    .to(nestedSink)
    .run()

  sys.terminate()

}
