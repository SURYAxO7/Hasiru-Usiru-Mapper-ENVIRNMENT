# Firestore Database Schema

## users/{userId}
| Field | Type | Description |
|-------|------|-------------|
| name | string | Display name |
| email | string | Email address |
| phone | string | Phone number |
| photoUrl | string | Profile image URL |
| role | string | CITIZEN, VOLUNTEER, MUNICIPALITY, ADMIN |
| contributionPoints | number | Gamification points |
| treesTagged | number | Total trees contributed |
| pitsReported | number | Total pits reported |
| badges | array | Earned badge IDs |
| preferredLanguage | string | en or kn |
| city | string | Bengaluru or Mysuru |
| fcmToken | string | Push notification token |
| createdAt | timestamp | Account creation |

## trees/{treeId}
| Field | Type | Description |
|-------|------|-------------|
| userId | string | Contributor UID |
| userName | string | Contributor name |
| latitude | number | GPS latitude |
| longitude | number | GPS longitude |
| species | string | English species name |
| speciesKn | string | Kannada name |
| scientificName | string | Botanical name |
| girthCm | number | Trunk girth in cm |
| healthCondition | string | EXCELLENT to CRITICAL |
| ageEstimateYears | number | Estimated age |
| oxygenScore | number | girth × species factor |
| imageUrl | string | Firebase Storage URL |
| environmentalBenefits | string | AI-generated benefits |
| kannadaDescription | string | Kannada description |
| healthSuggestions | string | Care recommendations |
| aiConfidence | number | 0-1 confidence |
| status | string | PENDING, APPROVED, REJECTED |
| city | string | City name |
| createdAt | timestamp | Creation time |
| updatedAt | timestamp | Last update |

## empty_pits/{pitId}
| Field | Type | Description |
|-------|------|-------------|
| userId | string | Reporter UID |
| latitude | number | GPS latitude |
| longitude | number | GPS longitude |
| imageUrl | string | Pit photo URL |
| soilType | string | CLAY, SANDY, LOAMY, etc. |
| pitWidthCm | number | Width in cm |
| pitDepthCm | number | Depth in cm |
| waterAvailability | string | LOW, MODERATE, HIGH |
| sunlightExposure | string | FULL, PARTIAL, SHADE |
| nearbyConditions | string | Free text notes |
| recommendedSpecies | array | AI species suggestions |
| priority | string | LOW to URGENT |
| status | string | PENDING, APPROVED, REJECTED |
| adminNotes | string | Municipality notes |
| city | string | City name |
| createdAt | timestamp | Report time |

## species/{speciesId}
Cached native species guide entries (seeded from Room on first launch).

## leaderboard/{city}
Aggregated ranking documents updated via Cloud Functions.
