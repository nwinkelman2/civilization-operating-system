import json
import logging
from typing import Any, Callable, Dict, List, Union

logger = logging.getLogger(__name__)

# Standard Event Types
RESOURCE_TICK = "RESOURCE_TICK"
NEXUS_MESSAGE = "NEXUS_MESSAGE"
GLOBAL_EVENT = "GLOBAL_EVENT"

# Topic/Event Name Mappings to Standard Event Types
EVENT_TYPE_MAPPING = {
    "civos.resources.tick_processed": RESOURCE_TICK,
    "RESOURCE_TICK": RESOURCE_TICK,
    "civos.nexus.message_sent": NEXUS_MESSAGE,
    "NEXUS_MESSAGE": NEXUS_MESSAGE,
    "civos.events.global_occurred": GLOBAL_EVENT,
    "GLOBAL_EVENT": GLOBAL_EVENT,
}


class CivOSEventListener:
    """Listener and dispatcher for CivOS domain events."""

    RESOURCE_TICK = RESOURCE_TICK
    NEXUS_MESSAGE = NEXUS_MESSAGE
    GLOBAL_EVENT = GLOBAL_EVENT

    def __init__(self):
        self._handlers: Dict[str, List[Callable[[Dict[str, Any]], None]]] = {}

    def on(self, event_type: str, handler: Callable[[Dict[str, Any]], None]) -> None:
        """Register a handler for a specific event type."""
        if event_type not in self._handlers:
            self._handlers[event_type] = []
        if handler not in self._handlers[event_type]:
            self._handlers[event_type].append(handler)

    def register_handler(self, event_type: str, handler: Callable[[Dict[str, Any]], None]) -> None:
        """Alias for `on` to register an event handler."""
        self.on(event_type, handler)

    def remove_handler(self, event_type: str, handler: Callable[[Dict[str, Any]], None]) -> None:
        """Unregister a handler for a specific event type."""
        if event_type in self._handlers and handler in self._handlers[event_type]:
            self._handlers[event_type].remove(handler)

    def dispatch(self, event_type: str, data: Dict[str, Any]) -> None:
        """Dispatch event data to all registered handlers for the event type."""
        handlers = self._handlers.get(event_type, [])
        for handler in handlers:
            try:
                handler(data)
            except Exception as e:
                logger.error(f"Error executing handler for event '{event_type}': {e}", exc_info=True)

    def emit(self, event_type: str, data: Dict[str, Any]) -> None:
        """Alias for dispatch."""
        self.dispatch(event_type, data)

    def process_event(self, raw_event: Union[str, bytes, Dict[str, Any]]) -> None:
        """Parse raw event (JSON string/bytes or dict) and dispatch to matching handlers."""
        if isinstance(raw_event, (str, bytes)):
            try:
                event_dict = json.loads(raw_event)
            except json.JSONDecodeError as e:
                logger.error(f"Failed to parse JSON event: {e}")
                return
        elif isinstance(raw_event, dict):
            event_dict = raw_event
        else:
            logger.error(f"Unsupported event payload type: {type(raw_event)}")
            return

        raw_event_type = event_dict.get("event_type") or event_dict.get("type") or ""
        event_type = EVENT_TYPE_MAPPING.get(raw_event_type, raw_event_type)

        payload = event_dict.get("data")
        if payload is None:
            payload = event_dict.get("payload")
        if payload is None:
            payload = event_dict

        if event_type:
            self.dispatch(event_type, payload)
            # If mapped event type differs from raw event type, also dispatch to raw event type handlers if any
            if raw_event_type and raw_event_type != event_type:
                self.dispatch(raw_event_type, payload)
        else:
            logger.warning("Event payload missing 'event_type' or 'type' field.")
