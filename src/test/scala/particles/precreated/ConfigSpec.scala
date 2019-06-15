package particles.precreated

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{FunSpecLike, MustMatchers}
import particles.precreated.Config._

import scala.collection.immutable
import scala.concurrent.duration._

class ConfigSpec extends TestKit(ActorSystem("particles-system")) with MustMatchers with FunSpecLike with ImplicitSender {

  describe("Config") {

    it("should return Model with incremeted version on Tranform") {
      val configuration = TestActorRef(Props(new Config()), "config")
      val leftParticle = Particle.Model(0L, "|", 0)
      val rightParticle = Particle.Model(0L, "|", 10)
      val activeParticle = Particle.Model(0L, "*", 5)

      val model = Model(0L, immutable.Seq(leftParticle, rightParticle, activeParticle))
      configuration ! SetModel(model)

      assert(receiveOne(1 second).asInstanceOf[ModelSet].model.particles.toSet == Set(leftParticle, rightParticle, activeParticle))

      configuration ! Transform
      println(receiveOne(1 second).asInstanceOf[Transformed].model.display())
      configuration ! PoisonPill
    }
  }
}
