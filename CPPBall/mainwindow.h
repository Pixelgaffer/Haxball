#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();
    void MainWindow::updatefield(int);
	std::string MainWindow::getplayer();
private:
    Ui::MainWindow *ui;
};

#endif // MAINWINDOW_H
