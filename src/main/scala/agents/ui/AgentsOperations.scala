package agents.ui


  trait AgentManagement {
    def start(): Unit
    def stop(): Unit
    def step(): Unit
  }

