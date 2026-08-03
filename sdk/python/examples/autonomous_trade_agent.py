import logging
from typing import Any, Dict, Optional

from cortex_sdk import BaseCortexAgent, CivOSClient, CivOSEventListener

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class AutonomousTradeAgent(BaseCortexAgent):
    """An autonomous Cortex AI agent that checks resource levels on tick events

    and automatically proposes trades when resource scarcity is detected.
    """

    def __init__(
        self,
        client: CivOSClient,
        target_civ_id: str,
        scarcity_threshold: float = 20.0,
        trade_request_amount: float = 50.0,
        agent_id: Optional[str] = None,
        name: Optional[str] = None,
        event_listener: Optional[CivOSEventListener] = None,
    ):
        super().__init__(client=client, agent_id=agent_id, name=name, event_listener=event_listener)
        self.target_civ_id = target_civ_id
        self.scarcity_threshold = scarcity_threshold
        self.trade_request_amount = trade_request_amount
        self.trade_history = []

    def check_scarcity_and_trade(self, resources: Dict[str, float]) -> None:
        """Check resource levels against scarcity threshold and propose trades if scarce."""
        for resource_type, quantity in resources.items():
            if quantity < self.scarcity_threshold:
                logger.warning(
                    f"[{self.name}] Low resource detected: {resource_type} = {quantity} "
                    f"(Threshold: {self.scarcity_threshold})"
                )
                result = self.submit_trade(
                    target_civ_id=self.target_civ_id,
                    resource_type=resource_type,
                    quantity=self.trade_request_amount,
                )
                self.trade_history.append(
                    {
                        "resource_type": resource_type,
                        "quantity": self.trade_request_amount,
                        "result": result,
                    }
                )

    def on_resource_tick(self, tick_data: Dict[str, Any]) -> None:
        """Process resource tick data to evaluate resource scarcity."""
        logger.info(f"[{self.name}] Processing resource tick: {tick_data.get('tick_id', 'unknown')}")
        resources = tick_data.get("resources", {})
        self.check_scarcity_and_trade(resources)

    def on_nexus_message(self, message_data: Dict[str, Any]) -> None:
        """Log incoming Nexus mesh network messages."""
        logger.info(
            f"[{self.name}] Received Nexus message from {message_data.get('sender_id')}: "
            f"{message_data.get('content')}"
        )

    def on_global_event(self, event_data: Dict[str, Any]) -> None:
        """Handle global events (e.g. natural disasters, policy updates)."""
        logger.info(f"[{self.name}] Global event occurred: {event_data.get('event_name')}")


def main():
    # Initialize client and agent
    client = CivOSClient(base_url="http://localhost:8080")
    agent = AutonomousTradeAgent(
        client=client,
        target_civ_id="civ-partner-02",
        scarcity_threshold=30.0,
        trade_request_amount=100.0,
        name="AutoTradeBot-01",
    )

    simulated_tick_event = {
        "event_type": "RESOURCE_TICK",
        "data": {
            "tick_id": 1042,
            "civilization_id": "civ-main-01",
            "resources": {
                "ENERGY": 150.0,
                "MINERALS": 15.0,
                "FOOD": 8.0,
            },
        },
    }

    print(f"Simulating tick event processing for agent '{agent.name}'...")
    agent.event_listener.process_event(simulated_tick_event)
    print(f"Trade proposals initiated: {len(agent.trade_history)}")


if __name__ == "__main__":
    main()
