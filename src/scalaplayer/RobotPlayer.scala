package scalaplayer

import battlecode.common._

object RobotPlayer {
  var rc: RobotController = _

  def run(rc: RobotController): Unit = {
    // DO NOT FORGET THIS
    RobotPlayer.rc = rc

    var turnCount = 0
    while (true) {
      try {
        import battlecode.common.RobotType._
        rc.getType match {
          case HQ => Hq.run(turnCount)
          case MINER => Miner.run()
//          case REFINERY => runRefinery()
//          case VAPORATOR => runVaporator()
          case DESIGN_SCHOOL => Factory.run(Factory.DS())
          case FULFILLMENT_CENTER => Factory.run(Factory.FC())
          case LANDSCAPER => Landscaper.run()
          case DELIVERY_DRONE => Drone.run()
//          case NET_GUN => runNetGun()
        }
        turnCount += 1
        Clock.`yield`()
      } catch {
        case e: Exception =>
          System.out.println(rc.getType + " Exception")
          e.printStackTrace()
      }
    }
  }
}
