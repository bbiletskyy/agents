package agents

case class DynamicState(curr: State, next: State) {
  def version = curr.version


  def transform(transformation:(State)=>State): DynamicState = {
    this.copy()
  }

}

object DynamicState {
  def apply(initialState: State): DynamicState = DynamicState(initialState, initialState)
}


