package scalaplayer

import battlecode.common._

sealed trait State

final case class Init() extends State

final case class Moving(path: List[Direction]) extends State

final case class Wandering() extends State

final case class Mining(dir: Direction) extends State

object Miner {
  var state: State = Init()

  def run(rc: RobotController, turnCount: Int): Unit = Miner.state = Miner.state match {
    case Init() =>
      Actions.findSoup(rc.getLocation, rc.getCurrentSensorRadiusSquared) match {
        case Some(loc) =>
          Moving(Actions.findPath(rc.getLocation, loc))
        case None => Wandering()
      }
    case Moving(path) =>
      // Mining requires a direction, so we switch to mining right before arriving at the target soup
      path match {
        case dir :: rest if rest.nonEmpty =>
          if (Actions.tryMove(dir)) Moving(rest)
          else Moving(path)
        case dir :: _ =>
          Mining(dir)
      }
    case Mining(dir) =>
      Actions.tryMine(dir)
      Mining(dir)
    case Wandering() => Wandering()
  }
}
