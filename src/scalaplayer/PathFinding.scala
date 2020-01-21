package scalaplayer

import battlecode.common._
import RobotPlayer.rc

object PathFinding {
  type State = Option[MapLocation]
  val emptyState: State = None

  def run(prevOrigin: MapLocation
          , origin: MapLocation
          , target: MapLocation
          , prevWall: Option[MapLocation]
          , isDrone: Boolean = false): (Direction, State) = {
    if (origin == target) (Direction.CENTER, None)
    else {
      val masterDir = origin.directionTo(target)
      val masterDest = origin.add(masterDir)

      // move onto target even if it is a wall
      if (masterDest == target || canMove(origin, masterDest, isDrone)) (masterDir, None)
      else {
        prevWall match {
          case Some(wall) =>
            val (dir: Direction, rotateClockwise: Boolean) =
              if (origin.x == wall.x) {
                val dir = directionFromOffset(origin.x.compare(prevOrigin.x), wall.y.compareTo(origin.y))
                (dir, dir.dx.compare(0) == dir.dy.compare(0))
              } else {
                val dir = directionFromOffset(wall.x.compareTo(origin.x), origin.y.compare(prevOrigin.y))
                (dir, dir.dx.compare(0) != dir.dy.compare(0))
              }

            followWall(prevOrigin, origin, dir, rotateClockwise, isDrone)

          case None =>
            val twoOptions = Seq(true, false).map(followWall(prevOrigin, origin, masterDir, _, isDrone))
            // return the shorter of the two paths
            twoOptions.minBy(option => origin.add(option._1).distanceSquaredTo(target))
        }
      }
    }
  }

  private def followWall(prevOrigin: MapLocation
                         , origin: MapLocation
                         , startingDir: Direction
                         , rotateClockwise: Boolean
                         , isDrone: Boolean): (Direction, State) = {
    var dir = startingDir
    while (!canMove(origin, origin.add(dir), isDrone)) {
      dir = if (rotateClockwise) dir.rotateRight else dir.rotateLeft
    }
    (dir, Some(origin.add(if (rotateClockwise) dir.rotateLeft else dir.rotateRight)))
  }

  def canMove(origin: MapLocation, target: MapLocation, isDrone: Boolean): Boolean = {
    rc.onTheMap(target) &&
      ((isDrone || Math.abs(rc.senseElevation(origin) - rc.senseElevation(target)) <= 3)
        && (isDrone || !rc.senseFlooding(target))
        && (rc.senseRobotAtLocation(target) == null || !Constants.buildings.contains(rc.senseRobotAtLocation(target).`type`)))

  }

  private def directionFromOffset(dx: Int, dy: Int): Direction = {
    (dx, dy) match {
      case (0, 1) => Direction.NORTH
      case (1, 1) => Direction.NORTHEAST
      case (1, 0) => Direction.EAST
      case (1, -1) => Direction.SOUTHEAST
      case (0, -1) => Direction.SOUTH
      case (-1, -1) => Direction.SOUTHWEST
      case (-1, 0) => Direction.WEST
      case (-1, 1) => Direction.NORTHWEST
      case (0, 0) => Direction.CENTER
    }
  }
}
