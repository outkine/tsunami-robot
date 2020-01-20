package scalaplayer.miner

import battlecode.common._
import scalaplayer.Actions
import scalaplayer.RobotPlayer.rc

object Builder {
  // @formatter:off
  sealed trait State
  final case class Init() extends State
  final case class BuildingDS() extends State
  final case class BuildingFC() extends State
  final case class Done() extends State
  // @formatter:on

  def run(state: State): State = state match {
    case Init() => BuildingDS()
    case BuildingDS() =>
      if (Actions.tryBuild(RobotType.DESIGN_SCHOOL, Direction.SOUTH)) BuildingFC()
      else BuildingDS()
    case BuildingFC() =>
      if (Actions.tryBuild(RobotType.FULFILLMENT_CENTER, Direction.SOUTHWEST)) Done()
      else BuildingFC()
    case Done() => Done()
  }
}
