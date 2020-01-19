package scalaplayer

import battlecode.common._
import battlecode.common.RobotType._

object Constants {
  val buildings: List[RobotType] = List(HQ, REFINERY, DESIGN_SCHOOL, FULFILLMENT_CENTER, NET_GUN, VAPORATOR)
  val movableDirections: List[Direction] = Direction.values.dropRight(1).to[List]
}
