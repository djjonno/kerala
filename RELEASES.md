### Releases Roadmap 📦

#### [0.1.0-alpha](https://github.com/djjonno/kerala/releases/tag/0.1.0-alpha) Released

- Distributed + Standalone cluster configurations ✅
- Topics (`create-topic`, `delete-topic`, `read-topics`) ✅
- Produce to Topics (`produce-topic`) ✅
- `in-memory` log storage engine ✅
- Consume from Topics (`consume-topic`) ✅

#### 0.2.0-alpha (November ‘19)

- Client-Service contracts
- Broker service (expose `cluster-info` to clients)
- `keralactl` cli tool *(provides interaction with **Kerala** node from a command-line)*
    - `> kerala-ctl --broker <broker-host> cluster-info`
    - `> kerala-ctl --broker <broker-host> create-topic <topic> [--storage-engine <engine>]`
    - `> kerala-ctl --broker <broker-host> topic-info [<topic>]`
    - `> kerala-ctl --broker <broker-host> produce-topic <topic> [--timeout ms]`
    - `> kerala-ctl --broker <broker-host> consume-topic <topic> [--poll ms] [--timeout ms]`

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
        - basic api for stateless functional processing (map/reduce/group/filter/etc)
        - produce to stream
- Docs

#### 0.4.0-alpha

- `file` log storage engine

#### 0.5.0-alpha

- `snapshot` log compaction
- Production strategies
    - `full` - ensures KV is committed and replicated. 
    - `partial` - ensures KV is appended but not committed (could be replaced).  This provides more production throughput where the data is not as critical.

#### 0.6.0-alpha

- TLS authentication
    - server can be configured to use certificate pair(s) for authentication between clients and **Kerala** nodes. 
- Dynamic cluster configuration
    - clients will have the ability to add/remove nodes from the cluster at runtime   

#### Future Roadmap Items

- `Consumer` Groups
- stateful `Consumer` operations
    - windowing
    - joining
    - aggregations
