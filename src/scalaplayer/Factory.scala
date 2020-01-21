package scalaplayer

import battlecode.common._
import RobotPlayer.rc

object Factory {
  // @formatter:off
  sealed trait Type
  final case class DS() extends Type
  final case class FC() extends Type
  // @formatter:on

  var phase: Int = 0

  def run(`type`: Type): Unit = {
    val robotType = `type` match {
      case DS() => RobotType.LANDSCAPER
      case FC() => RobotType.DELIVERY_DRONE
    }

    // in the first phase (phase = 0), build robot only on even rounds
    // in the second phase (phase = 1), build robot only on odd rounds
    // this is a way of "communicating" with the created robot
    if (phase < 2 && rc.getRoundNum % 2 == phase && Actions.tryBuild(robotType, Direction.SOUTH)) phase += 1
  }
}
