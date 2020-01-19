package scalaplayer

import battlecode.common._

object Constants {

  import RobotType._

  val buildings: Set[RobotType] = Set(HQ, REFINERY, DESIGN_SCHOOL, FULFILLMENT_CENTER, NET_GUN, VAPORATOR)
}

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

  def findSoup(origin: MapLocation, senseRadius: Int): Option[MapLocation] = {
    val soups = generateSenseTiles(origin, senseRadius)
      .filter(rc.senseSoup(_) > 0)
      .sortWith(origin.distanceSquaredTo(_) < origin.distanceSquaredTo(_))
    soups.headOption
  }

  private def generateSenseTiles(origin: MapLocation, senseRadius: Int): IndexedSeq[MapLocation] = {
    generateTileGrid(origin, senseRadius * 2).filter(rc.canSenseLocation)
  }

  private def generateTileGrid(origin: MapLocation, size: Int): IndexedSeq[MapLocation] = {
    for (x <- 0 until size;
         y <- 0 until size) yield new MapLocation(x, y)
  }

  def canMove(origin: MapLocation, target: MapLocation): Boolean = {
    ((rc.senseElevation(origin) - rc.senseElevation(target) <= 3)
      && (!rc.senseFlooding(target))
      && (rc.senseRobotAtLocation(target) == null || Constants.buildings(rc.senseRobotAtLocation(target).`type`)))
  }

  def findPath(origin: MapLocation, target: MapLocation): Option[List[MapLocation]] =
    findPath(origin, origin, target, 0)

  private def findPath(
                        prevOrigin: MapLocation,
                        origin: MapLocation,
                        target: MapLocation,
                        prevClosestDistance: Int,
                        prevWall: Option[MapLocation] = None
                      ): Option[List[MapLocation]] = {
    if (origin.equals(target)) Some(List())
    else {
      val closestDistance = {
        val distance = prevOrigin.distanceSquaredTo(origin)
        if (distance < prevClosestDistance) distance else prevClosestDistance
      }

      val masterDir = origin.directionTo(target)
      val masterDest = origin.add(masterDir)

      val path =
        if (Actions.canMove(origin, masterDest) && masterDest.distanceSquaredTo(target) < closestDistance)
          findPath(origin, masterDest, target, closestDistance)
        else {
          prevWall match {
            case Some(wall) =>
              val (dir: Direction, rotateClockwise: Boolean) =
                if (origin.x == wall.x) {
                  val dir = Actions.directionFromOffset(origin.x.compare(prevOrigin.x), wall.y.compareTo(origin.y))
                  (dir, dir.dx.compare(0) == dir.dy.compare(0))
                } else {
                  val dir = Actions.directionFromOffset(wall.x.compareTo(origin.x), origin.y.compare(prevOrigin.y))
                  (dir, dir.dx.compare(0) != dir.dy.compare(0))
                }

              followWall(prevOrigin, origin, dir, rotateClockwise, target, closestDistance)

            case None =>
              val twoPaths = Seq(true, false).flatMap { rotateClockwise =>
                followWall(prevOrigin, origin, masterDir, rotateClockwise, target, closestDistance)
              }
              // return the shorter of the two paths
              twoPaths.sortWith(_.length > _.length).headOption
          }
        }

      path.map(origin :: _)
    }
  }

  private def followWall(prevOrigin: MapLocation
                         , origin: MapLocation
                         , startingDir: Direction
                         , rotateClockwise: Boolean
                         , target: MapLocation
                         , closestDistance: Int) = {

    var dir = startingDir

    while (!Actions.canMove(origin, origin.add(dir)) && origin.add(dir) != prevOrigin) {
      dir = if (rotateClockwise) dir.rotateRight else dir.rotateLeft
    }

    if (origin.add(dir) == prevOrigin) None
    else findPath(origin, origin.add(dir), target, closestDistance,
      prevWall = Some(origin.add(if (rotateClockwise) dir.rotateLeft else dir.rotateRight))
    )
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
