//
// Created by malte on 05.12.21.
//

#ifndef WIFI_SWITCH_HTTPSOCKET_HPP
#define WIFI_SWITCH_HTTPSOCKET_HPP

#include <string>
#include <stdexcept>

#include <unistd.h>     /* read(), write(), close() */
#include <string.h>     /* memcpy(), memset() */
#include <sys/socket.h> /* socket(), connect() */
#include <netinet/in.h> /* struct sockaddr_in, struct sockaddr */
#include <netdb.h>      /* struct hostent, gethostbyname() */

class SocketException : std::exception {
public:
    SocketException(const char *msg) noexcept: msg(msg) {}

    const char *what() const noexcept override {
        return msg;
    }

private:
    const char *msg;
};

class HttpSocket {

    static constexpr short PORT = 80;

public:
    HttpSocket(std::string host) {
        fd = socket(AF_INET, SOCK_STREAM, 0);

        if (fd < 0)
            throw SocketException("failed to create socket");

        struct hostent *server = gethostbyname(host.c_str());

        if (server == NULL)
            throw SocketException("no such host");


        struct sockaddr_in sockAddr;

        memset(&sockAddr, 0, sizeof(sockAddr));
        sockAddr.sin_family = AF_INET;
        sockAddr.sin_port = htons(PORT);
        memcpy(&sockAddr.sin_addr.s_addr, server->h_addr_list[0], server->h_length);

        if (connect(fd, (struct sockaddr *) &sockAddr, sizeof(sockAddr)))
            throw SocketException("failed to connect");

    }

    ~HttpSocket() {
        close(fd);
    }

public:
    void sendRequest(std::string message) {
        const char *msg = message.c_str();

        size_t total = message.size();
        size_t sent = 0;

        do {
            ssize_t bytes = write(fd, msg + sent, total - sent);
            if (bytes < 0) {
                throw SocketException("failed to write to socket");
            } else if (bytes == 0) {
                break;
            }

            sent += bytes;
        } while (sent < total);
    }

    std::string receiveResponse() {
        std::string res;

        while (true) {
            char buffer[1024];
            memset(buffer, 0, sizeof(buffer));
            size_t max_len = sizeof(buffer) - 1;
            ssize_t received = read(fd, buffer, max_len);

            if (received < 0)
                throw SocketException("failed to read from socket");
            else if (received == 0)
                return res;

            res.append(buffer, received);
        }
    }

private:
    int fd;
};


#endif //WIFI_SWITCH_HTTPSOCKET_HPP
