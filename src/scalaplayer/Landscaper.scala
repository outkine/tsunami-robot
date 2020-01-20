package scalaplayer

import battlecode.common._

object Landscaper {
  // @formatter:off
  sealed trait State
  final case class Init() extends State
  // @formatter:on

  var state: State = Init()

  def run(): Unit = Landscaper.state = Landscaper.state match {
    case Init() => Init()
  }
}
