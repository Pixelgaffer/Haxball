#-------------------------------------------------
#
# Project created by QtCreator 2015-02-04T22:09:52
#
#-------------------------------------------------

QT       += core gui network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = CPPQTBall
TEMPLATE = app

LIBS += -lWS2_32.lib

SOURCES += main.cpp\
        mainwindow.cpp \
    socket.cpp \
    qtnet.cpp

HEADERS  += mainwindow.h \
    locsocket.h \
    qtnet.h

FORMS    += \
    mainwindow.ui


