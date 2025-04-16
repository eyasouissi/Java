#include "mainwindow22.h"
#include "ui_mainwindow22.h"

MainWindow22::MainWindow22(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow22)
{
    ui->setupUi(this);
}

MainWindow22::~MainWindow22()
{
    delete ui;
}

