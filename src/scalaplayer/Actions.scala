package scalaplayer

import battlecode.common._
import RobotPlayer.rc

object Actions {
  def tryMove(dir: Direction): Boolean = if (rc.isReady && rc.canMove(dir)) {
    rc.move(dir)
    true
  } else false

  def tryBuild(`type`: RobotType, dir: Direction): Boolean = if (rc.isReady && rc.canBuildRobot(`type`, dir)) {
    rc.buildRobot(`type`, dir)
    true
  } else false

  def tryMine(dir: Direction): Boolean = if (rc.isReady && rc.canMineSoup(dir)) {
    rc.mineSoup(dir)
    true
  } else false

  def tryRefine(dir: Direction): Boolean = if (rc.isReady && rc.canDepositSoup(dir)) {
    rc.depositSoup(dir, rc.getSoupCarrying)
    true
  } else false

  def tryPickup(id: Int): Boolean = if (rc.isReady && rc.canPickUpUnit(id)) {
    rc.pickUpUnit(id)
    true
  } else false

  def tryDrop(dir: Direction): Boolean = if (rc.isReady && rc.canDropUnit(dir)) {
    rc.dropUnit(dir)
    true
  } else false

  def tryBlockchain(message: Array[Int], cost: Int): Boolean = if (rc.canSubmitTransaction(message, cost)) {
    rc.submitTransaction(message, 10)
    true
  } else false

  def findSoup(): Option[MapLocation] = {
    // Arrays and Sets break Battlecode
    rc.senseNearbySoup().to[List].sortBy(rc.getLocation.distanceSquaredTo).headOption
  }

  def senseRobotAtLocation(loc: MapLocation): Option[RobotInfo] = {
    Option(rc.senseRobotAtLocation(loc))
  }
}
