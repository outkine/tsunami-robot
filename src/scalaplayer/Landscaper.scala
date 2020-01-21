package scalaplayer

import battlecode.common._
import RobotPlayer.rc

object Landscaper {
  // @formatter:off
  sealed trait State
  final case class Init(prevLoc: MapLocation) extends State
  final case class Moving(prevOrigin: MapLocation, target: MapLocation, pathState: PathFinding.State) extends State
  final case class Attacking(dir: Direction) extends State
  final case class Idle() extends State
  // @formatter:on

  var state: State = Init(rc.getLocation)

  //noinspection DuplicatedCode
  def run(): Unit = Landscaper.state = Landscaper.state match {
    case Init(prevLoc) =>
      val loc = rc.getLocation
      // still waiting
      if (loc == prevLoc) {
        Actions.tryDig(Direction.SOUTH)
        Init(prevLoc)
      } else {
        val hqOption = rc.senseNearbyRobots().find(_.`type` == RobotType.HQ)
        hqOption match {
          case Some(hq) =>
            val dir = loc.directionTo(hq.location)
            if (loc.add(dir) == hq.location) Attacking(dir)
            else Moving(loc, hq.location, PathFinding.emptyState)

          case None => Idle()
        }
      }

    case Moving(prevOrigin, target, pathState) =>
      val origin = rc.getLocation
      val (dir, newPathState) = PathFinding.run(prevOrigin, origin, target, pathState)
      if (origin.add(dir) == target) {
        Attacking(dir)
      } else {
        if (Actions.tryMove(dir)) Moving(origin, target, newPathState)
        else Moving(prevOrigin, target, pathState)
      }

    case Attacking(dir) =>
      if (rc.getDirtCarrying == 0) {
        Actions.tryDig(dir.opposite)
      } else {
        Actions.tryDepositDirt(dir)
      }
      Attacking(dir)

    // TODO: implement this behavior
    case Idle() => Idle()
  }
}
