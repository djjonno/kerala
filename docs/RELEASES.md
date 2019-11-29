### Releases Roadmap 📦

#### [0.1.0-alpha](https://github.com/djjonno/kerala/releases/tag/0.1.0-alpha) Released

- Distributed + Standalone cluster configurations ✅
- Topics (`create-topic`, `delete-topic`, `read-topics`) ✅
- Produce to Topics (`produce-topic`) ✅
- `in-memory` log storage engine ✅
- Consume from Topics (`consume-topic`) ✅

#### [0.2.0-alpha](https://github.com/djjonno/kerala/releases/tag/0.2.0-alpha) Released

- Client-Service contracts ✅
- Broker service (expose `cluster` to clients) ✅
- `kerala-ctl` cli tool *(provides interaction with **Kerala** node from a command-line)* ✅
    - `> kerala-ctl --broker <broker-host> cluster` ✅
    - `> kerala-ctl --broker <broker-host> topics` ✅
    - `> kerala-ctl --broker <broker-host> create-topic <topic>` ✅
    - `> kerala-ctl --broker <broker-host> console-producer <topic>` ✅
    - `> kerala-ctl --broker <broker-host> console-consumer <topic> [-i,--index <index>]` ✅

#### 0.3.0-alpha January 2020

- Kotlin client
    - Standard client configuration
        - bootstrap broker
        - timeout
    - Serializers/Deserializers
        - std serializers/deserializers for primitive types (ints, longs, floats, doubles, strings, 
        - produce/consume Objects
    - Producer
        - max message size
        - error handling
        - std serializers/deserializers for primitive types
    - Consumer
        - process streams
        - basic api for stateless functional processing (map/reduce/filter/etc)
        - topological processing
        - produce to stream
- Client Docs

#### 0.4.0-alpha

- stateful `Consumer` operations
    - windowing
    - joining
    - aggregations
- `produce` strategies
    - `full` - ensures KV is committed and replicated. 
    - `partial` - ensures KV is appended but not committed (could be replaced).  This provides more production throughput where the data is not as critical.

#### 0.5.0-alpha

- `file` log storage engine
- log reduction schemes

#### 0.6.0-alpha

- TLS authentication
    - server can be configured to use certificate pair(s) for authentication between clients and **Kerala** nodes. 
- Dynamic cluster configuration
    - clients will have the ability to add/remove nodes from the cluster at runtime   

#### Future Roadmap

- `Consumer` Groups
