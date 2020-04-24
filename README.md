# Chat-Room-using-Java
we will implement a network with socket programming using server- client architecture.
We have a single server that can handle requests from two different types of clients Admin Client and Member Client.

## Getting Started
1. The server has two groups (i.e. two chat groups). The communication between clients in any group is handled by the server. 

2. The server offers two services: SUBSCRIBE (for Member Clients only) and BROADCAST (for Admin Clients only). 

3. A Member Client can subscribe to one of the two groups. If a Member Client subscribes to a group, then they will receive all messages sent in this group. However, they can not
receive any messages sent in the other group. 

4. A Member Client is NOT allowed to send any messages in the group. They just listen to the received announcements. 

5. An Admin Client is the only client that can broadcast messages to a group. 

6. If an Admin Client broadcasts a message to a group, then all the Member Clients in this
group ONLY must receive this message. 

7. An Admin Client does not subscribe to a group. They just broadcast their messages to one of the two groups. Therefore, if an Admin Client decides to broadcast a message to
all Member Clients subscribed to a group, the admin will not receive the message himself. 

8. The server must handle multiple requests from Member Clients at the same time. For example, the server can serve 3 Member Clients in Group 1 and 2 Member Clients in
Group 2 at the same time. 

9. The server must handle mutliple requests from Admin Clients at the same time. For example, the server can serve 2 Admin Clients broadcasting to Group 1 and one admin
client broadcasting to Group 2. 

10. If two admins are broadcasting to the same group at the same time, then the Member Clients of this group will receive the messages from both admins. However, the two
admins will not see each others’ messages. 
11. The server provides the service SUBSRIBE at port 3000, and provides the service BROADCAST at port 3001.

###  Details of the SUBSCRIBE Service:
### A) Communication between Server and Member Client (MC):
