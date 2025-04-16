#include "mainwindowlogin.h"
#include "ui_mainwindowlogin.h"

MainWindowLogin::MainWindowLogin(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindowLogin)
{
    ui->setupUi(this);
}

MainWindowLogin::~MainWindowLogin()
{
    delete ui;
}

