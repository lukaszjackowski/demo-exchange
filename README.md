

# Exchange Engine Simulation

A simplified exchange matching engine built with **Spring Boot**, and the **LMAX Disruptor** framework.

## 1. Project Objective
The goal of this project was to design and implement a simplified exchange engine simulation. The focus was on architectural patterns used in low-latency financial systems to handle high-throughput order processing while maintaining strict data integrity and consistency.

## 2. Requirements
The engine is built upon two non-negotiable principles:
* **FIFO (First-In-First-Out):** Orders are processed strictly in the order they are received. By using a single-threaded consumer model for the matching logic, we eliminate race conditions and ensure fair execution.
* **Determinism:** The system is deterministic. Given the same initial state and the same input sequence of events, the system will always produce the exact same output state. This allows for reliable recovery.

## 3. Technical Stack & Approach
* **LMAX Disruptor:** Chosen as the core execution engine. It provides a lock-free **Ring Buffer** that acts as the "Single Source of Truth" for event sequencing.
* **PostgreSQL Journaling:** Every order is persisted to a SQL journal. While a relational DB provides ACID guarantees, in a production HFT environment, this would typically be replaced by an append-only binary log.
* **Spring Boot:** Acts as the foundational framework and "orchestrator." It manages the application lifecycle, Dependency Injection (DI), provides the REST API layer, and simplifies integration with the database and the Disruptor engine.

## 4. Current Implementation Status
- [x] **Matching Engine:** Core logic to match BUY and SELL orders within the Disruptor event loop.
- [x] **Journaling:** Persistence layer using Postgres to capture the event stream.
- [x] **Error Handling:** Basic resilience for DB persistence to prevent Disruptor thread termination.
- [x] **REST API:** Spring Boot entry point for order submission.
- [x] **State Recovery:** Naive replay mechanism that reconstructs the in-memory Order Book from the journal upon startup.
- [x] **Integration Testing:** End-to-end verification from API call to matching results.

## 5. Next Steps
- [ ] **Snapshotting:** Implement periodic state persistence to reduce recovery time (replaying from the last snapshot instead of the beginning of time).
- [ ] **Advanced Error Handling:** Integration of **Resilience4j Circuit Breakers** and Retries to protect the engine from cascading failures.
- [ ] **Observability:** Adding Micrometer/Prometheus metrics for throughput, latency, and Ring Buffer saturation.
- [ ] **Performance Benchmarking:** Identifying bottlenecks using **Gatling**.