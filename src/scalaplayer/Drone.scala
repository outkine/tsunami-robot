package scalaplayer

import battlecode.common._
import RobotPlayer.rc

object Drone {
  // @formatter:off
  sealed trait State
  final case class Init() extends State
  final case class Moving(prevOrigin: MapLocation, targets: List[MapLocation], pathState: PathFinding.State) extends State
  final case class Idle() extends State
  // @formatter:on

  var state: State = Init()

  //noinspection DuplicatedCode
  def run(): Unit = Drone.state = Drone.state match {
    case Init() =>
      val hqLoc = rc.getLocation.add(Direction.NORTH).add(Direction.NORTH).add(Direction.NORTHEAST)
      val clockwisePath = List(
        new MapLocation(hqLoc.x, rc.getMapHeight - hqLoc.y - 1),
        new MapLocation(rc.getMapWidth - hqLoc.x - 1, rc.getMapHeight - hqLoc.y - 1),
        new MapLocation(rc.getMapWidth - hqLoc.x - 1, hqLoc.y)
      )

      // see Factory, building on even/odd rounds is a way of communicating the path type
      val pathType = rc.getRoundNum % 2
      val targets = if (pathType == 0) clockwisePath else clockwisePath.reverse

      val landscaperId = rc.senseRobotAtLocation(rc.getLocation.add(Direction.EAST)).ID
      if (Actions.tryPickup(landscaperId))
        Moving(rc.getLocation, targets, PathFinding.emptyState)
      else Init()

    case Moving(prevOrigin, targets, pathState) =>
      val origin = rc.getLocation
      val target = targets.head

      val NoHQ = rc.canSenseLocation(target) && Actions.senseRobotAtLocation(target).forall(_.`type` != RobotType.HQ)
      if (NoHQ) {
        Moving(origin, targets.tail, PathFinding.emptyState)
      } else {
        val (dir, newPathState) = PathFinding.run(prevOrigin, origin, target, pathState)
        val (dir2, _) = PathFinding.run(origin, origin.add(dir), target, newPathState)

        if (origin.add(dir).add(dir2) == target) {
          Actions.tryDrop(dir)
          Idle()
        } else {
          if (Actions.tryMove(dir)) Moving(origin, targets, newPathState)
          else Moving(prevOrigin, targets, pathState)
        }
      }

    case Idle() => Idle()
  }
}
