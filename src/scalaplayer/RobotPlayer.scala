package scalaplayer

import battlecode.common._

object RobotPlayer {
  def run(rc: RobotController): Unit = {
    print(rc.getType + " created")

    // DO NOT FORGET THIS
    Actions.init(rc)

    var turnCount = 0
    while (true) {
      try {
        import battlecode.common.RobotType._
        rc.getType match {
          case HQ => Hq.run(rc, turnCount)
          case MINER => Miner.run(rc)
//          case REFINERY => runRefinery()
//          case VAPORATOR => runVaporator()
          case DESIGN_SCHOOL => Factory.run(rc, Factory.DS())
          case FULFILLMENT_CENTER => Factory.run(rc, Factory.FC())
          case LANDSCAPER => Landscaper.run(rc)
          case DELIVERY_DRONE => Landscaper.run(rc)
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
