package scalaplayer

import battlecode.common._

object Actions {
  private var rc: RobotController = _

  def init(rc: RobotController): Unit = {
    Actions.rc = rc
  }

  @throws[GameActionException]
  def tryMove(dir: Direction): Boolean = if (rc.isReady && rc.canMove(dir)) {
    rc.move(dir)
    true
  } else false

  @throws[GameActionException]
  def tryBuild(`type`: RobotType, dir: Direction): Boolean = if (rc.isReady && rc.canBuildRobot(`type`, dir)) {
    rc.buildRobot(`type`, dir)
    true
  } else false

  @throws[GameActionException]
  def tryMine(dir: Direction): Boolean = if (rc.isReady && rc.canMineSoup(dir)) {
    rc.mineSoup(dir)
    true
  } else false

  @throws[GameActionException]
  def tryRefine(dir: Direction): Boolean = if (rc.isReady && rc.canDepositSoup(dir)) {
    rc.depositSoup(dir, rc.getSoupCarrying)
    true
  } else false

  @throws[GameActionException]
  def tryBlockchain(message: Array[Int], cost: Int): Boolean = if (rc.canSubmitTransaction(message, cost)) {
    rc.submitTransaction(message, 10)
    true
  } else false
}
