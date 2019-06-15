package particles

import java.util.UUID

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{FunSpecLike, MustMatchers}
import Config.{Model, ModelSet, SetModel}

import scala.concurrent.duration._
import scala.util.Random

class ConfigSpec extends TestKit(ActorSystem("particles-system")) with MustMatchers with FunSpecLike with ImplicitSender {
  describe("Config") {
    it("should create initial configuration") {
      val configuration = TestActorRef(Props(new Config()), "config")
      configuration ! PoisonPill
    }
    it("should increase version affter transformation") {
      val leftBorder = StaticParticle('[', 0)
      val rightBorder = StaticParticle(']', 20)
      val activeParticle = FloatingParticle('*', 10)
      val model = Model(0, Set(leftBorder, activeParticle, rightBorder))

      val configuration = TestActorRef(Props(new Config()), "config")
      configuration ! SetModel(model)
      expectMsg(ModelSet(model))

      configuration ! Config.Transform
      expectMsg(Config.Transformed(Model(1, Set(leftBorder, activeParticle, rightBorder))))
      configuration ! PoisonPill
    }
    it("should change particle location on transform") {
      val leftBorder = StaticParticle('[', 0)
      val rightBorder = StaticParticle(']', 4)
      val activeParticle = FloatingParticle('*', 2)
      val model = Model(0, Set(leftBorder, activeParticle, rightBorder))
      val configuration = TestActorRef(Props(new Config()), "config")
      configuration ! SetModel(model)
      expectMsg(ModelSet(model))
      println(s"Model: ${model.display}")
      for(i <- 1 to 100 ) {
        configuration ! Config.Transform
        val updatedModel = receiveOne(1 second).asInstanceOf[Config.Transformed].model
        println(s"Model: ${updatedModel.display}")
      }
      configuration ! PoisonPill
    }

    it("should prevent particles from overlapping") {
      val leftBorder = StaticParticle('[', 0)
      val rightBorder = StaticParticle(']', 2)
      val activeParticle = FloatingParticle('*', 1)
      val model = Model(0, Set(leftBorder, activeParticle, rightBorder))
      val configuration = TestActorRef(Props(new Config()), "config")
      configuration ! SetModel(model)
      expectMsg(ModelSet(model))
      println(s"Model: ${model.display}")
      for(i <- 1 to 100 ) {
        configuration ! Config.Transform
        val updatedModel = receiveOne(1 second).asInstanceOf[Config.Transformed].model
        updatedModel.particles.map(p => p.pos).size mustBe updatedModel.particles.size
        println(s"Model: ${updatedModel.display}")
      }
      configuration ! PoisonPill

    }
  }
}

