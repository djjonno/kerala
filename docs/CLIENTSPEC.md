### Client Spec (evolving API)

#### Client Ctl Commands

The Ctl Command interface provides clients a means to query information about the server or perform operations like creating or deleting Topics.

To send a Ctl Command, establish a connection with the `ClientService/ClientCommand` service with the following request:

```proto
message RpcCommandRequest {
    string command = 1;
    repeated RpcArgPair args = 3;
}

message RpcArgPair {
    string param = 1;
    string arg = 2;
}
```

`command` is a String that specifies the command you wish to run.
`args` is a list of arg pairs `param`/`arg` as arguments to the command you provided.

The following table documents the available commands.

> Note: The command support is inline with the offering of the `kerala-ctl` tool.

| Command | Info | Args |
|---|---|---|
| `cluster` | Query information about the cluster. E.g who is the leader? | nil |
| `topics` | Query information about the Topics | nil |
| `create-topic` | Create a new topic. | `namespace` |
| `delete-topic` | Delete a topic. | `namespace` |

#### Producer

##### Producer Interaction

To allow the highest possible throughput over the producer connection, a bi-directional stream is established between client and server.

To produce to a Topic, the client needs to connect to the `ClientService/TopicProducer` service, sending an
 `RpcProducerRecord` containing production details:
 
```proto
message RpcProducerRequest {
    string topic = 1;
    repeated RpcKV kv = 2;
}
```

> Note: The act of a client sending an RpcProducerRecord is referred to as a **production**.

Once the server replicates this production, the server will respond to the client with a `RpcProducerResponse`:
  
```proto
message RpcProducerResponse {
    uint32 status = 1;
}
```
The `status` property corresponds to a Producer ACK code and has a specific reason.  See Producer ACK Codes for how to handle producer status codes.

> Note: If a client were to continue producing records w/out checking the ACK, the ordering guarantee of the KVs is lost.

#### Consumer

##### Consumer Interaction

Same as the producer, the consumer establishes a bi-directional stream with the server.  This setup provides a low
-latency, low-bandwidth channel for fast data transfer.

As a simplification to the consumer architecture, we opted for Kerala Consumers to be poll-based as opposed to push-based.  With push-based mechanisms, the server must keep track of each consumer and their location of consumption for a given Topic.  It must also take into account the downstream state e.g did the consumer receive the data, is the consumer able to process the data, am I pushing too much, am I pushing too little?  These are the questions that the server needs to keep in check and leads to a brittle and complex system.

Poll-based system alleviates a lot of these challenges. Firstly, the server does not need to keep track of anything pertaining to the consumer – it is ultimately kept stateless.  The consumer is the one who decides its location in the Topic, it decides what to do on failure, and it will ask for the next Topic data only when it's ready.  To further improve the performance of a poll-based mechanism, consumers are automatically configured to long-poll.  If a consumers asks for an index on the Topic that does not yet exist, the stream will be kept open and once the index does exist on the Topic, the data is immediately dispatched to the consumer waiting on the other end.  This system is much simpler and offers significantly better performance in both network and topic throughput.

#### Consumer API

To consume a Topic, the client needs to connect to the `ClientService/TopicConsumer`, sending an
 `RpcConsumerRequest` containing consumption details:

```proto
message RpcConsumerRequest {
    string topic = 1; // namespace of Topic to consume
    uint64 index = 2; // index/location to start consuming from
}
```

The consumer dictates its location within the Topic.  If the consumer wants to consumer everything on the Topic so far, it can specify a value of `0`.  If the consumer just wants to start consuming from the end of the Topic, it can leave the `index` unset e.g `null`.  The server will assume the next topic, and place the consumer into a `long-poll` state.  As a consumer, keep the connection established.  The server will respond with data as soon as it exists in the Topic.

```proto
message RpcConsumerResponse {
    string topic = 1;
    repeated RpcKV kvs = 3;
    uint32 status = 4;
}
```

`status` represents a Consumer ACK Code, see below for how to handle consumer status codes.

##### Consumer ACK Codes
| Code | Reason | Behavior |
|---|---|---|
| 0 | ok | Success. |
| 1 | generic error | Retry operation. |
| 2 | network error | Server crashed or network failed. Try again. |
| 3 | invalid operation | Operation is invalid. Server can return this on an internal server exception. |
| 4 | topic unknown | Create the topic via `create-topic` command prior to consumption. |

##### Producer ACK Codes
| Code | Reason | Behavior |
|---|---|---|
| 0 | ok | Success. |
| 1 | generic error | Retry operation. |
| 2 | network error | Retry operation. |
| 3 | invalid operation | Contact broker for capable production node and continue production to that node. |
| 4 | topic unknown | Create the topic via `create-topic` command prior to production. |
| 5 | operation timeout | Retry last message. |
