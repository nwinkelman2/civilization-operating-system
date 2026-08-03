from abc import ABC
import logging
import uuid
from typing import Any, Dict, Optional

from cortex_sdk.client import CivOSClient
from cortex_sdk.event_bus import (
    GLOBAL_EVENT,
    NEXUS_MESSAGE,
    RESOURCE_TICK,
    CivOSEventListener,
)

logger = logging.getLogger(__name__)


class BaseCortexAgent(ABC):
    """Abstract base class for Cortex autonomous AI agents."""

    def __init__(
        self,
        client: CivOSClient,
        agent_id: Optional[str] = None,
        name: Optional[str] = None,
        event_listener: Optional[CivOSEventListener] = None,
    ):
        self.client = client
        self.agent_id = agent_id or f"agent-{uuid.uuid4().hex[:8]}"
        self.name = name or self.__class__.__name__

        self.event_listener = event_listener or CivOSEventListener()
        self._bind_event_listener(self.event_listener)

    def _bind_event_listener(self, listener: CivOSEventListener) -> None:
        """Bind agent callbacks to the event listener."""
        listener.on(RESOURCE_TICK, self.on_resource_tick)
        listener.on(NEXUS_MESSAGE, self.on_nexus_message)
        listener.on(GLOBAL_EVENT, self.on_global_event)

    def on_resource_tick(self, tick_data: Dict[str, Any]) -> None:
        """Callback invoked on resource tick events."""
        pass

    def on_nexus_message(self, message_data: Dict[str, Any]) -> None:
        """Callback invoked on incoming Nexus messages."""
        pass

    def on_global_event(self, event_data: Dict[str, Any]) -> None:
        """Callback invoked on global system events."""
        pass

    def send_message(self, target_node_id: str, content: str) -> Dict[str, Any]:
        """Send a message to a target Nexus node."""
        logger.info(f"Agent '{self.name}' ({self.agent_id}) sending message to node {target_node_id}")
        return self.client.send_nexus_message(
            target_node_id=target_node_id,
            content=content,
            sender_id=self.agent_id,
        )

    def submit_trade(self, target_civ_id: str, resource_type: str, quantity: float) -> Dict[str, Any]:
        """Submit a trade proposal to a target civilization."""
        logger.info(
            f"Agent '{self.name}' ({self.agent_id}) proposing trade of {quantity} {resource_type} to civ {target_civ_id}"
        )
        return self.client.propose_trade(
            target_civilization_id=target_civ_id,
            resource_type=resource_type,
            quantity=quantity,
        )
