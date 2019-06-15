package particles

import java.util.UUID

import org.scalatest.{FunSpecLike, MustMatchers}

class ParticlesSpec extends FunSpecLike with MustMatchers {
  
  describe("Particle") {
    it("should return energy = 1 when there is only one particle") {
      val p = FloatingParticle('*', 1)
      val neighborhood = Map(p.id -> p)
      p.energy(neighborhood) mustBe 1
    }

    it("should preserve energy = 1 after transformation") {
      val p = FloatingParticle('*', 1)
      val neighborhood = Map(p.id -> p)
      val newNeighborhood = p.transform(neighborhood)

      p.energy(newNeighborhood) mustBe 1
    }

    it("should change particle location after transformation") {
      val particle = new FloatingParticle('*', 1, UUID.randomUUID().toString, 1L) {
        override def transform(neighborhood: Map[String, Particle]): Map[String, Particle] = {
            val deltaPos = 1
            val newParticle = copy(ver = this.ver + 1, pos = this.pos + deltaPos)
            neighborhood + (newParticle.id -> newParticle)
        }
      }

      val neighborhood = Map(particle.id -> particle)
      val newNeighborhood = particle.transform(neighborhood)
      val newParticle = newNeighborhood.map{ case (id, particle) => particle}.head
      newParticle.pos mustBe particle.pos + 1
    }
  }
  describe("StaticParticle") {
    it("should return energy = 0 when there is only one particle") {
      val p = StaticParticle('|', 1)
      val neighborhood = Map(p.id -> p)
      p.energy(neighborhood) mustBe 0
    }

    it("should preserve energy = 0 after transformation") {
      val p = StaticParticle('|', 1)
      val neighborhood = Map(p.id -> p)
      val newNeighborhood = p.transform(neighborhood)

      p.energy(newNeighborhood) mustBe 0
    }

    it("should not change particle location after transformation") {
      val particle = StaticParticle('|', 0, UUID.randomUUID().toString, 1L)
      val neighborhood = Map(particle.id -> particle)
      val newNeighborhood = particle.transform(neighborhood)
      val newParticle = newNeighborhood.map{ case (id, particle) => particle}.head
      newParticle.pos mustBe particle.pos
    }

    it("should not change neighborhood after transformation") {
      val particle = StaticParticle('|', 0, UUID.randomUUID().toString, 1L)
      val neighborhood = Map(particle.id -> particle)
      val newNeighborhood = particle.transform(neighborhood)

      neighborhood mustBe newNeighborhood
    }
  }
}

class StaticParticleSpec extends FunSpecLike with MustMatchers {
  describe("StaticParticle") {
    it("should return energy = 0 when there is only one particle") {
      val p = StaticParticle('|', 1)
      val neighborhood = Map(p.id -> p)
      p.energy(neighborhood) mustBe 0
    }

    it("should preserve energy = 0 after transformation") {
      val p = StaticParticle('|', 1)
      val neighborhood = Map(p.id -> p)
      val newNeighborhood = p.transform(neighborhood)

      p.energy(newNeighborhood) mustBe 0
    }

    it("should not change particle location after transformation") {
      val particle = StaticParticle('|', 0, UUID.randomUUID().toString, 1L)
      val neighborhood = Map(particle.id -> particle)
      val newNeighborhood = particle.transform(neighborhood)
      val newParticle = newNeighborhood.map{ case (id, particle) => particle}.head
      newParticle.pos mustBe particle.pos
    }

    it("should not change neighborhood after transformation") {
      val particle = StaticParticle('|', 0, UUID.randomUUID().toString, 1L)
      val neighborhood = Map(particle.id -> particle)
      val newNeighborhood = particle.transform(neighborhood)

      neighborhood mustBe newNeighborhood
    }
  }
}