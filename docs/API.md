# API Reference

Base URL: `http://localhost:8080`

## Strategy

### GET `/api/v1/strategy/balance`
Returns the resource balance report — supply vs demand across all categories.

**Response:** `List<Map<String, Object>>`
```json
[
  {
    "category": "ENERGY",
    "available": 1500.0,
    "required": 2000.0,
    "percentageMet": 75.0
  }
]
```

---

## Resources

### GET `/api/v1/resources`
List all mapped resources.

### POST `/api/v1/resources`
Register a new resource.

**Body:**
```json
{
  "name": "Solar Panel Array",
  "type": "ENERGY",
  "quantity": 500,
  "unit": "MW",
  "region": "South America"
}
```

---

## Needs

### GET `/api/v1/needs`
List all human needs.

### GET `/api/v1/needs/{region}`
Filter needs by region.

### POST `/api/v1/needs`
Register a new need.

---

## Production

### GET `/api/v1/facilities`
List all production facilities.

### POST `/api/v1/facilities`
Register a new facility.

---

## Logistics

### GET `/api/v1/shipments`
List all shipments.

### POST `/api/v1/shipments`
Create a new shipment.

---

## Biosphere Monitoring

### GET `/api/v1/biosphere`
List all biosphere metrics.

### POST `/api/v1/biosphere`
Record a new metric. If status is `CRITICAL`, triggers a `BiosphereCriticalEvent`.

---

## Constitution (Rules)

### GET `/api/v1/rules`
List all governance rules.

### POST `/api/v1/rules`
Propose a new rule (status = `PROPOSED`, votes = 0).

### POST `/api/v1/rules/{id}/vote`
Vote on a rule (increments `votesCount`).

---

## Interaction

### GET `/api/v1/interactions`
List all community interactions.

### POST `/api/v1/interactions`
Record a new interaction.

---

## Automation (Execution)

### GET `/api/v1/automation`
List all automation units.

### POST `/api/v1/automation`
Register a new automation unit.

### POST `/api/v1/automation/{id}/status`
Update unit status.

### DELETE `/api/v1/automation/{id}`
Remove an automation unit.

---

## Governance

### GET `/api/v1/governance`
List all scientific committees.

### POST `/api/v1/governance`
Create a new committee.

---

## Purpose & Contribution

### POST `/api/v1/purpose/contribute`
Record a contribution (updates citizen reputation).

### GET `/api/v1/purpose/contributions`
List all contributions.

### GET `/api/v1/purpose/citizens`
List all citizens.

### GET `/api/v1/purpose/projects`
List active projects.

### GET `/api/v1/purpose/citizens/{citizenId}/impact`
Get contributions for a specific citizen.

---

## Social Stability

### GET `/api/v1/social/incidents`
List all reported incidents.

### POST `/api/v1/social/incidents`
Report a new incident.

### GET `/api/v1/social/cases`
List all cases.

### GET `/api/v1/social/assessments/{citizenId}`
Get behavior assessments for a citizen.

---

## Simulation

### GET `/api/v1/simulation/status`
Get the Cortex engine status.

**Response:**
```json
{
  "engine": "Cortex Simulation Engine (Java Native)",
  "activeRulesCount": 3,
  "lastDecision": "DECISION: ENERGY deficiency detected...",
  "monitoredCategories": ["ENERGY", "FOOD", "WATER"]
}
```

---

## nexus Mesh Extensions

### GET `/api/v1/nexus/trades`
Returns a history log of autonomous cortex mesh resource and personnel trades.

### POST `/api/v1/nexus/civilizations/{id}/robot-priorities`
Update priority weights for robot fabrications (Agri, Aqua, Explore, Utility).

---

## Citizen Migrations

### POST `/api/v1/migrations/apply`
Submit a transfer transit request to relocate to another civilization.

### GET `/api/v1/migrations/pending/{civId}`
List pending incoming migration applications for a target civilization.

### POST `/api/v1/migrations/{id}/approve`
Approve migration application, shifting populations between target civilizations.

### POST `/api/v1/migrations/{id}/reject`
Reject migration application.
