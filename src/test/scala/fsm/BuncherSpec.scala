package fsm

import akka.actor.{ActorSystem, Props}
import akka.actor.FSM.{CurrentState, SubscribeTransitionCallBack}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import fsm.Buncher._
import javax.print.attribute.standard.Destination
import org.scalatest.{FunSpecLike, MustMatchers}

import scala.collection.immutable

class BuncherSpec extends TestKit(ActorSystem("buncher-system")) with MustMatchers with FunSpecLike with ImplicitSender {

  describe("The Buncher") {


    it("should stay at Transacting when the Deposit is less then the price of the coffee") {
      val buncher = TestActorRef(Props(new Buncher()), "buncher")

      buncher ! SetTarget(testActor)
      //buncher ! SubscribeTransitionCallBack(testActor)
      //expectMsg(CurrentState(buncher, Idle))
      //expectMsg(CurrentState(buncher, Idle))
      buncher ! Queue("Hello")
      buncher ! Queue("Bye")
      buncher ! Flush
      // expectMsg(CurrentState(buncher, Active))
      expectMsg(Batch(immutable.Seq("Hello", "Bye")))


//      coffeeMachine ! SetCostOfCoffee(5)
//      coffeeMachine ! SetNumberOfCoffee(10)
//      coffeeMachine ! SubscribeTransitionCallBack(testActor)
//
//      expectMsg(CurrentState(coffeeMachine, Open))
//
//      coffeeMachine ! Deposit(2)
//
//      coffeeMachine ! GetNumberOfCoffee
//
//      expectMsg(10)
    }

  }
}
