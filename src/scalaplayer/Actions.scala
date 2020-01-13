package scalaplayer

import battlecode.common._

object Actions {
  private var rc: RobotController = _

  def init(rc: RobotController): Unit = {
    Actions.rc = rc
  }

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

  def tryBlockchain(message: Array[Int], cost: Int): Boolean = if (rc.canSubmitTransaction(message, cost)) {
    rc.submitTransaction(message, 10)
    true
  } else false

  private def generateTileGrid(origin: MapLocation, size: Int): IndexedSeq[MapLocation] = {
    for (x <- 0 until size;
         y <- 0 until size) yield new MapLocation(x, y)
  }

  private def generateSenseTiles(origin: MapLocation, senseRadius: Int): IndexedSeq[MapLocation] = {
    generateTileGrid(origin, senseRadius * 2).filter(rc.canSenseLocation)
  }

  def findSoup(origin: MapLocation, senseRadius: Int): Option[MapLocation] = {
    val soups = generateSenseTiles(origin, senseRadius).filter(rc.senseSoup(_) > 0)
    soups.headOption
  }

  def findPath(origin: MapLocation, destination: MapLocation): List[Direction] = {
    if (origin.equals(destination)) List()
    else {
      val dir = origin.directionTo(destination)
      if (rc.canMove(dir)) dir :: findPath(origin.add(dir), destination)
      else List()
    }
  }

}
