### Releases Roadmap 📦

#### [0.1.0-alpha](https://github.com/djjonno/elkd/releases/tag/0.1.0-alpha) Released

- Distributed + Standalone cluster configurations ✅
- Topics (`create-topic`, `delete-topic`, `read-topics`) ✅
- Produce to Topics (`produce-topic`) ✅
- `in-memory` log storage engine ✅
- Consume from Topics (`consume-topic`) ✅

#### 0.2.0-alpha (November ‘19)

- Client-Service contracts
- Broker service (expose `cluster-info` to clients)
- elkdctl tool *(this provides simple interaction with elkd node from the command line)*
    - `> elkdctl --broker <broker-host> cluster-info`
    - `> elkdctl --broker <broker-host> create-topic <topic> [--storage-engine <engine>]`
    - `> elkdctl --broker <broker-host> topic-info [<topic>]`
    - `> elkdctl --broker <broker-host> produce-topic <topic> [--timeout ms]`
    - `> elkdctl --broker <broker-host> consume-topic <topic> [--poll ms] [--timeout ms]`

#### 0.3.0-alpha

- Kotlin client
    - Establish benchmark for integrating with an elkd server
    - Standard client configuration
        - bootstrap broker
        - timeout
    - Producer
        - max message size
        - error handling
        - std serializers/deserializers for primitive types
        - custom serializers/deserializers
            - support for producing your own objects
    - Consumer
        - process streams
        - flexible api for functional processing (map/reduce/group/filter/etc)
        - produce to stream
- Docs

#### 0.4.0-alpha

- `file` log storage engine

#### 0.5.0-alpha

- `snapshot` log compaction

#### 0.6.0-alpha

- TLS authentication
    - server can be configured to use certificate pair(s) for authentication between clients and elkd nodes. 
- Dynamic cluster configuration
    - clients will have the ability to add/remove nodes from the cluster at runtime   
