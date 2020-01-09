package scalaplayer

import battlecode.common._

object Miner {
  def run(rc: RobotController): Unit = {
    Actions.tryMove(Direction.NORTH)
  }
}
