#include "mainsms.h"

#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    MainSms w;
    w.show();
    return a.exec();
}
