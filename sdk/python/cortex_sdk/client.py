import json
from typing import Any, Dict, Optional
import urllib.parse
import urllib.request


class CivOSClient:
    """Client for interacting with the Civilization Operating System GraphQL and REST APIs."""

    def __init__(self, base_url: str = "http://localhost:8080", timeout: float = 10.0):
        self.base_url = base_url.rstrip("/")
        self.timeout = timeout

    def _request(
        self,
        method: str,
        endpoint: str,
        params: Optional[Dict[str, Any]] = None,
        json_data: Optional[Dict[str, Any]] = None,
        headers: Optional[Dict[str, str]] = None,
    ) -> Any:
        url = f"{self.base_url}{endpoint}"
        if params:
            filtered_params = {k: v for k, v in params.items() if v is not None}
            if filtered_params:
                query_string = urllib.parse.urlencode(filtered_params)
                url = f"{url}?{query_string}"

        req_headers = {"Accept": "application/json"}
        if headers:
            req_headers.update(headers)

        body_bytes = None
        if json_data is not None:
            req_headers["Content-Type"] = "application/json"
            body_bytes = json.dumps(json_data).encode("utf-8")

        req = urllib.request.Request(url, data=body_bytes, headers=req_headers, method=method.upper())

        try:
            with urllib.request.urlopen(req, timeout=self.timeout) as response:
                response_text = response.read().decode("utf-8")
                if not response_text:
                    return {}
                return json.loads(response_text)
        except urllib.error.HTTPError as e:
            error_body = e.read().decode("utf-8")
            try:
                parsed_error = json.loads(error_body)
                raise RuntimeError(f"HTTP {e.code}: {parsed_error}") from e
            except json.JSONDecodeError:
                raise RuntimeError(f"HTTP {e.code}: {error_body}") from e

    def query(self, query_string: str, variables: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
        """Execute a GraphQL query."""
        payload = {
            "query": query_string,
            "variables": variables or {},
        }
        return self._request("POST", "/graphql", json_data=payload)

    def get_civilizations(self, page: int = 0, size: int = 10) -> Dict[str, Any]:
        """Get paginated list of civilizations."""
        return self._request("GET", "/api/v1/civilizations", params={"page": page, "size": size})

    def get_regions(self, claimed: Optional[bool] = None) -> Any:
        """Get regions, optionally filtered by claim status."""
        params = {}
        if claimed is not None:
            params["claimed"] = str(claimed).lower()
        return self._request("GET", "/api/v1/regions", params=params)

    def get_nexus_nodes(self) -> Any:
        """Get list of nexus nodes."""
        return self._request("GET", "/api/v1/nexus/nodes")

    def get_technologies(self) -> Any:
        """Get available technologies / tech tree."""
        return self._request("GET", "/api/v1/technologies")

    def propose_trade(
        self,
        target_civilization_id: str,
        resource_type: str,
        quantity: float,
        notes: str = "",
    ) -> Dict[str, Any]:
        """Propose a trade with another civilization."""
        payload = {
            "target_civilization_id": target_civilization_id,
            "resource_type": resource_type,
            "quantity": quantity,
            "notes": notes,
        }
        return self._request("POST", "/api/v1/trade", json_data=payload)

    def propose_rule(self, title: str, description: str, category: str) -> Dict[str, Any]:
        """Propose a governance rule."""
        payload = {
            "title": title,
            "description": description,
            "category": category,
        }
        return self._request("POST", "/api/v1/rules", json_data=payload)

    def send_nexus_message(
        self,
        target_node_id: str,
        content: str,
        sender_id: Optional[str] = None,
    ) -> Dict[str, Any]:
        """Send a message across the Nexus mesh network."""
        payload = {
            "target_node_id": target_node_id,
            "content": content,
        }
        if sender_id:
            payload["sender_id"] = sender_id
        return self._request("POST", "/api/v1/nexus/messages", json_data=payload)

