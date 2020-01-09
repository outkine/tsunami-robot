package scalaplayer

import battlecode.common._

object Hq {
  def run(rc: RobotController): Unit = {
    Actions.tryBuild(RobotType.MINER, Direction.NORTH)
  }
}
