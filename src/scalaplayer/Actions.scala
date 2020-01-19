package scalaplayer

import battlecode.common._

object Actions {
  private var rc: RobotController = _

  def init(rc: RobotController): Unit = {
    Actions.rc = rc
  }

  def tryMove(dir: Direction): Boolean = if (rc.isReady && rc.canMove(dir)) {
    rc.move(dir)
    true
  } else false

  def tryBuild(`type`: RobotType, dir: Direction): Boolean = if (rc.isReady && rc.canBuildRobot(`type`, dir)) {
    rc.buildRobot(`type`, dir)
    true
  } else false

  def tryMine(dir: Direction): Boolean = if (rc.isReady && rc.canMineSoup(dir)) {
    rc.mineSoup(dir)
    true
  } else false

  def tryRefine(dir: Direction): Boolean = if (rc.isReady && rc.canDepositSoup(dir)) {
    rc.depositSoup(dir, rc.getSoupCarrying)
    true
  } else false

  def tryBlockchain(message: Array[Int], cost: Int): Boolean = if (rc.canSubmitTransaction(message, cost)) {
    rc.submitTransaction(message, 10)
    true
  } else false

  def findSoup(): Option[MapLocation] = {
    // Arrays and Sets break Battlecode
    rc.senseNearbySoup().to[List].sortBy(rc.getLocation.distanceSquaredTo).headOption
  }

  def findPath(prevOrigin: MapLocation, origin: MapLocation, target: MapLocation): Direction = {
    if (origin == target) Direction.CENTER
    else {
      val masterDir = origin.directionTo(target)
      val masterDest = origin.add(masterDir)

      if (Actions.canMove(origin, masterDest)) masterDir
      else {
        val commonWalls =
          adjacentTiles(prevOrigin)
            .filter(!Actions.canMove(prevOrigin, _))
            .intersect(adjacentTiles(origin))
        val closestWall = commonWalls.sortBy(origin.distanceSquaredTo).headOption

        closestWall match {
          case Some(wall) =>
            val (dir: Direction, rotateClockwise: Boolean) =
              if (origin.x == wall.x) {
                val dir = Actions.directionFromOffset(origin.x.compare(prevOrigin.x), wall.y.compareTo(origin.y))
                (dir, dir.dx.compare(0) == dir.dy.compare(0))
              } else {
                val dir = Actions.directionFromOffset(wall.x.compareTo(origin.x), origin.y.compare(prevOrigin.y))
                (dir, dir.dx.compare(0) != dir.dy.compare(0))
              }

            followWall(prevOrigin, origin, dir, rotateClockwise)

          case None =>
            val twoPaths = Seq(true, false).map { rotateClockwise =>
              followWall(prevOrigin, origin, masterDir, rotateClockwise)
            }
            // return the shorter of the two paths
            twoPaths.minBy(origin.add(_).distanceSquaredTo(target))
        }
      }
    }
  }

  private def followWall(prevOrigin: MapLocation, origin: MapLocation, startingDir: Direction, rotateClockwise: Boolean): Direction = {
    var dir = startingDir
    while (!Actions.canMove(origin, origin.add(dir))) {
      dir = if (rotateClockwise) dir.rotateRight else dir.rotateLeft
    }
    dir
  }

  def canMove(origin: MapLocation, target: MapLocation): Boolean = {
    ((rc.senseElevation(origin) - rc.senseElevation(target) <= 3)
      && (!rc.senseFlooding(target))
      && (rc.senseRobotAtLocation(target) == null || Constants.buildings.contains(rc.senseRobotAtLocation(target).`type`)))
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

  private def adjacentTiles(location: MapLocation): List[MapLocation] = {
    Constants.movableDirections.map(location.add)
  }
}
