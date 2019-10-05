#### Roadmap

Ground work for topic production/consumption
- [x] add topic client validation / check if topic already exists
- [ ] harden thread model of topic consensus (synchronizing component read/writes, synchronizing the LogFacade)
- [ ] harden command execution components (abstractions don't really make sense)
- [ ] create cli client (create-topic)
- [ ] deprecate NotificationCenter, replace with Kotlin Observables

Clients (Kotlin client)
- [ ] implement basic produce/consume API
- [ ] kv serialization
    - kv fields are opaque, and are simply byte data.
- [ ] design stream API
    - functional APIs (mapping/folds/filtering/grouping/windowing)
    - experiment with a Kotlin DSL

Extended Topic Configurations
- Allow clients to specify replication factor

Alpha
- [ ] Bugs
- [ ] documentation / base documentation website off github repo / CI for master changes
- [ ] hello world service to interact w/ on docs website
