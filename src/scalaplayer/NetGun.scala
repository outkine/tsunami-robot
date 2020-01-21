package scalaplayer

import battlecode.common._
import RobotPlayer.rc

object NetGun {
  def run(): Unit = {
    val enemies = rc.senseNearbyRobots().to[List].filter(_.team != rc.getTeam)
    enemies.foreach(enemy => Actions.tryShoot(enemy.ID))
  }
}
