#include "mainsms.h"
#include "ui_mainsms.h"

MainSms::MainSms(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainSms)
{
    ui->setupUi(this);
}

MainSms::~MainSms()
{
    delete ui;
}

