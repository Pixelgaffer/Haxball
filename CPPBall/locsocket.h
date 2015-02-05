#ifndef LOCSOCKET_H
#define LOCSOCKET_H

//Include statements for using sockets

/* all UNIX-like OSs (Linux, *BSD, MacOSX, Solaris, ...) */
#if defined(unix) || defined(__unix) || defined(__unix__)
#include <sys/socket.h>
#include <arpa/inet.h>
/* MS Windows */
#elif defined(WIN32) || defined(_WIN32) || defined(__WIN32__) || defined(__TOS_WIN__)
#include <winsock2.h>
#include <winsock2.h>
#include <Ws2tcpip.h>
/* IBM OS/2 */
#elif defined(OS2) || defined(_OS2) || defined(__OS2__) || defined(__TOS_OS2__)
#include <sys/socket.h>
#include <arpa/nameser.h>
#else
#error unsupported or unknown operating system.
#endif

#include <cstdlib>
#include <io.h>
#include <iostream>
#include <vector>
#include <string>



// Default Server
unsigned short int defaultport {8001};
std::string servip {"10.0.4.25"};
int switchv{ 0 };

// Player stats
struct player
{
	unsigned short int gid;
	std::pair <unsigned long int, unsigned long int> pos;
	char team;

};
char playerid;
std::string name {"Jython_sucks"};
std::vector<player> playerlist;


//Field
char receive;
char crap;
unsigned long int length;

int game();
void startgame(SOCKET);
int cppfoot(SOCKET);
int javahax(SOCKET);

#endif // LOCSOCKET_H

