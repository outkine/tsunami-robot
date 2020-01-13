package scalaplayer

import battlecode.common._

object RobotPlayer {
  def run(rc: RobotController): Unit = {
    print(rc.getType + " created")

    // DO NOT FORGET THIS
    Actions.init(rc)

    var turnCount = 0
    while (true) {
      turnCount += 1

      try {
        import battlecode.common.RobotType._
        rc.getType match {
          case HQ => Hq.run(rc, turnCount)
          case MINER => Miner.run(rc, turnCount)
//          case REFINERY => runRefinery()
//          case VAPORATOR => runVaporator()
//          case DESIGN_SCHOOL => runDesignSchool()
//          case FULFILLMENT_CENTER => runFulfillmentCenter()
//          case LANDSCAPER => runLandscaper()
//          case DELIVERY_DRONE => runDeliveryDrone()
//          case NET_GUN => runNetGun()
        }
        Clock.`yield`()
      } catch {
        case e: Exception =>
          System.out.println(rc.getType + " Exception")
          e.printStackTrace()
      }
    }
  }
}
