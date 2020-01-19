package scalaplayer

import battlecode.common._

object Landscaper {
  // @formatter:off
  sealed trait State
  final case class Init() extends State
  final case class Working(dir: Direction) extends State
  final case class Wandering() extends State
  // @formatter:on

  var state: State = Init()

  def run(rc: RobotController): Unit = Landscaper.state = Landscaper.state match {
    case Init() => Init()
  }
}
