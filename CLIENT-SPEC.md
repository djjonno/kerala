### Client Spec (evolving wip)

#### Producer

##### Producer Interaction

To allow the highest possible throughput over the producer connection, a 2-way stream is established between client and server.

Client will send an `RpcProducerRecord`.
```proto
message RpcProducerRecord {
    string topic = 1;
    repeated RpcKV kv = 2;
}
```

> Note: The act of a client sending an RpcProducerRecord is referred to as a **production**.

Once the server replicates this production, the server will respond to the client with a `RpcProducerAck`.  
```proto
message RpcProducerAck {
    uint32 notification = 1;
}
```
The `notification` property corresponds to a Producer ACK code and has a specific reason.  While the client is waiting for the ACK, it must not produce any further records.

> Note: If a client were to continue producing records w/out checking the ACK, the ordering guarantee of the KVs is lost.

Base the logic for the 

##### Producer ACK Codes
| Code | Reason | Behavior |
|---|---|---|
| 0 | ok | Success, send next production. |
| 1 | client error | Retry production. |
| 10 | invalid operation | Contact broker for capable production node and continue production to that node. |
| 11 | unknown topic | Create the topic via `create-topic` command prior to production. |
