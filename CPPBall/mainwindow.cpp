#include "mainwindow.h"
#include "ui_mainwindow.h"

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::updatefield(int length)
{
    ui->field->resize(length, length/2);
    ui->goal1->setGeometry(0,length/4,0,length-length/4);
    ui->goal2->setGeometry(length,length/4,length,length-length/4);
    ui->midline->setGeometry(length/2,0,length/2,length/2);
    return;
}

std::string MainWindow::getplayer()
{
	std::string tempstring;
	tempstring.push_back(';');
	tempstring.push_back('0 ');
	tempstring.push_back(ui->teamstate_1->currentData);
	tempstring.push_back(';');
	tempstring.push_back('1 ');
	tempstring.push_back(ui->teamstate_2->currentData);
	tempstring.push_back(';');
	tempstring.push_back('2 ');
	tempstring.push_back(ui->teamstate_3->currentData);
	tempstring.push_back(';');
	tempstring.push_back('3 ');
	tempstring.push_back(ui->teamstate_4->currentData);
	return tempstring;
}
