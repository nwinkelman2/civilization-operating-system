import json
import unittest
from unittest.mock import MagicMock, patch

from cortex_sdk import CivOSClient


class TestCivOSClient(unittest.TestCase):

    def setUp(self):
        self.client = CivOSClient(base_url="http://localhost:8080")

    def mock_urlopen_response(self, data, status=200):
        response_bytes = json.dumps(data).encode("utf-8")
        mock_response = MagicMock()
        mock_response.read.return_value = response_bytes
        mock_response.__enter__.return_value = mock_response
        mock_response.__exit__.return_value = None
        return mock_response

    @patch("urllib.request.urlopen")
    def test_query(self, mock_urlopen):
        mock_data = {"data": {"civilizations": [{"id": "civ-1"}]}}
        mock_urlopen.return_value = self.mock_urlopen_response(mock_data)

        query_str = "query { civilizations { id } }"
        res = self.client.query(query_str, variables={"limit": 5})

        self.assertEqual(res, mock_data)
        req = mock_urlopen.call_args[0][0]
        self.assertEqual(req.get_full_url(), "http://localhost:8080/graphql")
        self.assertEqual(req.get_method(), "POST")
        body = json.loads(req.data.decode("utf-8"))
        self.assertEqual(body["query"], query_str)
        self.assertEqual(body["variables"], {"limit": 5})

    @patch("urllib.request.urlopen")
    def test_get_civilizations(self, mock_urlopen):
        mock_data = {"content": [{"id": "civ-1", "name": "Solaria"}]}
        mock_urlopen.return_value = self.mock_urlopen_response(mock_data)

        res = self.client.get_civilizations(page=1, size=20)
        self.assertEqual(res, mock_data)
        req = mock_urlopen.call_args[0][0]
        self.assertEqual(req.get_full_url(), "http://localhost:8080/api/v1/civilizations?page=1&size=20")
        self.assertEqual(req.get_method(), "GET")

    @patch("urllib.request.urlopen")
    def test_get_regions(self, mock_urlopen):
        mock_data = [{"id": "r1", "claimed": True}]
        mock_urlopen.return_value = self.mock_urlopen_response(mock_data)

        res = self.client.get_regions(claimed=True)
        self.assertEqual(res, mock_data)
        req = mock_urlopen.call_args[0][0]
        self.assertEqual(req.get_full_url(), "http://localhost:8080/api/v1/regions?claimed=true")

    @patch("urllib.request.urlopen")
    def test_get_nexus_nodes(self, mock_urlopen):
        mock_data = [{"id": "node-1", "address": "192.168.1.1"}]
        mock_urlopen.return_value = self.mock_urlopen_response(mock_data)

        res = self.client.get_nexus_nodes()
        self.assertEqual(res, mock_data)
        req = mock_urlopen.call_args[0][0]
        self.assertEqual(req.get_full_url(), "http://localhost:8080/api/v1/nexus/nodes")

    @patch("urllib.request.urlopen")
    def test_get_technologies(self, mock_urlopen):
        mock_data = [{"id": "tech-1", "name": "Solar Energy"}]
        mock_urlopen.return_value = self.mock_urlopen_response(mock_data)

        res = self.client.get_technologies()
        self.assertEqual(res, mock_data)
        req = mock_urlopen.call_args[0][0]
        self.assertEqual(req.get_full_url(), "http://localhost:8080/api/v1/technologies")

    @patch("urllib.request.urlopen")
    def test_propose_trade(self, mock_urlopen):
        mock_data = {"status": "SUCCESS", "trade_id": "t100"}
        mock_urlopen.return_value = self.mock_urlopen_response(mock_data)

        res = self.client.propose_trade("civ-2", "MINERALS", 50.0, notes="Urgent request")
        self.assertEqual(res, mock_data)
        req = mock_urlopen.call_args[0][0]
        self.assertEqual(req.get_full_url(), "http://localhost:8080/api/v1/trade")
        self.assertEqual(req.get_method(), "POST")
        body = json.loads(req.data.decode("utf-8"))
        self.assertEqual(body["target_civilization_id"], "civ-2")
        self.assertEqual(body["resource_type"], "MINERALS")
        self.assertEqual(body["quantity"], 50.0)
        self.assertEqual(body["notes"], "Urgent request")

    @patch("urllib.request.urlopen")
    def test_propose_rule(self, mock_urlopen):
        mock_data = {"status": "PROPOSED", "rule_id": "rule-42"}
        mock_urlopen.return_value = self.mock_urlopen_response(mock_data)

        res = self.client.propose_rule("Equal Distribution", "Distribute energy equally", "ECONOMY")
        self.assertEqual(res, mock_data)
        req = mock_urlopen.call_args[0][0]
        self.assertEqual(req.get_full_url(), "http://localhost:8080/api/v1/rules")
        self.assertEqual(req.get_method(), "POST")
        body = json.loads(req.data.decode("utf-8"))
        self.assertEqual(body["title"], "Equal Distribution")
        self.assertEqual(body["description"], "Distribute energy equally")
        self.assertEqual(body["category"], "ECONOMY")


if __name__ == "__main__":
    unittest.main()
