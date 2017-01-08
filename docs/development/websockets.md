# Working with WebSockets

### Overview

Wombats uses websockets' full-duplex communication channels to pass game information to the clients, and to enable chat to enhance the in game experience.

### Tech Overview

Since this is a Clojure project, our sockets encourage the use of EDN, however during the initial handshake, you may request sessions to transmit in JSON.

### Process

- Client sends initial connection request to the server.
- Server saves the channel id and channel to in memory state and responds to the channel with

    ```clj
    {:meta {:msg-type :handshake}
     :payload {:chan-id 1234}}
    ```

- Client then sends back to the server identifying information

    ```clj
    {:meta {:msg-type :handshake
            :chan-id 1234}
     :payload {:user-id 5678
               :game-id 4321}}
    ```

- The server now associates the appropriate user / game information with the correct channel.
- All subsequent client messages must contain the following

  ```clj
  {:meta {:msg-type MESSAGE_TYPE
          :chan-id CHANNEL_ID}
   :payload MESSAGE_PAYLOAD}
  ```
