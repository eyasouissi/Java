#include "cal.h"
#include "ui_cal.h"

cal::cal(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::cal)
{
    ui->setupUi(this);
}

cal::~cal()
{
    delete ui;
}

