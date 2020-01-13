package scalaplayer

import battlecode.common._

object Hq {
  var hasBuilt = false

  def run(rc: RobotController, turnCount: Int): Unit = {
    if (!hasBuilt && Actions.tryBuild(RobotType.MINER, Direction.NORTH)) Hq.hasBuilt = true
  }
}
