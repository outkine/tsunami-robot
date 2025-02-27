package scalaplayer.miner

import battlecode.common._
import scalaplayer.{Actions, PathFinding}
import scalaplayer.RobotPlayer.rc

object SoupMiner {
  // @formatter:off
  sealed trait Stage
  final case class MiningStage() extends Stage
  final case class RefiningStage() extends Stage

  sealed trait State
  final case class Init() extends State
  final case class Moving(stage: Stage, prevOrigin: MapLocation, target: MapLocation, pathState: PathFinding.State) extends State
  final case class Working(stage: Stage, dir: Direction) extends State
  final case class Wandering() extends State
  // @formatter:on

  private def prepareMining(): State = {
    Actions.findSoup() match {
      case Some(soup) => Moving(MiningStage(), rc.getLocation, soup, PathFinding.emptyState)
      case None => Wandering()
    }
  }

  //noinspection DuplicatedCode
  def run(state: State, hqLoc: MapLocation): State = state match {
    case Init() => prepareMining()

    case Moving(stage, prevOrigin, target, pathState) =>
      val origin = rc.getLocation
      val (dir, newPathState) = PathFinding.run(prevOrigin, origin, target, pathState)
      if (origin.add(dir) == target) {
        Working(stage, dir)
      } else {
        if (Actions.tryMove(dir)) Moving(stage, origin, target, newPathState)
        else Moving(stage, prevOrigin, target, pathState)
      }

    case Working(stage, dir) => stage match {
      case MiningStage() =>
        if (rc.getSoupCarrying < RobotType.MINER.soupLimit) {
          if (rc.senseSoup(rc.getLocation.add(dir)) > 0) {
            Actions.tryMine(dir)
            Working(MiningStage(), dir)
          } else prepareMining()
        } else Moving(RefiningStage(), rc.getLocation, hqLoc, PathFinding.emptyState)
      case RefiningStage() =>
        if (rc.getSoupCarrying > 0) {
          Actions.tryRefine(dir)
          Working(RefiningStage(), dir)
        } else prepareMining()
    }

    case Wandering() => Wandering()
  }
}
