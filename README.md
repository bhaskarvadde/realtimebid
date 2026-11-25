# RealTimeBid: Real-Time Auction Platform

A full-stack auction platform where users list items and place bids, and every viewer sees new bids instantly. The main engineering focus is correctness under concurrency: many users can bid on the same item at the same moment without lost updates.

| | |
|---|---|
| **Live app** | https://realtimebid.vercel.app/ |
| **API** | https://realtimebid.onrender.com |
| **Postman collection** | [`Docs/RealTimeBid.postman_collection.json`](docs/RealTimeBid.postman_collection.json) |

> The API runs on a free tier and sleeps when idle. The first request after a pause can take up to a minute.

## Features

- Register and log in with JWT authentication (BCrypt password hashing, stateless sessions)
- Create auctions with a title, description, image URL, starting price, and start and end times
- Browse live, scheduled, and ended auctions with filters and search
- Place bids with strict rules: a bid must beat the current highest bid (or the starting price), and sellers cannot bid on their own auctions
- **Concurrency-safe bidding** using optimistic locking with automatic retry
- **Live updates** over WebSocket (STOMP): new bids and auction results appear without refreshing
- **Anti-sniping:** a bid in the final 2 minutes extends the auction to 2 minutes from that bid
- A scheduled job activates auctions at their start time, closes them at their end time, and records the winner
- Personal dashboard: my bids (leading or outbid), my auctions, and won auctions
- Validation and consistent JSON errors (400, 401, 404, 409)

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot, Spring Security, Spring Data JPA (Hibernate), Bean Validation |
| Auth | JWT (jjwt), BCrypt |
| Real-time | WebSocket with STOMP |
| Database | MySQL-compatible (TiDB Cloud in production, MySQL locally) |
| Frontend | React (Vite), React Router, `@stomp/stompjs` |
| Testing | JUnit 5, Mockito, a multithreaded concurrency test |
| Deployment | Render (Docker), Vercel, TiDB Cloud |

## Architecture

```
React (Vercel)  --REST + JWT-->  Spring Boot (Render)  -->  TiDB / MySQL
      |                                |
      +------ WebSocket (STOMP) -------+   /ws   topic: /topic/auctions/{id}
```

The backend is a layered monolith organized by feature (`user`, `auction`, `bid`): Controller, Service, Repository. DTOs are used at every API boundary, so entities never leave the server.

## Database design

![ER diagram](docs/RealTimeBid-ER-Diagram.png)

- `users`: one row per account (email is unique, password is a BCrypt hash)
- `auctions`: belongs to a seller, optionally has a winner, and carries `version` (optimistic locking) and `current_highest_bid`
- `bids`: belongs to one auction and one bidder
- Money is stored as `DECIMAL(10,2)` and handled with `BigDecimal`

## How concurrent bidding stays correct

Two bids on the same auction can read the same highest bid and both pass validation. Without protection, the later write silently overwrites the earlier one (a lost update).

`Auction` has a `@Version` column. Every update includes `AND version = ?`; if another transaction changed the row first, zero rows match and Hibernate throws `OptimisticLockingFailureException`. An outer retry loop (up to 3 attempts, outside the `@Transactional` method so each attempt gets a fresh transaction and a fresh read) re-runs the bid. The WebSocket broadcast is sent only after the transaction commits.

A multithreaded test (10 threads bidding on one auction) checks the final highest bid.

## API overview

All routes are prefixed with `/api/v1`. Protected routes need `Authorization: Bearer <token>`.

| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/auth/register` | Public | Create an account |
| POST | `/auth/login` | Public | Returns a JWT |
| GET | `/users/me` | JWT | Current user profile |
| POST | `/auctions` | JWT | Create an auction |
| GET | `/auctions` | Public | Active auctions |
| GET | `/auctions/all` | Public | All auctions, newest first |
| GET | `/auctions/mine` | JWT | Auctions I created |
| GET | `/auctions/won` | JWT | Auctions I won |
| GET | `/auctions/{id}` | JWT | One auction |
| GET | `/auctions/{id}/bids` | JWT | Bid history |
| POST | `/bids` | JWT | Place a bid |
| GET | `/bids/mine` | JWT | My bids |

WebSocket: connect to `/ws`, subscribe to `/topic/auctions/{id}`.

Error format: `{ "timestamp": "...", "status": 400, "error": "Bad Request", "message": "..." }`

## Run locally

**Prerequisites:** Java 21+, Maven, Node 20+, MySQL 8.

1. Create the database: `CREATE DATABASE realtimebid;`
2. Set environment variables for the backend:

| Variable | Example | Required |
|---|---|---|
| `DB_USERNAME` | `root` | Yes |
| `DB_PASSWORD` | `your-password` | Yes |
| `DB_URL` | `jdbc:mysql://localhost:3306/realtimebid` | No (this is the default) |
| `JWT_SECRET` | a random string of 32+ characters | Yes outside local dev |
| `CORS_ORIGINS` | `http://localhost:5173` | No (this is the default) |

3. Start the backend: `mvn spring-boot:run` (runs on port 8080)
4. Start the frontend:
```bash
cd frontend
npm install
npm run dev
```
5. Open `http://localhost:5173`

## Tests

```bash
mvn test
```
The tests need the database and the environment variables above. They cover the bidding rules (bid too low, self-bidding, anti-sniping window, valid bid), winner selection, and a concurrent-bidding scenario.

## Deployment

- **Database:** TiDB Cloud (MySQL-compatible), TLS required
- **Backend:** Render, built from the Dockerfile
- **Frontend:** Vercel (Vite build), with `vercel.json` rewriting all routes to `index.html`

Backend environment variables on Render: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ORIGINS` (the Vercel URL).
Frontend environment variables on Vercel: `VITE_API_URL`, `VITE_WS_URL`.

## Screenshots

_Add screenshots here: home page, live auction page, dashboard._

## Known limitations

- The product image is a URL, not an uploaded file
- Date and time values have no timezone (`LocalDateTime`); the server runs in `Asia/Kolkata`
- The JWT is stored in `localStorage` (readable by scripts if the page has an XSS bug)
- The WebSocket broker is the simple in-memory one and the scheduler polls every 30 seconds, so neither supports multiple server instances
- No frontend tests and no load testing yet

## Author

Bhaskar Vadde
