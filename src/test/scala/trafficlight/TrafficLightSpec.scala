package trafficlight

import akka.actor.FSM.{CurrentState, SubscribeTransitionCallBack, Transition}
import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{FunSpecLike, MustMatchers}
import traficlight.TrafficLight
import traficlight.TrafficLight._

class TrafficLightSpec  extends TestKit(ActorSystem("traffic-light-system")) with MustMatchers with FunSpecLike with ImplicitSender {
  describe("The traffic light") {

    it("should start with in Red state") {
      val trafficLight = TestActorRef(Props(new TrafficLight()), "traffic-light")
      trafficLight ! GetState
      expectMsg(GetStateReply(Green))
      trafficLight ! PoisonPill
    }

    it("should go Green -> Yellow -> Red -> Yellow -> Green if StopRequested") {
      val trafficLight = TestActorRef(Props(new TrafficLight()), "traffic-light")

      trafficLight ! SubscribeTransitionCallBack(testActor)
      expectMsg(CurrentState(trafficLight, Green))
      trafficLight ! RequestStop
      expectMsg(Transition(trafficLight, Green, Yellow))
      expectMsg(Transition(trafficLight, Yellow, Red))
      expectMsg(Transition(trafficLight, Red, Yellow))
      expectMsg(Transition(trafficLight, Yellow, Green))

      trafficLight ! PoisonPill
    }

    it("should go to Red if StopRequested in Red state") {
      val trafficLight = TestActorRef(Props(new TrafficLight()), "traffic-light")

      trafficLight ! SubscribeTransitionCallBack(testActor)
      expectMsg(CurrentState(trafficLight, Green))
      trafficLight ! RequestStop
      expectMsg(Transition(trafficLight, Green, Yellow))
      expectMsg(Transition(trafficLight, Yellow, Red))
      trafficLight ! RequestStop
      expectMsg(Transition(trafficLight, Red, Yellow))
      expectMsg(Transition(trafficLight, Yellow, Green))

      trafficLight ! PoisonPill
    }

  }
}
