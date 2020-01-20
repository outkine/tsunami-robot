package scalaplayer

import battlecode.common._
import battlecode.common.RobotType._
import battlecode.common.Direction._

object Constants {
  val buildings: List[RobotType] = List(HQ, REFINERY, DESIGN_SCHOOL, FULFILLMENT_CENTER, NET_GUN, VAPORATOR)
  val movableDirections: List[Direction] = List(NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST)
  val cardinalDirections: List[Direction] = List(NORTH, EAST, SOUTH, WEST)
}
