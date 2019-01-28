package agents

import akka.actor.ActorRef
import akka.event.{EventBus, LookupClassification}
import akka.actor._
import akka.event._

final case class FieldEvent(kind: String, payload: Any)

class Field extends EventBus with LookupClassification {
  type Event = FieldEvent
  type Classifier = String
  type Subscriber = ActorRef

  override protected def mapSize(): Int = 128

  override protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event.payload
  }

  override protected def classify(event: Event): Classifier = event.kind

  override protected def compareSubscribers(a: Subscriber, b: Subscriber): Int = a.compareTo(b)
}




