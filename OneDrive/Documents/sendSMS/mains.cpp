#include "mains.h"
#include "ui_mains.h"

MainS::MainS(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainS)
{
    ui->setupUi(this);
}

MainS::~MainS()
{
    delete ui;
}

