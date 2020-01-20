package scalaplayer

import battlecode.common._

object Factory {
  // @formatter:off
  sealed trait Type
  final case class DS() extends Type
  final case class FC() extends Type
  // @formatter:on

  var hasBuilt: Boolean = false

  def run(`type`: Type): Unit = {
    val robot = `type` match {
      case DS() => RobotType.LANDSCAPER
      case FC() => RobotType.DELIVERY_DRONE
    }
    if (!Factory.hasBuilt && Actions.tryBuild(robot, Direction.SOUTH)) Factory.hasBuilt = true
  }
}
