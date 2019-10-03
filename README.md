# elkd 🦌

[![Build Status](https://img.shields.io/travis/com/djjonno/elkd/master.svg)](https://travis-ci.com/djjonno/elkd)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/mockito/mockito/blob/master/LICENSE)

elkd is a distributed streaming platform built in the modern day, from absolute scratch.  It is robust, lightweight and fast.
- **Pubsub** - Define a topic, write to a topic, consume from a topic. 
- **Process** - Write scalable stream processing logic that process events in realtime.
- **Distribute** - Write events and distribute them reliably across all cluster consumers.

### Features
- Produce / Consume stream API
- Stream Processing API
- Topic Partitioning and Consumer Groups
- The underlying datastore is an in-memory append-only log; elkd does not persist events **(although we may do this is in the future)** it's main priority is fast routing and processing of events in a distributed fashion.
- Consensus algorithm is built with Raft so it's familiar to developers but incredibly robust.

### Get Started
We haven't setup our release pipeline yet so this is how you can run us for the time being.

#### Run build (runs tests) and produce executable jar
```bash
$ gradlew build installDist
```
You will find the `elkd-server` executable here: `core/build/install/elkd-server/bin`

#### Define a static cluster
Configuring 3 nodes in this example.
```bash
$ bin/elkd-server --cluster-set 0.0.0.0:9191,0.0.0.0:9292,0.0.0.0:9393 --port 9191
$ bin/elkd-server --cluster-set 0.0.0.0:9191,0.0.0.0:9292,0.0.0.0:9393 --port 9292
$ bin/elkd-server --cluster-set 0.0.0.0:9191,0.0.0.0:9292,0.0.0.0:9393 --port 9393
```

#### Run with Docker (recommended)
To create a elkd cluster with 4 nodes, run the docker-compose configuration:
```bash
$ docker-compose up
```

#### TODO

- [ ] add topic client validation / check if topic already exists
- [ ] harden thread model of topic consensus (synchronizing component read/writes, synchronizing the LogFacade)
- [ ] create command client (create-topic)
- [ ] deprecate NotificationCenter, replace with Kotlin Observables

---

*an open source project by [@djjonno](https://github.com/djjonno)*

