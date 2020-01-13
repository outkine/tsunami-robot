package scalaplayer

import battlecode.common._

// @formatter:off
sealed trait MovingStage
final case class MiningStage() extends MovingStage
final case class RefiningStage() extends MovingStage

sealed trait State
final case class Init() extends State
final case class Moving(path: List[Direction], stage: MovingStage) extends State
final case class Wandering() extends State
final case class Mining(dir: Direction) extends State
final case class Refining(dir: Direction) extends State
// @formatter:on

object Miner {
  var state: State = Init()
  var hqLoc: MapLocation = _

  private def prepareMining(rc: RobotController): State = {
    Actions.findSoup(rc.getLocation, rc.getCurrentSensorRadiusSquared) match {
      case Some(loc) =>
        Moving(Actions.findPath(rc.getLocation, loc), MiningStage())
      case None => Wandering()
    }
  }

  def run(rc: RobotController, turnCount: Int): Unit = Miner.state = Miner.state match {
    case Init() =>
      // TODO: do not depend on spawning location to locate HQ
      Miner.hqLoc = rc.getLocation.add(Direction.SOUTH)
      prepareMining(rc)

    case Moving(path, stage) =>
      // Mining/depositing requires a direction, so we switch right before arriving at the target
      path match {
        case dir :: rest if rest.nonEmpty =>
          if (Actions.tryMove(dir)) Moving(rest, stage)
          else Moving(path, stage)
        case dir :: _ =>
          stage match {
            case mining: MiningStage => Mining(dir)
            case depositing: RefiningStage => Refining(dir)
          }
      }

    case Mining(dir) =>
      if (rc.getSoupCarrying < RobotType.MINER.soupLimit) {
        if (rc.senseSoup(rc.getLocation.add(dir)) > 0) {
          Actions.tryMine(dir)
          Mining(dir)
        } else prepareMining(rc)
      } else Moving(Actions.findPath(rc.getLocation, Miner.hqLoc), RefiningStage())
    case Refining(dir) =>
      if (rc.getSoupCarrying > 0) {
        Actions.tryRefine(dir)
        Refining(dir)
      } else prepareMining(rc)

    case Wandering() => Wandering()
  }
}
