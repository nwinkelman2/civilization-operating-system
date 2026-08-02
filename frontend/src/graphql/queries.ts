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
