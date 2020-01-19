package scalaplayer

import battlecode.common._
import battlecode.common.RobotType._

object Hq {
  var buildOrder = List(MINER, MINER)
  def run(rc: RobotController, turnCount: Int): Unit = Hq.buildOrder = Hq.buildOrder match {
    case robot :: rest =>
      if (Actions.tryBuild(robot, Direction.NORTH)) {
        rest
      } else robot :: rest
    case empty =>
      empty
  }
}
