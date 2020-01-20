package scalaplayer

import battlecode.common._

object Drone {
  // @formatter:off
  sealed trait State
  final case class Init() extends State
  final case class Moving(prevOrigin: MapLocation, target: MapLocation, pathState: PathFinding.State) extends State
  // @formatter:on

  var state: State = Init()

  //noinspection DuplicatedCode
  def run(rc: RobotController): Unit = Drone.state = Drone.state match {
    case Init() =>
      val landscaperId = rc.senseRobotAtLocation(rc.getLocation.add(Direction.EAST)).ID
      if (Actions.tryPickup(landscaperId))
        Moving(rc.getLocation, new MapLocation(rc.getLocation.x, rc.getMapHeight - rc.getLocation.y), PathFinding.emptyState)
      else Init()

    case Moving(prevOrigin, target, pathState) =>
      val origin = rc.getLocation
      val (dir, newPathState) = PathFinding.run(prevOrigin, origin, target, pathState)
      Actions.tryMove(dir)
      Moving(origin, target, newPathState)
  }
}
