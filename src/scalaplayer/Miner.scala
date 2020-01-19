package scalaplayer

import battlecode.common._

// @formatter:off
sealed trait Stage
final case class MiningStage() extends Stage
final case class RefiningStage() extends Stage

sealed trait State
final case class Init() extends State
final case class Moving(path: List[MapLocation], stage: Stage) extends State
final case class Working(dir: Direction, stage: Stage) extends State
final case class Wandering() extends State
// @formatter:on

object Miner {
  var state: State = Init()
  var hqLoc: MapLocation = _

  private def prepareMining(rc: RobotController): State = {
    Actions.findSoup(rc.getLocation, rc.getCurrentSensorRadiusSquared) match {
      case Some(soup) => Moving(Actions.findPath(rc.getLocation, soup).head, MiningStage())
      case None => Wandering()
    }
  }

  def run(rc: RobotController, turnCount: Int): Unit = Miner.state = Miner.state match {
    case Init() =>
      Miner.hqLoc = rc.senseNearbyRobots(1, rc.getTeam()).find(_.getType == RobotType.HQ).head.getLocation
      prepareMining(rc)

    case Moving(path, stage) =>
      // Mining/depositing requires a direction, so we switch right before arriving at the target
      path match {
        case loc :: rest if rest.nonEmpty =>
          if (Actions.tryMove(rc.getLocation.directionTo(loc))) Moving(rest, stage)
          else Moving(path, stage)
        case loc :: _ => Working(rc.getLocation.directionTo(loc), stage)
      }

    case Working(dir, stage) => stage match {
      case MiningStage() =>
        if (rc.getSoupCarrying < RobotType.MINER.soupLimit) {
          if (rc.senseSoup(rc.getLocation.add(dir)) > 0) {
            Actions.tryMine(dir)
            Working(dir, MiningStage())
          } else prepareMining(rc)
        } else Moving(Actions.findPath(rc.getLocation, Miner.hqLoc).head, RefiningStage())
      case RefiningStage() =>
        if (rc.getSoupCarrying > 0) {
          Actions.tryRefine(dir)
          Working(dir, RefiningStage())
        } else prepareMining(rc)
    }

    case Wandering() => Wandering()
  }
}
