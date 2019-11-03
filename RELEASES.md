### Releases Roadmap 📦

#### [0.1.0-alpha](https://github.com/djjonno/ravine/releases/tag/0.1.0-alpha) Released

- Distributed + Standalone cluster configurations ✅
- Topics (`create-topic`, `delete-topic`, `read-topics`) ✅
- Produce to Topics (`produce-topic`) ✅
- `in-memory` log storage engine ✅
- Consume from Topics (`consume-topic`) ✅

#### 0.2.0-alpha (November ‘19)

- Client-Service contracts
- Broker service (expose `cluster-info` to clients)
- `ravine` cli tool *(provides interaction with **Ravine** node from a command-line)*
    - `> ravine --broker <broker-host> cluster-info`
    - `> ravine --broker <broker-host> create-topic <topic> [--storage-engine <engine>]`
    - `> ravine --broker <broker-host> topic-info [<topic>]`
    - `> ravine --broker <broker-host> produce-topic <topic> [--timeout ms]`
    - `> ravine --broker <broker-host> consume-topic <topic> [--poll ms] [--timeout ms]`

#### 0.3.0-alpha

- Kotlin client
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
    - server can be configured to use certificate pair(s) for authentication between clients and **Ravine** nodes. 
- Dynamic cluster configuration
    - clients will have the ability to add/remove nodes from the cluster at runtime   
