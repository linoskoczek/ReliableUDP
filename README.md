# Reliable UDP Protocol


_**Requirements:**_ 
- local network
- Java Runtime Environment 8
- `java` as a location of java.exe file in Java Runtime Environment 8 directory

## Overview

This protocol is to be used instead of TCP in cases that you want to provide reliable data transfer. In particular, the
task was to create a file transfer protocol, which will transfer the file without any errors.

## Packet structure

|    ID   | CHECKSUM | COMMAND |  LENGTH |     DATA    |
|:-------:|:--------:|:-------:|:-------:|:-----------:|
| 2 bytes | 32 bytes | 3 bytes | 2 bytes |  <=1024 bytes |

- **ID** - id of a message
- **CHECKSUM** - checksum of a message
- **COMMAND** - commands for a client/server
- **LENGTH** - length of data
- **DATA** - content of a message

### ID

ID is a 2 byte _short_ number which is a unique information about a message. It's used in acknowledgements to indicate
that this concrete packet has been received successfully.

### CHECKSUM

CHECKSUM is an MD5 hash of ID, COMMAND and DATA.

In other words: `CHECKSUM = MD5(ID + COMMAND + DATA)`.

As full MD5 hash is used, it takes 32 bytes total in a packet.

### COMMAND

COMMAND is a 3 byte string which indicates what type of operation server/client is supposed to do.
Possible commands are:
- HAI - introducing yourself to a server/client. Server/client won't allow any other nodes to send/receive information, 
so only client and server are 'talking'. It will base it on IP and PORT of sender's/receiver's socket.
- FLI - sending file information to a server/client
- CNT - sending content of a file (in parts)
- FSF - information sent to the server that file has been sent completely
- PRP - ask to resend a message with a specific file part
- DSC - information about the willingness to disconnect from client/server
- ACK - confirmation about delivery of a packet

### LENGTH

LENGTH is simply length of DATA, so how many bytes are sent.

### DATA

Content of a message. Possible content for the commands:
- HAI:
    * ME CLIENT: used by client to introduce himself to server
    * ME SERVER: answer to client's welcome message
- FLI:

    * Information about the file separated by semicolon (`;`):
      
     `FILE NAME;FILE SIZE;NUMBER OF PARTS;MD5`
      
     Where:
     * FILE NAME: name of the file that is being sent
     * FILE SIZE: size of the file that is being sent
     * NUMBER OF PARTS: number of packages in which file content will be sent (by default it's `ceil(size/1024) + 1`)
     * MD5: MD5 checksum of a whole file
     
    * KK;_SPEED_ where speed stands for number of KB sent per one second
- CNT:
    File content with information about which part of file it is:
    `NUMBER OF FILE PART;FILE PART CONTENT`
- FSF:

    1 - information that file has been sent
- PRP:

    Number of file part that you didn't catch and want to get it again. 
- DSC:

    1 - information that you want to disconnect.
- ACK:
    ID - ID of a message that has been delivered
    
    
## Example of the protocol in action

Package parts will be divided by `|`. Checksums and lengths are omited on purpuse - you are not a computer to read that.

```
Server starts.
Client starts.
CLIENT> 0  | HAI | ME CLIENT
SERVER> 0  | ACK | 0
SERVER> 1  | HAI | ME SERVER
CLIENT> 1  | ACK | 1
CLIENT> 2  | FLI | abc.txt;2050;3;MD5
SERVER> 2  | ACK | 2
SERVER> 3  | FLI | KK;5
CLIENT> 3  | ACK | 3
suddenly, message 3 didn't arrive from a client! Server will resend the information:
SERVER> 4  | FLI | KK;5
CLIENT> 4  | ACK | 4
CLIENT> 5  | CNT | 1;sgsagsdgsgs...
SERVER> 5  | ACK | 5
CLIENT> 6  | CNT | 2;dsgsgsdgdsg...
SERVER> 6  | ACK | 6
CLIENT> 7  | CNT | 3;sgsdgsdagsd...
SERVER> 7  | ACK | 7
suddenly, something went wrong on server, and he somehow ignored 2nd package and want to move a bit back
CLIENT> 8  | CNT | 4;dsafassadfs...
SERVER> 8  | ACK | 8
SERVER> 9  | PRP | 2
CLIENT> 9  | ACK | 9
CLIENT> 10  | CNT | 2;dsgsgsdgdsg...
SERVER> 10  | ACK | 10
CLIENT> 11  | CNT | 3;sgsdgsdagsd...
SERVER> 11  | ACK | 11
file has been completely sent so FSF is sent by CLIENT
CLIENT> 12  | FSF | 1
SERVER> 12  | ACK | 12
Server checks the MD5 of a received file.
CLIENT> 13  | DSC | 1
SERVER> 13  | ACK | 13
Client exists.
Server exists.
```

## Why is this protocol reliable?

- It checks MD5 hash of every message and compares it with the one that is given in a package. It's almost impossible
to lose some information from ID, CMD or DATA and have a correct MD5 hash.
- It checks file MD5 sum after it's sent by a client.
- It will resend a packet if ACK message won't be delivered by another node.
- It will shift to previous file parts and send them again if server asks for that.

## Speed and progress calculations

### Speed measurement and control 
Protocol saves information about sent packets and time at which they were sent. Also, it keeps time when it started to 
send data. This way, it can calculate average speed from any time of a transfer. 

Server is able to decide about how much data it wants to be transferred per second. Then, the protocol can decide about 
time of waiting between sending packets.

### Progress measurement
Apart from speed calculations, you can also measure how much bytes have been already sent and how much are left. This 
can be simply calculated by both sides (Client & Server) basing on the information of size of the file that was 
exchanged at the beginning and current number of part that has been sent/received.

## How to start...

### Server
    java Server/Server <port of a socket> <intial sending speed value>
    
### Client
    java Client/Client <address of server> <port of a socket on server> <name of file you want to send>
    
## What was implemented in a coding solution and what is yet to be done

- [x] connection establishment
- [x] ensuring that the packet has been delivered
- [x] verifying correctness of received data
- [x] ensuring that nobody interferes between client and server communication
- [x] md5 verification of received file
- [x] sending TXT files
- [ ] creating a packet which consists of bytes read from a file, not necessarily the string which is connected with:
- [ ] sending files with any content
- [ ] speed measurement and controlling

## Bugs in current _draft_ implementation
- if `:` will appear in a file content, the packet will not be delivered correctly. Solution of that is to create a good
package and not mess up with strings.
- if last part of a file will be sent by a client and server will not be able to use last but one packet, server will 
have no time to ask client to shift sending to last but one packet, because client might disconnect till this time...
- ... and same thing happens when received file's MD5 doesn't match MD5 that was sent at the beginning. Solution for 
errors is that client will wait until server states that everything is OK and it doesn't need anything more. 

_Project was created during process of education on PJATK._