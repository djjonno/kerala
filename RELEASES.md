#### Release Roadmap 📦

##### 0.1.0-alpha

- Distributed + Standalone deployment configurations
- Topics (create-topic, read-topics)
- Produce to Topics (produce-topic)
- Consume from Topics (consume-topic)
- `in-memory` log storage engine

##### 0.2.0-alpha

- Broker service (describe-topics)
- elkdctl tool *(this provides simple interaction with elkd node from the command line)*
    - `> elkdctl --broker <broker-host> cluster-info`
    - `> elkdctl --broker <broker-host> create-topic <topic-namespace> [--log-engine <engine>]`
    - `> elkdctl --broker <broker-host> read-topics`
    - `> elkdctl --broker <broker-host> produce <topic-namepsace> [--poll ms]`
    - `> elkdctl --broker <broker-host> consume`

##### 0.3.0-alpha

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

##### 0.4.0-alpha

- `file` log storage engine

##### 0.4.0-alpha

- Dynamic cluster configuration
    - clients will have the ability to add/remove nodes from the cluster at runtime

##### backlog

- Auth
- Additional support for Dynamic cluster configuration
    - discover service – bootstrap cluster via `https://discovery.elkd.io/new?size=3`
