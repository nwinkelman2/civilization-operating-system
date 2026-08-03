import { gql } from '@apollo/client'

export const GET_CIVILIZATIONS = gql`
  query GetCivilizations($page: Int, $size: Int) {
    civilizations(page: $page, size: $size) {
      content {
        id
        name
        scale
        region
        status
        reputationScore
        population
        resources {
          food
          water
          minerals
          energy
          housing
        }
      }
      totalPages
      totalElements
    }
  }
`

export const GET_CIVILIZATION = gql`
  query GetCivilization($id: ID!) {
    civilization(id: $id) {
      id
      name
      scale
      status
      reputationScore
      population
      resources {
        food
        water
        minerals
        energy
        housing
      }
      nexusNodes {
        id
        name
        type
        status
      }
    }
  }
`

export const GET_NEXUS_NODES = gql`
  query GetNexusNodes($civilizationId: ID) {
    nexusNodes(civilizationId: $civilizationId) {
      id
      name
      type
      status
      region
      lastActiveAt
      messageCount
    }
  }
`

export const GET_LEADERBOARD = gql`
  query GetLeaderboard {
    leaderboard {
      civilizationId
      name
      reputationScore
      population
      rank
    }
  }
`

export const GET_SIMULATION_STATUS = gql`
  query GetSimulationStatus {
    simulationStatus {
      engineName
      activeRules
      lastDecision
      tickCount
      monitoredCategories
    }
  }
`

export const CREATE_CIVILIZATION = gql`
  mutation CreateCivilization($name: String!, $scale: CivilizationScale, $region: String) {
    createCivilization(name: $name, scale: $scale, region: $region) {
      id
      name
      status
      resources {
        food
        water
      }
    }
  }
`

export const FOUND_CIVILIZATION = gql`
  mutation FoundCivilization($name: String!, $scale: CivilizationScale, $regionId: ID!) {
    foundCivilization(name: $name, scale: $scale, regionId: $regionId) {
      id
      name
      status
    }
  }
`

export const GET_REGIONS = gql`
  query GetRegions($claimed: Boolean) {
    regions(claimed: $claimed) {
      id
      name
      description
      scale
      foodAvailability
      waterAvailability
      mineralAvailability
      energyAvailability
      housingAvailability
      dominantResource
      claimed
      claimedByCivilizationId
    }
  }
`

export const GET_RESOURCES = gql`
  query GetResources($region: String) {
    resources(region: $region) {
      id
      name
      type
      description
      quantity
      unit
      region
    }
  }
`

export const GET_SHIPMENTS = gql`
  query GetShipments($civilizationId: ID, $status: ShipmentStatus) {
    shipments(civilizationId: $civilizationId, status: $status) {
      id
      originRegion
      destinationRegion
      resourceType
      quantity
      status
      civilizationId
      createdAt
    }
  }
`

export const GET_GLOBAL_EVENTS = gql`
  query GetGlobalEvents($activeOnly: Boolean) {
    globalEvents(activeOnly: $activeOnly) {
      id
      title
      description
      type
      severity
      active
      startedAt
      endedAt
    }
  }
`

export const GET_PROJECTS = gql`
  query GetProjects($civilizationId: ID, $category: ProjectCategory) {
    projects(civilizationId: $civilizationId, category: $category) {
      id
      name
      description
      category
      status
      targetContribution
      currentContribution
      civilizationId
    }
  }
`

export const GET_INCIDENTS = gql`
  query GetIncidents($civilizationId: ID) {
    incidents(civilizationId: $civilizationId) {
      id
      title
      description
      type
      status
      civilizationId
      createdAt
    }
  }
`

export const GET_BALANCE_REPORT = gql`
  query GetBalanceReport {
    balanceReport {
      category
      percentageMet
      status
    }
  }
`

export const GET_EMERGENT_ARCHETYPES = gql`
  query GetEmergentArchetypes {
    emergentArchetypes {
      archetype
      civilizationId
      civilizationName
      emergenceScore
      keyFeature
    }
  }
`


