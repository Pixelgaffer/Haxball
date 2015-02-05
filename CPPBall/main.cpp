#include "mainwindow.h"
#include <QApplication>
#include <iostream>

extern int game();

MainWindow w;


std::vector<std::pair<unsigned int, char>> loclist;

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    w.show();
    if(game()!=0)
        std::cerr << "Something wrent wrong!" << std::endl;
    return a.exec();
}

