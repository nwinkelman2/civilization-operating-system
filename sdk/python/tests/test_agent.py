import json
import unittest
from unittest.mock import MagicMock, patch

from cortex_sdk import (
    GLOBAL_EVENT,
    NEXUS_MESSAGE,
    RESOURCE_TICK,
    BaseCortexAgent,
    CivOSClient,
    CivOSEventListener,
)
from examples.autonomous_trade_agent import AutonomousTradeAgent


class DummyAgent(BaseCortexAgent):
    """Concrete test agent implementation."""

    def __init__(self, client: CivOSClient, **kwargs):
        super().__init__(client=client, **kwargs)
        self.tick_calls = []
        self.message_calls = []
        self.event_calls = []

    def on_resource_tick(self, tick_data):
        self.tick_calls.append(tick_data)

    def on_nexus_message(self, message_data):
        self.message_calls.append(message_data)

    def on_global_event(self, event_data):
        self.event_calls.append(event_data)


class TestCivOSEventListener(unittest.TestCase):

    def setUp(self):
        self.listener = CivOSEventListener()

    def test_handler_registration_and_dispatch(self):
        mock_handler = MagicMock()
        self.listener.on(RESOURCE_TICK, mock_handler)

        payload = {"tick_id": 100, "resources": {"FOOD": 50}}
        self.listener.dispatch(RESOURCE_TICK, payload)

        mock_handler.assert_called_once_with(payload)

    def test_alias_register_handler_and_emit(self):
        mock_handler = MagicMock()
        self.listener.register_handler(NEXUS_MESSAGE, mock_handler)

        payload = {"sender_id": "node-1", "content": "hello"}
        self.listener.emit(NEXUS_MESSAGE, payload)

        mock_handler.assert_called_once_with(payload)

    def test_remove_handler(self):
        mock_handler = MagicMock()
        self.listener.on(GLOBAL_EVENT, mock_handler)
        self.listener.remove_handler(GLOBAL_EVENT, mock_handler)

        self.listener.dispatch(GLOBAL_EVENT, {"event_name": "Solar Flare"})
        mock_handler.assert_not_called()

    def test_process_event_dict(self):
        mock_handler = MagicMock()
        self.listener.on(RESOURCE_TICK, mock_handler)

        raw_dict = {
            "event_type": "RESOURCE_TICK",
            "data": {"tick_id": 101},
        }
        self.listener.process_event(raw_dict)
        mock_handler.assert_called_once_with({"tick_id": 101})

    def test_process_event_json_str(self):
        mock_handler = MagicMock()
        self.listener.on(GLOBAL_EVENT, mock_handler)

        raw_str = json.dumps({"type": "GLOBAL_EVENT", "payload": {"name": "Market Crash"}})
        self.listener.process_event(raw_str)
        mock_handler.assert_called_once_with({"name": "Market Crash"})

    def test_process_event_topic_mapping(self):
        mock_handler = MagicMock()
        self.listener.on(RESOURCE_TICK, mock_handler)

        raw_kafka_event = {
            "event_type": "civos.resources.tick_processed",
            "data": {"tick_id": 202},
        }
        self.listener.process_event(raw_kafka_event)
        mock_handler.assert_called_once_with({"tick_id": 202})


class TestBaseCortexAgent(unittest.TestCase):

    def setUp(self):
        self.client = MagicMock(spec=CivOSClient)
        self.agent = DummyAgent(client=self.client, agent_id="test-agent-01", name="TestAgent")

    def test_initialization(self):
        self.assertEqual(self.agent.agent_id, "test-agent-01")
        self.assertEqual(self.agent.name, "TestAgent")
        self.assertIsNotNone(self.agent.event_listener)

    def test_event_listener_binding_and_callbacks(self):
        tick_data = {"tick_id": 1}
        message_data = {"sender_id": "n1", "content": "ping"}
        event_data = {"event_name": "Election"}

        self.agent.event_listener.dispatch(RESOURCE_TICK, tick_data)
        self.agent.event_listener.dispatch(NEXUS_MESSAGE, message_data)
        self.agent.event_listener.dispatch(GLOBAL_EVENT, event_data)

        self.assertEqual(self.agent.tick_calls, [tick_data])
        self.assertEqual(self.agent.message_calls, [message_data])
        self.assertEqual(self.agent.event_calls, [event_data])

    def test_send_message(self):
        self.client.send_nexus_message.return_value = {"status": "DELIVERED"}

        res = self.agent.send_message("node-42", "Greetings Nexus")

        self.assertEqual(res, {"status": "DELIVERED"})
        self.client.send_nexus_message.assert_called_once_with(
            target_node_id="node-42",
            content="Greetings Nexus",
            sender_id="test-agent-01",
        )

    def test_submit_trade(self):
        self.client.propose_trade.return_value = {"status": "ACCEPTED", "trade_id": "t1"}

        res = self.agent.submit_trade("civ-99", "MINERALS", 75.0)

        self.assertEqual(res, {"status": "ACCEPTED", "trade_id": "t1"})
        self.client.propose_trade.assert_called_once_with(
            target_civilization_id="civ-99",
            resource_type="MINERALS",
            quantity=75.0,
        )


class TestAutonomousTradeAgent(unittest.TestCase):

    def setUp(self):
        self.client = MagicMock(spec=CivOSClient)
        self.client.propose_trade.return_value = {"status": "PROPOSED", "trade_id": "t-100"}
        self.trade_agent = AutonomousTradeAgent(
            client=self.client,
            target_civ_id="civ-partner",
            scarcity_threshold=20.0,
            trade_request_amount=50.0,
            agent_id="trade-bot-1",
            name="TradeBot",
        )

    def test_scarcity_detection_triggers_trade(self):
        tick_event = {
            "event_type": "RESOURCE_TICK",
            "data": {
                "tick_id": 500,
                "resources": {
                    "ENERGY": 100.0,  # OK
                    "WATER": 10.0,   # Low (<20.0) -> should trigger trade proposal
                    "FOOD": 5.0,     # Low (<20.0) -> should trigger trade proposal
                },
            },
        }

        self.trade_agent.event_listener.process_event(tick_event)

        self.assertEqual(self.client.propose_trade.call_count, 2)
        self.assertEqual(len(self.trade_agent.trade_history), 2)

        calls = self.client.propose_trade.call_args_list
        proposed_resources = [c.kwargs["resource_type"] for c in calls]
        self.assertIn("WATER", proposed_resources)
        self.assertIn("FOOD", proposed_resources)


if __name__ == "__main__":
    unittest.main()
