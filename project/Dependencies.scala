/**
 * File     : Dependencies.scala
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
import sbt._

/**
 * Centralised dependencies declaration, to reduce duplication and version conflicts between the different submodules.
 */
object Dependencies {

  // ----------------------------------------------------------------------------------------------------
  // Akka
  // ----------------------------------------------------------------------------------------------------
  private val akkaStreams = "com.typesafe.akka" %% "akka-stream" % "2.5.3"

  // ----------------------------------------------------------------------------------------------------
  // Testing
  // ----------------------------------------------------------------------------------------------------
  private val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"

  lazy val dependencies = Seq(
    akkaStreams,
    scalaTest % Test
  )

}
