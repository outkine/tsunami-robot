package scalaplayer

import battlecode.common._
import RobotPlayer.rc

object Miner {
  // @formatter:off
  sealed trait State
  final case class Init() extends State
  final case class SoupMiner(state: miner.SoupMiner.State) extends State
  final case class Builder(state: miner.Builder.State) extends State
  // @formatter:on

  var state: State = Init()
  var hqLoc: MapLocation = _

  def run(): Unit = Miner.state = Miner.state match {
    case Init() =>
      Miner.hqLoc = rc.senseNearbyRobots(1, rc.getTeam).find(_.getType == RobotType.HQ).head.getLocation
      if (hqLoc.directionTo(rc.getLocation) == Direction.SOUTH) Builder(miner.Builder.Init())
      else SoupMiner(miner.SoupMiner.Init())

    case Builder(state) => state match {
      case miner.Builder.Done() => SoupMiner(miner.SoupMiner.Init())
      case _ => Builder(miner.Builder.run(state))
    }
    case SoupMiner(state) => SoupMiner(miner.SoupMiner.run(state, Miner.hqLoc))
  }
}
