package scalaplayer

import battlecode.common._
import battlecode.common.RobotType._

object Hq {
  var buildOrder = List((MINER, Direction.NORTH), (MINER, Direction.SOUTH))

  def run(): Unit = {
    Hq.buildOrder = Hq.buildOrder match {
      case (robot, dir) :: rest =>
        if (Actions.tryBuild(robot, dir)) rest
        else Hq.buildOrder
      case rest => rest
    }
    // HQ is also a NetGun
    NetGun.run()
  }
}
