# GitHub Access Report Service

This Spring Boot application connects to GitHub and generates a report showing which users have access to which repositories within a given organization. It authenticates securely, retrieves organization repositories, determines user access levels, and exposes a JSON API endpoint for the aggregated access report.

## Features

- **Secure Authentication**: Uses GitHub Personal Access Token (PAT) for API access.
- **Repository Retrieval**: Fetches all repositories for a specified GitHub organization.
- **User Access Determination**: Identifies collaborators and their permission levels (admin, push, pull) for each repository.
- **Aggregated Report**: Generates a JSON mapping of users to their accessible repositories with permissions.
- **Scalable Design**: Handles large organizations (100+ repos, 1000+ users) with parallel API calls.
- **Error Handling**: Gracefully handles forbidden access (403) by logging and continuing the report.

## Prerequisites

- **Java**: Version 17 or higher
- **Maven**: Version 3.6 or higher
- **GitHub Account**: With access to the target organization
- **GitHub Personal Access Token**: With `repo` and `read:org` scopes

## Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Shankarraospatil/github-access-report.git
   cd github-access-report
   ```

2. **Set the GitHub Token**:
   - Create a PAT at [GitHub Settings > Developer settings > Personal access tokens](https://github.com/settings/tokens).
   - Select scopes: `repo` (full control of private repositories) and `read:org` (read org membership).
   - Set as environment variable:
     ```bash
     export GITHUB_TOKEN=your_personal_access_token_here
     ```
     Or in Windows PowerShell: `$env:GITHUB_TOKEN = "your_token"`

3. **Build the Project**:
   ```bash
   mvn clean install
   ```

## Running the Application

### Using Maven
```bash
mvn spring-boot:run
```
The application starts on port 8090 (configurable in `application.properties`).

### Using Eclipse
1. Import as Maven project: `File > Import > Maven > Existing Maven Projects`.
2. Select the `pom.xml` file.
3. Run: Right-click project > `Run As > Spring Boot App`.

### Alternative: Command-Line Override
```bash
mvn spring-boot:run -Dgithub.token=your_token_here
```

## API Usage

### Get Access Report

**Endpoint**: `GET /api/access-report/{organization}`

**Description**: Returns a JSON report of user access to repositories in the specified organization.

**Example Request**:
```bash
curl -H "Accept: application/json" http://localhost:8090/api/access-report/your-org-name
```

**Example Response**:
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

**Response Fields**:
- `userToRepos`: Object mapping usernames to arrays of repository accesses.
- `repoName`: Name of the repository.
- `permission`: Access level (`admin`, `push`, `pull`, `read`).

**Error Responses**:
- `403 Forbidden`: Token lacks permissions for org/repos.
- `401 Unauthorized`: Invalid or missing token.
- `404 Not Found`: Organization not found.

## Authentication Configuration

The app uses GitHub's REST API v3 with Bearer token authentication.

- **Token Storage**: Set via environment variable `GITHUB_TOKEN` (recommended) or directly in `application.properties` (not for production).
- **Scopes Required**:
  - `repo`: Access to private repositories and collaborators.
  - `read:org`: Read organization membership and repositories.
- **Security**: Never commit tokens to version control. Use `.gitignore` to exclude `application.properties` if hardcoded.

## Project Structure

```
github-access-report/
├── pom.xml                          # Maven dependencies and build config
├── README.md                        # This file
├── .gitignore                       # Excludes build artifacts and secrets
└── src/
    └── main/
        ├── java/
        │   └── com/cloudeagle/githubaccessreport/
        │       ├── GithubAccessReportApplication.java  # Main Spring Boot class
        │       ├── controller/
        │       │   └── AccessReportController.java     # REST API endpoint
        │       ├── model/
        │       │   ├── AccessReport.java               # Response model
        │       │   ├── Repository.java                 # Repo with collaborators
        │       │   ├── RepositoryAccess.java           # User-repo permission
        │       │   └── User.java                       # User with login/role
        │       └── service/
        │           └── GithubService.java              # GitHub API logic
        └── resources/
            └── application.properties                 # Config (token placeholder)
```

## Design Overview

- **Architecture**: MVC with service layer for API calls.
- **Reactive Programming**: Uses Spring WebFlux WebClient for non-blocking, parallel HTTP requests.
- **Scalability**: Parallel `Flux` for collaborator fetches to avoid sequential bottlenecks.
- **Error Handling**: 403 on collaborators logged and skipped; other errors propagated.
- **Permissions Mapping**: GitHub's `permissions` object (admin/push/pull) mapped to roles.
- **Aggregation**: Builds user-to-repos map from all accessible repo collaborators.

## Assumptions and Design Decisions

- **Token Permissions**: Assumes PAT has `repo` and `read:org` scopes for full access.
- **Organization Access**: User must be member/admin of the org or have public repo access.
- **API Limits**: GitHub rate limits (5000/hour authenticated) are respected; no explicit throttling implemented.
- **Permissions**: Only collaborator permissions considered; ignores teams/roles for simplicity.
- **Error Resilience**: 403 Forbidden on repos skips them (logged) to continue partial report.
- **Data Freshness**: No caching; real-time API calls.
- **Security**: Tokens not stored in code; environment variables preferred.

## Scale Considerations

- **Large Orgs**: Designed for 100+ repos and 1000+ users via parallel processing.
- **API Efficiency**: Reactive calls minimize blocking; Flux merges results.
- **Rate Limits**: Authenticated requests allow higher limits; consider pagination for 1000+ repos.
- **Performance**: WebClient handles concurrency; JVM tuning may be needed for very large responses.
- **Production**: Add caching (Redis), retry logic, and monitoring for high-volume use.

## Troubleshooting

- **403 Forbidden**: Check token scopes and org membership.
- **401 Unauthorized**: Verify token validity and format.
- **Empty Report**: All repos may be inaccessible; check logs for 403 messages.
- **Build Errors**: Ensure Java 17 and Maven are installed.
- **Eclipse Issues**: Update Maven project (`Alt+F5`) and check JRE.

## Contributing

1. Fork the repo.
2. Create a feature branch.
3. Commit changes.
4. Push and create a PR.


---

**Repository**: https://github.com/Shankarraospatil/github-access-report
**Author**: Shankarrao Patil
**Date**: March 2026
