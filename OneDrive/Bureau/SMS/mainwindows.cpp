#include "mainwindows.h"
#include "ui_mainwindows.h"

MainWindowS::MainWindowS(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindowS)
{
    ui->setupUi(this);
}

MainWindowS::~MainWindowS()
{
    delete ui;
}

