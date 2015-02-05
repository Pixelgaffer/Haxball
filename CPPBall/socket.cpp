#include "locsocket.h"
#include "mainwindow.h"
#include <QApplication>

extern MainWindow w;

int game()
{

	int Socket = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (!(Socket))
	{
		std::cout << "Error at creating socket" << std::endl;
		return 1;
	}
	sockaddr_in service;
	service.sin_family = AF_INET;
	service.sin_port = htons(defaultport);
	service.sin_addr.s_addr = inet_pton(AF_INET, servip.c_str(), &service.sin_addr.s_addr);
	int result{ connect(Socket, reinterpret_cast<sockaddr*>(&service), sizeof(service)) };
	if (!(result))
	{
		std::cout << "Connection failed!" << std::endl;
		return 10;
	}
	switch (switchv)
		{
			case 0:
				cppfoot(Socket);
				return 0;
				break;
			case 1:
				javahax(Socket);
				return 0;
				break;
			default:
				std::cerr << "Error: Couldn't get one of the playing methods";
				return 100;
		};
}


int javahax(SOCKET Socket)
{

    char ch {'2'};
    char c {static_cast<const char>(name.size())};
    std::cout << "Connection built successful!" << std::endl;
    send(Socket, &ch , 1, 0);
    send(Socket, &c, name.size(), 0);
    send(Socket, name.data(), name.size(), 0);
    //Warten auf Spielfeld: Spieler-ID, Höhe, Breite, Tore
    recv(Socket, &playerid, 1, 0);
    recv(Socket, &receive, 4, 0);
    length = static_cast<unsigned int>(receive);
    recv(Socket, &receive, 4, 0);




    _close(Socket);
    closesocket(Socket);
    return 0;
}

int cppfoot(SOCKET Socket)
{
	//Send hello
	send(Socket, static_cast<const char*>("hello"), std::numeric_limits<unsigned char>::digits, 0);
	//Receive width of field
	recv(Socket, &receive, 4, 0);
	std::size_t pos = std::to_string(receive).find("=");
	std::string lengthredit = std::to_string(receive+1).substr(pos);
	//Update GUI
	w.updatefield(atoi(lengthredit.c_str()));
	//Send player + colors
	std::string pushstring{"color"};
	pushstring += w.getplayer();
	send(Socket, static_cast<const char*>(pushstring.c_str()), pushstring.size(), 0);
	recv(Socket, &crap, 5, 0);
	boolean tempb{ false };
	for (player tempplayer; recv(Socket, &receive, 1, 0) > 0;tempb = !tempb)
		{
			if (tempb == false)
				{
					tempplayer.gid = receive;
				}
			else
				{
					tempplayer.team = receive;
					playerlist.push_back(tempplayer);
				}
		}
	startgame(Socket);
	return 0;
}

void startgame(SOCKET Socket)
{
	for (;;)
	{
		recv(Socket, &crap, 6, 0);
		recv(Socket, &receive, 1, 0);

	}
	return;
}