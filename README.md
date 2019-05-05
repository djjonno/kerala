# elkd 🦌

[![Build Status](https://img.shields.io/travis/com/elkd/elkd/master.svg)](https://travis-ci.com/elkd/elkd)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/mockito/mockito/blob/master/LICENSE)

elkd is a distributed streaming platform built in the modern day, from absolute scratch.  It is robust, lightweight and fast.
- **Pubsub** - Create a topic, write to a topic, consume from a topic. 
- **Process** - Write scalable stream processing logic that process events in realtime.
- **Distribute** - Write events and distribute them reliably across all cluster consumers.

### Features
- Produce and consume from topics
- Rich Processing API (functional friendly) to process events in realtime, publish result to a new stream.
- The underlying datastore is an in-memory append-only log; elkd does not store events **(although we may do this is in the future)** it's main priority is fast routing and processing of events in a distributed fashion.
- Consensus algorithm is built with Raft so it's familiar to developers but incredibly robust.

### Get Started
We haven't setup our release pipeline yet so this is how you can run us for the time being.

#### Build a jar
```bash
$ gradlew build installDist
```
You will find the `elkd-server` executable here: `core/build/install/elkd-server/bin`

#### Run with Docker
To create a elkd cluster with 4 nodes, run the docker-compose configuration:
```bash
$ docker-compose up
```

#### Run tests
```bash
$ ./gradlew build
```
---

#### Docs
- [client docs (wip)](https://github.com/elkd/elkd/wiki/Client-Docs)
- [server-docs (wip)](https://github.com/elkd/elkd/wiki/Server-Docs)

*an open source project by [@djjonno](https://github.com/djjonno)*
