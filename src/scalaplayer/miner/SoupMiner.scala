package scalaplayer.miner

import battlecode.common._
import scalaplayer.Actions

object SoupMiner {
  // @formatter:off
  sealed trait Stage
  final case class MiningStage() extends Stage
  final case class RefiningStage() extends Stage

  sealed trait State
  final case class Init() extends State
  final case class Moving(stage: Stage, prevOrigin: MapLocation, target: MapLocation) extends State
  final case class Working(stage: Stage, dir: Direction) extends State
  final case class Wandering() extends State
  // @formatter:on

  private def prepareMining(rc: RobotController): State = {
    Actions.findSoup() match {
      case Some(soup) => Moving(MiningStage(), rc.getLocation, soup)
      case None => Wandering()
    }
  }

  def run(rc: RobotController, state: State, hqLoc: MapLocation): State = state match {
    case Init() => prepareMining(rc)

    case Moving(stage, prevOrigin, target) =>
      val dir = Actions.findPath(prevOrigin, rc.getLocation, target)
      if (rc.getLocation.add(dir) == target) {
        Working(stage, dir)
      } else {
        Actions.tryMove(dir)
        Moving(stage, rc.getLocation, target)
      }

    case Working(stage, dir) => stage match {
      case MiningStage() =>
        if (rc.getSoupCarrying < RobotType.MINER.soupLimit) {
          if (rc.senseSoup(rc.getLocation.add(dir)) > 0) {
            Actions.tryMine(dir)
            Working(MiningStage(), dir)
          } else prepareMining(rc)
        } else Moving(RefiningStage(), rc.getLocation, hqLoc)
      case RefiningStage() =>
        if (rc.getSoupCarrying > 0) {
          Actions.tryRefine(dir)
          Working(RefiningStage(), dir)
        } else prepareMining(rc)
    }

    case Wandering() => Wandering()
  }
}
