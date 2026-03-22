# GitHub Access Report Service

This Spring Boot application connects to GitHub and generates a report showing which users have access to which repositories within a given organization.

## Features

- Authenticates with GitHub using a Personal Access Token
- Retrieves all repositories for a specified organization
- Determines user access levels for each repository
- Generates an aggregated JSON report mapping users to their accessible repositories with permissions

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- A GitHub Personal Access Token with `repo` and `read:org` scopes

## Setup

1. Clone the repository
2. Set the GitHub token as an environment variable:
   ```bash
   export GITHUB_TOKEN=your_personal_access_token_here
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```

## Running the Application

Run the application using Maven:
```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Usage

### Get Access Report

**Endpoint:** `GET /api/access-report/{organization}`

**Example:**
```bash
curl -H "Accept: application/json" http://localhost:8080/api/access-report/your-org-name
```

**Response:**
```json
{
  "userToRepos": {
    "user1": [
      {
        "repoName": "repo1",
        "permission": "admin"
      },
      {
        "repoName": "repo2",
        "permission": "push"
      }
    ],
    "user2": [
      {
        "repoName": "repo1",
        "permission": "pull"
      }
    ]
  }
}
```

## Authentication Configuration

The application uses a GitHub Personal Access Token for authentication. Set the `GITHUB_TOKEN` environment variable before running the application.

To create a Personal Access Token:
1. Go to GitHub Settings > Developer settings > Personal access tokens
2. Generate a new token with `repo` and `read:org` scopes
3. Set it as an environment variable

## Assumptions and Design Decisions

- The application assumes the token has sufficient permissions to read organization repositories and collaborators.
- For scalability, API calls for collaborators are made in parallel using reactive programming.
- Permissions are mapped from GitHub's permission flags (admin, push, pull) to roles.
- The report aggregates access across all repositories for each user.
- Error handling includes basic exception catching; in production, consider more robust error responses.

## Scale Considerations

- Designed to handle 100+ repositories and 1000+ users efficiently.
- Uses reactive WebClient for non-blocking API calls.
- Parallel processing of collaborator retrieval to minimize sequential API calls.
- GitHub API rate limits are respected; consider implementing rate limit handling for high-volume usage.