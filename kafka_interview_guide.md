# Apache Kafka — Interview Prep Guide

A deep-dive reference covering Spring Kafka configuration for Sagas, consumer lag, cross-cluster replication, producer `acks`, log segments, and a full producer/consumer config cheat sheet.

## Table of Contents

1. [Spring Kafka Properties for Saga Orchestration](#1-spring-kafka-properties-for-saga-orchestration)
2. [Consumer Lag: Diagnosis & Fixes](#2-consumer-lag-diagnosis--fixes)
3. [Replicating Data Across Kafka Clusters](#3-replicating-data-across-kafka-clusters)
4. [Producer `acks` Explained](#4-producer-acks-explained)
5. [Log Segments](#5-log-segments)
6. [Producer & Consumer Configuration Reference](#6-producer--consumer-configuration-reference)

---

# 1. Spring Kafka Properties for Saga Orchestration

For a Spring Boot 3.x + Kafka + Micrometer Tracing architecture, these properties control tracing propagation, reliability, and data integrity.

## 1.1 Tracing & Observability

These let Micrometer Tracing inject trace context into Kafka message headers and extract it across services, keeping distributed traces intact.

| Property | What it does |
|---|---|
| `spring.kafka.template.observation-enabled: true` | The `KafkaTemplate` (producer) records observations and injects tracing metadata (e.g., W3C `traceparent`) into record headers before publishing |
| `spring.kafka.listener.observation-enabled: true` | The `@KafkaListener` container (consumer) reads tracing headers from incoming messages and creates a child span, keeping logs connected |

## 1.2 Core Operational Properties

| Property | What it does |
|---|---|
| `spring.kafka.bootstrap-servers` | Comma-separated host/port list (e.g., `localhost:9092`) for the initial cluster connection |
| `spring.kafka.consumer.group-id` | Unique string identifying the consumer group. E.g., `payment-service` uses its own group ID so Kafka can distribute partitions and track offsets for payment processing |
| `spring.kafka.consumer.auto-offset-reset` | What to do when there's no initial offset (or it no longer exists). `earliest` → read from the beginning of the topic; `latest` → only new events from now on |

## 1.3 Reliability & Data Integrity (Crucial for Sagas)

Saga orchestrators need precise delivery guarantees so transactions aren't processed twice or dropped.

| Property | What it does / why it matters |
|---|---|
| `spring.kafka.producer.acks` | How many replicas must acknowledge a write. For financial or critical inventory updates, use **`all`** — the message is replicated across all in-sync replicas, preventing loss if a broker crashes |
| `spring.kafka.consumer.enable-auto-commit: false` | Disables automatic offset commits. A Saga worker should commit an offset **only after** business logic (e.g., a MySQL update) fully succeeds. Commit manually, or let the Spring container commit after execution, to avoid losing messages in a crash |

## 1.4 Production-Ready `application.yml`

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092

    # 1. Tracing propagation
    template:
      observation-enabled: true
    listener:
      observation-enabled: true

    # 2. Producer reliability
    producer:
      acks: all
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

    # 3. Consumer reliability
    consumer:
      group-id: inventory-service-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.example.saga.dto"
```

---

# 2. Consumer Lag: Diagnosis & Fixes

## What is consumer lag?

The gap between the **latest offset produced** to a partition and the **offset your consumer group has processed**. Growing lag means consumers can't keep up with incoming volume.

## Step 1: Diagnose before fixing

*"How do you identify consumer lag?"*

- Use `kafka-consumer-groups.sh --describe --group <group>`, or tools like Burrow, Kafka Manager, Confluent Control Center, or metrics piped into Splunk/Grafana.
- Check lag **per partition**, not just in aggregate — one slow partition can hide behind healthy ones.
- Determine whether lag grows **steadily** (systemic issue) or **spikes periodically** (traffic bursts).

## Step 2: Common causes & fixes

### 1. Not enough consumer parallelism
- **Cause:** fewer active consumers than partitions, so one consumer handles too much.
- **Fix:** scale consumers up to the partition count (one consumer per partition is the maximum useful parallelism in a group). If already maxed, increase the topic's partition count — this can affect ordering guarantees for existing keys, so plan carefully.

### 2. Slow per-message processing
- **Cause:** expensive synchronous work per message — blocking DB call, external API call, heavy computation inside the listener.
- **Fix:**
  - Move non-critical work (analytics, notifications) to async processing or a separate downstream topic.
  - **Batch operations** — instead of one DB write per message, batch writes using `max.poll.records`.
  - **Cache** frequently looked-up data (e.g., Redis) to avoid repeated DB round-trips.

### 3. Poor batch/poll configuration
- **Cause:** defaults not tuned for your throughput — `max.poll.records` too low/high, or `max.poll.interval.ms` too short, causing rebalances mid-processing.
- **Fix:** tune `fetch.min.bytes`, `max.poll.records`, and `max.poll.interval.ms` to actual per-batch processing time.

### 4. Frequent consumer rebalances
- **Cause:** consumers dropping out (crashes, processing exceeding `max.poll.interval.ms`, deployments) trigger rebalances that pause the whole group.
- **Fix:** increase `max.poll.interval.ms` if processing legitimately takes longer; use incremental cooperative rebalancing (`CooperativeStickyAssignor`) instead of the default eager strategy to reduce pause impact.

### 5. Producer throughput exceeds consumer capacity
- **Cause:** an upstream traffic spike outpaces steady-state consumer capacity.
- **Fix:** autoscale consumers on lag metrics (commonly **KEDA** in Kubernetes), or apply backpressure/throttling upstream.

### 6. GC pauses / resource starvation
- **Cause:** JVM garbage collection pauses or CPU/memory starvation on the consumer pod.
- **Fix:** tune JVM heap/GC settings, set adequate Kubernetes resource limits, and correlate OOMs or long GC pauses with lag spikes.

## Sample interview answer

> "I treat consumer lag as a signal to investigate at the partition level first, not just the aggregate group level — one hot or skewed partition can hide behind otherwise healthy ones. Once I identify the pattern — steady growth versus periodic spikes — I look at whether it's a parallelism issue (not enough consumers for the partition count) or a processing-time issue (a blocking call inside the consumer loop).
>
> For example, if a consumer was doing a synchronous DB lookup per message, I'd offload it to an async path or introduce caching to cut per-message latency. I also make sure offset commits are manual and only happen after successful processing — combined with `max.poll.interval.ms` tuning — so I'm not triggering unnecessary rebalances that pause the whole group.
>
> If the root cause is genuinely higher throughput than the current consumer count can handle, I'd scale consumers to match partition count, and if that still isn't enough, increase partitions — keeping in mind that changes ordering guarantees for a key going forward, so it has to be a deliberate decision, not a reflexive fix."

---

# 3. Replicating Data Across Kafka Clusters

## Why replicate across clusters?

Disaster recovery, multi-region/multi-datacenter deployments, migrating from on-prem to cloud, or isolating workloads (e.g., replicating production data to a staging cluster).

## Primary tool: Kafka MirrorMaker 2 (MM2)

MM2 is Kafka's built-in cross-cluster replication tool, built on **Kafka Connect**, and the standard answer to this question.

**How it works**

- Runs as a Kafka Connect cluster with a `MirrorSourceConnector` and `MirrorCheckpointConnector`.
- Reads topics from a source cluster and replicates to a target cluster, preserving partitioning, offsets (via **offset translation**), and topic configs.
- Supports **active-passive** (one-way, common for DR) and **active-active** (bidirectional, common for multi-region) topologies.

**Key features**

- **Topic renaming:** by default replicated topics get the source cluster alias as a prefix (e.g., `sourceCluster.orders`) to avoid naming collisions in active-active setups.
- **Offset translation:** MM2 keeps a mapping between source and target offsets via internal checkpoint topics, so consumers can fail over and resume roughly where they left off (translated equivalents, not identical offsets).
- **Consumer group replication:** group offsets can be replicated too, so a failover doesn't restart every topic from the beginning.

## Other options

| Tool | Notes |
|---|---|
| **Confluent Replicator** | Confluent's commercial alternative to MM2; more polished tooling on Confluent Platform |
| **uReplicator (Uber)** | Older alternative with better rebalancing behavior; less common now that MM2 has matured |
| **Custom producer-consumer bridge** | Consume from cluster A, produce to cluster B. What MM2 does under the hood — rarely worth building yourself |

## Points that show depth

1. **Ordering / exactly-once caveats:** cross-cluster replication doesn't guarantee exactly-once end to end by default — duplicates are possible on failover, so target-side consumers should be **idempotent**.
2. **Replication lag monitoring:** monitor lag between clusters just like consumer lag — it determines your actual **RPO (recovery point objective)** in a DR scenario.
3. **Topic config sync:** partition counts, retention settings, and ACLs must be kept in sync or explicitly configured. MM2 can sync configs, but it has to be set up deliberately.

## Sample interview answer

> "The standard way to replicate data between Kafka clusters is MirrorMaker 2, which runs on top of Kafka Connect. It reads from a source cluster and writes to a target, and depending on the use case you'd set it up as active-passive — for disaster recovery, where the target is a standby — or active-active for multi-region setups where both clusters serve traffic.
>
> A few things I'd pay attention to: MM2 does offset translation rather than copying identical offsets, since source and target don't share the same offset numbering — so consumer groups can resume in the right place after failover, but it's translated, not identical. I'd also treat target-side consumers as needing to be idempotent, since replication doesn't guarantee exactly-once delivery, especially around a failover.
>
> And just like consumer lag within a single cluster, I'd monitor replication lag between clusters, since that tells you your real recovery point objective — how much data you could lose if the source cluster went down right now."

---

# 4. Producer `acks` Explained

## What is `acks`?

A producer setting that controls **how many broker replicas must confirm receipt** of a message before the write is considered successful. It's the durability vs. latency/throughput trade-off knob.

## The three values

| Value | Behavior | Trade-offs |
|---|---|---|
| **`acks=0`** | Producer doesn't wait for any acknowledgment — fire and forget | Fastest, but no durability guarantee; the producer can't know if the message was lost. Only for cases where occasional loss is acceptable (e.g., high-volume metrics/logs) |
| **`acks=1`** (default in older Kafka versions) | Waits for the **leader** replica only | Middle ground; if the leader crashes before followers replicate the message, it's lost |
| **`acks=all`** (or `-1`) | Waits for **all in-sync replicas (ISRs)** | Strongest guarantee — a message is committed only when every ISR has it, so another ISR can take over without loss. Slower due to extra round-trips; use when data loss is unacceptable |

## How it connects to other configs

`acks=all` alone isn't the full durability story:

- **`min.insync.replicas`** — the minimum number of ISRs that must acknowledge for a write to succeed. With `acks=all` but `min.insync.replicas=1`, you get little extra protection. A common production combination:
  - **replication factor 3 + `min.insync.replicas=2` + `acks=all`** → tolerates one broker failure with zero data loss.
- **`enable.idempotence=true`** — pairs with `acks=all` to prevent duplicate writes on retries. Kafka **requires** `acks=all` when idempotence is enabled.

## Sample interview answer

> "`acks` controls how many replicas must confirm a write before the producer treats it as successful — it's essentially Kafka's durability dial. `acks=0` doesn't wait for confirmation, so it's fastest but you can silently lose messages. `acks=1` waits for just the leader — a reasonable middle ground, but there's still a window where you could lose data if the leader crashes after acknowledging but before followers replicate.
>
> I used `acks=all`, which waits for all in-sync replicas, so a message is only committed once it's safely replicated. I paired it with `enable.idempotence=true`, since Kafka requires `acks=all` for idempotence — together they gave durability plus protection against duplicate writes from retries.
>
> `acks=all` alone isn't the complete picture — it works with `min.insync.replicas`. With replication factor 3 but `min.insync.replicas=1`, you're not getting much extra protection. For critical data I'd use replication factor 3 with `min.insync.replicas=2`, which tolerates one broker going down with zero message loss."

---

# 5. Log Segments

## What is a log segment?

Each Kafka partition is stored on disk as an **append-only commit log**. Instead of one giant file, Kafka splits it into smaller chunks called **log segments** — think of the partition as a book and each segment as a chapter.

## Why segments instead of one big file?

1. **Efficient deletion/retention:** Kafka deletes data by retention policy (time or size). Rather than rewriting a huge file to drop old messages, it deletes the whole expired segment file — a cheap, atomic OS-level operation.
2. **Efficient reads:** smaller files are faster to search, index, and cache in the OS page cache.
3. **Manageable file sizes:** a single unbounded file would be unwieldy for the filesystem and for operations like compaction.

## What files make up a segment?

Each segment is a set of files sharing a **base offset** as their filename (e.g., `00000000000000368769`):

| File | Purpose |
|---|---|
| `.log` | The actual message data — the append-only file containing the records |
| `.index` | **Offset index** — maps message offsets to physical byte positions in the `.log`, enabling fast lookups without scanning the whole file |
| `.timeindex` | **Time index** — maps timestamps to offsets for lookups like "messages after this timestamp" (used by the `offsetsForTimes` API) |

## How segments are created and rolled

- Kafka writes to the **active segment** (the newest one) of each partition.
- A new segment is **rolled** when either:
  - `log.segment.bytes` is reached (default **1 GB**), or
  - `log.roll.ms` / `log.roll.hours` is reached (default **7 days**), even if not full.
- Only the active segment is writable; older segments are read-only.

## Ties to retention & compaction

- **Time/size-based retention** (`log.retention.hours`, `log.retention.bytes`): Kafka evaluates closed segments as a whole — if a segment's newest message is older than the retention period, the entire file is deleted.
- **Log compaction** (`cleanup.policy=compact`): also works segment by segment — the cleaner thread processes older closed segments, keeping only the latest value per key, and writes a new compacted segment to replace them.

## Sample interview answer

> "A Kafka partition's data on disk isn't one continuous file — it's broken into log segments. Each has a `.log` file with the message data, plus `.index` and `.timeindex` files that let Kafka quickly find an offset or timestamp without scanning the whole file.
>
> Only one segment per partition — the active segment — is open for writes at a time. New segments are rolled when the current one hits `log.segment.bytes` or `log.roll.ms`, whichever comes first.
>
> This matters because it makes retention and compaction efficient. Kafka doesn't rewrite a giant file to strip old messages; it deletes whole segment files once everything in them is older than the retention window. Compaction likewise processes and rewrites segments rather than the entire log, which is how Kafka handles high-throughput, long-retention topics without cleanup becoming a bottleneck."

---

# 6. Producer & Consumer Configuration Reference

## Producer configurations

| Property | Meaning |
|---|---|
| `bootstrap.servers` | Initial broker `host:port` list used to discover the full cluster |
| `acks` | Durability control — `0` (no wait), `1` (leader only), `all`/`-1` (all in-sync replicas) |
| `key.serializer` / `value.serializer` | Converts Java key/value objects into bytes (e.g., `StringSerializer`, `JsonSerializer`) |
| `enable.idempotence` | When `true`, prevents duplicates from producer retries via per-partition sequence numbers. Requires `acks=all` |
| `retries` | Number of retries on failed sends (transient broker errors). Effectively "retry until timeout" by default in modern Kafka |
| `retry.backoff.ms` | Wait between retry attempts |
| `max.in.flight.requests.per.connection` | Unacknowledged requests allowed before blocking. Must be ≤ 5 to preserve ordering when idempotence is enabled |
| `batch.size` | Max bytes batched per partition before sending — larger batches improve throughput at the cost of latency |
| `linger.ms` | How long to wait to accumulate more messages into a batch, even if `batch.size` isn't reached — trades a little latency for throughput |
| `compression.type` | Compresses batches (`gzip`, `snappy`, `lz4`, `zstd`) — less network/disk usage at some CPU cost |
| `buffer.memory` | Total memory the producer can use to buffer unsent messages; if exceeded, `send()` blocks or throws depending on `max.block.ms` |
| `partitioner.class` | Chooses the partition when none is set — default hashes the key (sticky partitioner for null keys) |
| `request.timeout.ms` | Max wait for a broker response before treating the request as failed |
| `delivery.timeout.ms` | Upper bound on total time from `send()` to success or failure, including retries |
| `transactional.id` | Enables exactly-once semantics across partitions/topics via Kafka transactions (`initTransactions()`, `beginTransaction()`, `commitTransaction()`) |

## Consumer configurations

| Property | Meaning |
|---|---|
| `bootstrap.servers` | Initial broker list (same as producer) |
| `group.id` | Identifies the consumer group — consumers sharing a `group.id` split partitions among themselves |
| `key.deserializer` / `value.deserializer` | Converts bytes back into Java objects (inverse of the serializer) |
| `enable.auto.commit` | If `true`, offsets are committed on an interval regardless of whether processing succeeded — risk of message loss on crash. Use `false` with manual commits for reliability |
| `auto.commit.interval.ms` | How often auto-commit fires, if enabled |
| `auto.offset.reset` | Behavior when there's no committed offset: `earliest`, `latest`, or `none` (throw an exception) |
| `max.poll.records` | Max records returned per `poll()` — controls processing batch size |
| `max.poll.interval.ms` | Max time between `poll()` calls before the consumer is considered dead and a rebalance triggers — tune when processing is slow |
| `session.timeout.ms` | How long the broker waits without a heartbeat before declaring the consumer dead and rebalancing |
| `heartbeat.interval.ms` | Heartbeat frequency to the group coordinator (roughly 1/3 of `session.timeout.ms`) |
| `fetch.min.bytes` | Minimum data the broker should have before answering a fetch — lets the consumer batch more per request |
| `fetch.max.wait.ms` | Max time the broker waits to satisfy `fetch.min.bytes` before responding anyway — bounds latency |
| `partition.assignment.strategy` | Partition assignment algorithm: `RangeAssignor`, `RoundRobinAssignor`, `StickyAssignor`, or `CooperativeStickyAssignor` (reduces rebalance disruption) |
| `isolation.level` | `read_committed` vs `read_uncommitted` — whether the consumer sees messages from in-progress/aborted transactions (relevant with transactional producers) |
| `client.id` | Identifies the consumer instance in logs/metrics for debugging |

## Sample interview framing

> "On the producer side, the configs I paid most attention to were `acks` and `enable.idempotence` for durability, `linger.ms` and `batch.size` for throughput tuning, and `retries` with `delivery.timeout.ms` to handle transient failures without silently dropping messages.
>
> On the consumer side, the most important ones were `enable.auto.commit` — which I disabled in favor of manual commits — `max.poll.interval.ms` and `session.timeout.ms`, which I tuned to avoid unnecessary rebalances when processing took longer than the defaults expected, and `auto.offset.reset`, since choosing `earliest` vs `latest` determines whether a new consumer replays history or only sees new messages."
