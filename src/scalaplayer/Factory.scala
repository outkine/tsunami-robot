package scalaplayer

import battlecode.common._

object Factory {
  // @formatter:off
  sealed trait Type
  final case class DS() extends Type
  final case class FC() extends Type
  // @formatter:on

  var hasBuilt: Boolean = false

  def run(rc: RobotController, `type`: Type): Unit = {
    val robot = `type` match {
      case DS() => RobotType.LANDSCAPER
      case FC() => RobotType.DELIVERY_DRONE
    }
    if (Actions.tryBuild(robot, Direction.SOUTH) && !hasBuilt) hasBuilt = true
  }
}
